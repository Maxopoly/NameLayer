package vg.civcraft.mc.namelayer.zeus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupLink;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.GroupTracker;
import vg.civcraft.mc.namelayer.core.IllegalGroupStateException;
import vg.civcraft.mc.namelayer.core.NameLayerMetaData;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;
import vg.civcraft.mc.namelayer.core.log.impl.AcceptInvitation;
import vg.civcraft.mc.namelayer.core.log.impl.InviteMember;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddInviteMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddLinkMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddMemberMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddPermissionMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddToActionLogMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.ChangeMemberRankMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.CreateRankMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.DeleteGroupMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.DeleteRankMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.GroupMetaDataChangeMessage;
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
	private Map<String, String> defaultMetaData;

	public ZeusGroupTracker(NameLayerDAO database) {
		super();
		this.database = database;
		this.defaultMetaData = new ConcurrentHashMap<>();
		this.groupsBeingLoaded = new HashMap<>();
	}

	public void registerDefaultMetaData(String key, String value) {
		this.defaultMetaData.put(key, value);
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
			// first db access, because super call will already overwrite old name
			database.renameGroup(group, newName);
			super.renameGroup(group, newName);
			sendGroupUpdate(group, () -> new RenameGroupMessage(group.getPrimaryId(), newName));
		}
	}

	@Override
	public void setMetaDataValue(Group group, String key, String value) {
		synchronized (group) {
			super.setMetaDataValue(group, key, value);
			database.setGroupMetaData(group, key, value);
			sendGroupUpdate(group, () -> new GroupMetaDataChangeMessage(group.getPrimaryId(), key, value));
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

	@Override
	public void addLogEntry(Group group, LoggedGroupAction action) {
		synchronized (group) {
			super.addLogEntry(group, action);
			database.insertActionLog(group, action);
			sendGroupUpdate(group, () -> new AddToActionLogMessage(group.getPrimaryId(), action));
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
		if (group != null) {
			addGroup(group);
		}
		Object lock = groupsBeingLoaded.remove(id);
		synchronized (lock) {
			lock.notifyAll();
		}
		return group;
	}

	public Group loadOrGetGroup(String name) {
		int id = database.getGroupIdForName(name);
		if (id == -1) {
			return null;
		}
		return loadOrGetGroup(id);
	}

	public synchronized Group createGroup(String name, UUID creator) {
		Group group = database.createGroup(name, creator, getPermissionTracker().getAllPermissions());
		if (group == null) {
			return null;
		}
		synchronized (group) {
			addGroup(group);
			GroupRank owner = group.getGroupRankHandler().getOwnerRank();
			addPlayerToGroup(group, creator, owner);
			for (Entry<String, String> entry : defaultMetaData.entrySet()) {
				group.setMetaData(entry.getKey(), entry.getValue());
				database.setGroupMetaData(group, entry.getKey(), entry.getValue());
			}
			String now = String.valueOf(System.currentTimeMillis());
			group.setMetaData(NameLayerMetaData.CREATION_TIME_KEY, now);
			database.setGroupMetaData(group, NameLayerMetaData.CREATION_TIME_KEY, now);
			group.setMetaData(NameLayerMetaData.CREATOR_KEY, creator.toString());
			database.setGroupMetaData(group, NameLayerMetaData.CREATOR_KEY, creator.toString());
			return group;
		}
	}

	public void blacklistPlayer(Group group, UUID player, GroupRank rank) {
		addPlayerToGroup(group, player, rank);
	}

	public void unBlacklistPlayer(Group group, UUID player) {
		removePlayerFromGroup(group, player);
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
