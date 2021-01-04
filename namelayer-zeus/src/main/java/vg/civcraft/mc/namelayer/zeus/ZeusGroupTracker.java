package vg.civcraft.mc.namelayer.zeus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupLink;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.GroupTracker;
import vg.civcraft.mc.namelayer.core.IllegalGroupStateException;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddInviteMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddLinkMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddMemberMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddPermissionMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.ChangeMemberRankMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.CreateRankMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.DeleteGroupMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.DeleteRankMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.MergeGroupMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RecacheGroupMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RemoveInviteMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RemoveMemberMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RemovePermissionMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RenameGroupMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RenameRankMessage;

public class ZeusGroupTracker extends GroupTracker {

	private NameLayerDAO database;
	private Map<Integer, Object> groupsBeingLoaded;

	public ZeusGroupTracker(NameLayerDAO database) {
		super();
		this.database = database;
		this.groupsBeingLoaded = new HashMap<>();
	}
	
	@Override
	public void registerPermission(PermissionType perm) {
		synchronized (permissionTracker) {
			if (permissionTracker.getPermission(perm.getName()) != null) {
				return;
			}
			database.registerPermission(perm);
			super.registerPermission(perm);
		}
	}

	@Override
	public void addInvite(UUID toInvite, GroupRank rank, Group group) {
		synchronized (group) {
			super.addInvite(toInvite, rank, group);
			database.addGroupInvitation(toInvite, group, rank);
			sendGroupUpdate(group, () -> new AddInviteMessage(group.getPrimaryId(), toInvite, rank.getId()));
		}
	}

	@Override
	public void addPermissionToRank(Group group, GroupRank rank, PermissionType permission) {
		synchronized (group) {
			super.addPermissionToRank(group, rank, permission);
			database.addPermission(group, rank, permission);
			sendGroupUpdate(group,
					() -> new AddPermissionMessage(group.getPrimaryId(), rank.getId(), permission.getId()));
		}
	}

	@Override
	public void removePermissionFromRank(Group group, GroupRank rank, PermissionType permission) {
		synchronized (group) {
			super.removePermissionFromRank(group, rank, permission);
			database.removePermission(group, rank, permission);
			sendGroupUpdate(group,
					() -> new RemovePermissionMessage(group.getPrimaryId(), rank.getId(), permission.getId()));
		}
	}

	public void acceptInvite(Group group, UUID player) {
		synchronized (group) {
			GroupRank rank = group.getInvite(player);
			deleteInvite(group, player);
			addPlayerToGroup(group, player, rank);
		}
	}

	@Override
	public void renameGroup(Group group, String newName) {
		synchronized (group) {
			super.renameGroup(group, newName);
			database.renameGroup(group, newName);
			sendGroupUpdate(group, () -> new RenameGroupMessage(group.getPrimaryId(), newName));
		}
	}

	@Override
	public void setMetaDataValue(Group group, String key, String value) {
		synchronized (group) {
			super.setMetaDataValue(group, key, value);
		}
	}

	@Override
	public void renameRank(Group group, GroupRank rank, String newName) {
		synchronized (group) {
			super.renameRank(group, rank, newName);
			database.updateRankName(group, rank);
			sendGroupUpdate(group, () -> new RenameRankMessage(group.getPrimaryId(), rank.getId(), rank.getName()));
		}
	}

	@Override
	public void updatePlayerRankInGroup(Group group, UUID player, GroupRank rank) {
		synchronized (group) {
			super.updatePlayerRankInGroup(group, player, rank);
			database.updateMember(player, group, rank);
			sendGroupUpdate(group, () -> new ChangeMemberRankMessage(group.getPrimaryId(), player, rank.getId()));
		}
	}

	@Override
	public void removePlayerFromGroup(Group group, UUID player) {
		synchronized (group) {
			super.removePlayerFromGroup(group, player);
			database.removeMember(player, group);
			sendGroupUpdate(group, () -> new RemoveMemberMessage(group.getPrimaryId(), player));
		}
	}

	@Override
	public void deleteRank(Group group, GroupRank rank) {
		synchronized (group) {
			super.deleteRank(group, rank);
			database.deleteRank(group, rank);
			sendGroupUpdate(group, () -> new DeleteRankMessage(group.getPrimaryId(), rank.getId()));
		}
	}

	@Override
	public void deleteGroup(Group group) {
		synchronized (group) {
			super.deleteGroup(group);
			database.deleteGroup(group);
			sendGroupUpdate(group, () -> new DeleteGroupMessage(group.getPrimaryId()));
		}
	}

	public Group loadOrGetGroup(int id) {
		Group result;
		synchronized (groupsBeingLoaded) {
			result = getGroup(id);
			if (result != null) {
				return result;
			}
			Object loadingKey = groupsBeingLoaded.get(id);
			if (loadingKey != null) {
				// already being loaded, we will block until the loading is done
				synchronized (loadingKey) {
					while (groupsBeingLoaded.containsKey(id)) {
						try {
							loadingKey.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					return getGroup(id);
				}
			}
			groupsBeingLoaded.put(id, new Object());
		}
		Group group = database.getGroup(id, getPermissionTracker());
		addGroup(group);
		Object lock = groupsBeingLoaded.remove(id);
		synchronized (lock) {
			lock.notifyAll();
		}
		return group;
	}

	public synchronized Group createGroup(String name, UUID creator) {
		int groupID = database.createGroup(name, creator);
		if (groupID == -1) {
			return null;
		}
		Group group = new Group(name, groupID);
		synchronized (group) {
			Map<GroupRank, List<PermissionType>> permsToSave = new HashMap<>();
			GroupRank owner = new GroupRank("Owner", GroupRankHandler.OWNER_ID, null);
			GroupRankHandler handler = new GroupRankHandler(owner);
			GroupRank admin = new GroupRank("Admin", GroupRankHandler.DEFAULT_ADMIN_ID, owner);
			handler.putRank(admin);
			GroupRank mod = new GroupRank("Mod", GroupRankHandler.DEFAULT_MOD_ID, admin);
			handler.putRank(mod);
			GroupRank member = new GroupRank("Member", GroupRankHandler.DEFAULT_MEMBER_ID, mod);
			handler.putRank(member);
			GroupRank defaultNonMember = new GroupRank("Default", GroupRankHandler.DEFAULT_NON_MEMBER_ID, owner);
			handler.putRank(defaultNonMember);
			GroupRank blacklisted = new GroupRank("Blacklisted", GroupRankHandler.DEFAULT_BLACKLIST_ID,
					defaultNonMember);
			handler.putRank(blacklisted);
			for (GroupRank rank : handler.getAllRanks()) {
				if (rank == owner) {
					continue;
				}
				List<PermissionType> permList = new ArrayList<>();
				for (PermissionType perm : getPermissionTracker().getAllPermissions()) {
					if (perm.getDefaultPermLevels().getAllowedRankIds().contains(rank.getId())) {
						rank.addPermission(perm);
						permList.add(perm);
					}
				}
				permsToSave.put(rank, permList);
			}
			handler.setDefaultPasswordJoinRank(member);
			handler.setDefaultInvitationRank(member);
			database.addAllPermissions(groupID, permsToSave);
			addGroup(group);
		}
		return group;
	}

	public void blacklistPlayer(Group group, UUID player, GroupRank rank) {
		addPlayerToGroup(group, player, rank);
	}

	public GroupRank createRank(Group group, String name, GroupRank parent) {
		synchronized (group) {
			int id = -1;
			for (int i = GroupRankHandler.OWNER_ID; i < GroupRankHandler.MAXIMUM_TYPE_COUNT; i++) {
				if (group.getGroupRankHandler().getRank(i) == null) {
					id = i;
					break;
				}
			}
			if (id < 0) {
				return null;
			}
			GroupRank rank = new GroupRank(name, id, parent);
			group.getGroupRankHandler().createNewRank(rank);
			database.createRank(group, rank);
			sendGroupUpdate(group,
					() -> new CreateRankMessage(group.getPrimaryId(), rank.getId(), rank.getName(), parent.getId()));
			return rank;
		}
	}

	public void mergeGroups(Group toKeep, Group toRemove) {
		if (toKeep.equals(toRemove)) {
			throw new IllegalGroupStateException();
		}
		// Acquire locks in id order to avoid deadlocks
		Group lowerID = toKeep.getPrimaryId() < toRemove.getPrimaryId() ? toKeep : toRemove;
		Group upperID = toKeep.getPrimaryId() < toRemove.getPrimaryId() ? toRemove : toKeep;
		synchronized (lowerID) {
			synchronized (upperID) {
				if (!toRemove.getIncomingLinks().isEmpty()) {
					throw new IllegalGroupStateException();
				}
				if (!toRemove.getOutgoingLinks().isEmpty()) {
					throw new IllegalGroupStateException();
				}
				deleteGroup(toRemove);
				// ensure both groups exist on all clients that have one or the other
				sendGroupUpdate(toRemove, () -> new RecacheGroupMessage(toKeep));
				sendGroupUpdate(toKeep, () -> new RecacheGroupMessage(toRemove));
				// Clients will issue delete based on the merge message and add the appropriate
				// secondary id, we do not send it explicitly
				sendGroupUpdate(toRemove, () -> new MergeGroupMessage(toRemove.getPrimaryId(), toKeep.getPrimaryId()));
			}
		}
	}

	private static void sendGroupUpdate(Group group, Supplier<RabbitMessage> msg) {
		NameLayerZPlugin.getInstance().getGroupKnowledgeTracker().sendToInterestedServers(group, msg);
	}

	@Override
	public void deleteInvite(Group group, UUID player) {
		synchronized (group) {
			super.deleteInvite(group, player);
			database.removeGroupInvitation(player, group);
			sendGroupUpdate(group, () -> new RemoveInviteMessage(group.getPrimaryId(), player));
		}
	}

	@Override
	public void addPlayerToGroup(Group group, UUID player, GroupRank rank) {
		synchronized (group) {
			super.addPlayerToGroup(group, player, rank);
			database.addMember(player, group, rank);
			sendGroupUpdate(group, () -> new AddMemberMessage(group.getPrimaryId(), player, rank.getId()));
		}
	}

	@Override
	public GroupLink linkGroups(Group originating, GroupRank originatingType, Group target, GroupRank targetType) {
		// Acquire locks in id order to avoid deadlocks
		Group lowerID = originating.getPrimaryId() < target.getPrimaryId() ? originating : target;
		Group upperID = originating.getPrimaryId() < target.getPrimaryId() ? target : originating;
		synchronized (lowerID) {
			synchronized (upperID) {
				GroupLink link = super.linkGroups(originating, originatingType, target, targetType);
				database.addLink(link);
				sendGroupUpdate(originating, () -> new AddLinkMessage(originating.getPrimaryId(),
						originatingType.getId(), target.getPrimaryId(), targetType.getId()));
				return link;
			}
		}
	}

	@Override
	public void deleteGroupLink(GroupLink link) {
		// Acquire locks in id order to avoid deadlocks
		Group originating = link.getOriginatingGroup();
		Group target = link.getTargetGroup();
		Group lowerID = originating.getPrimaryId() < target.getPrimaryId() ? originating : target;
		Group upperID = originating.getPrimaryId() < target.getPrimaryId() ? target : originating;
		synchronized (lowerID) {
			synchronized (upperID) {
				super.deleteGroupLink(link);
				database.removeLink(link);
				sendGroupUpdate(originating, () -> new AddLinkMessage(originating.getPrimaryId(),
						link.getOriginatingRank().getId(), target.getPrimaryId(), link.getTargetRank().getId()));
			}
		}
	}

}
