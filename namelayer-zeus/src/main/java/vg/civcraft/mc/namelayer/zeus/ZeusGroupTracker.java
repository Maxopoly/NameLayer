package vg.civcraft.mc.namelayer.zeus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.rabbit.RabbitMessage;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupTracker;
import vg.civcraft.mc.namelayer.core.IllegalGroupStateException;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.AddMemberMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.ChangeMemberRankMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RemoveInviteMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RemoveMemberMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RenameGroupMessage;

public class ZeusGroupTracker extends GroupTracker {

	private GroupManagerDao database;

	public ZeusGroupTracker(GroupManagerDao database) {
		super();
		this.database = database;
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
			sendGroupUpdate(new RenameGroupMessage(group.getPrimaryId(), newName));
		}
	}
	
	@Override
	public void updatePlayerRankInGroup(Group group, UUID player, GroupRank rank) {
		synchronized (group) {
			super.updatePlayerRankInGroup(group, player, rank);
			database.updateMember(player, group, rank);
			sendGroupUpdate(new ChangeMemberRankMessage(group.getPrimaryId(), player, rank.getId()));
		}
	}
	
	public void removePlayerFromGroup(Group group, UUID player) {
		synchronized (group) {
			super.removePlayerFromGroup(group, player);
			database.removeMember(player, group);
			sendGroupUpdate(new RemoveMemberMessage(group.getPrimaryId(), player));
		}
	}

	public Group createGroup(String name, UUID creator) {
		return null; // TODO
	}

	public void blacklistPlayer(Group group, UUID player, GroupRank rank) {
		addPlayerToGroup(group, player, rank);
	}

	public GroupRank createRank(Group group, String name, GroupRank parent) {
		return null; //TODO 	
	}

	private static void sendGroupUpdate(RabbitMessage msg) {
		ZeusMain.getInstance().getBroadcastInterestTracker().broadcastMessage(msg);
	}

	public void deleteInvite(Group group, UUID player) {
		synchronized (group) {
			super.deleteInvite(group, player);
			database.removeGroupInvitation(player, group);
			sendGroupUpdate(new RemoveInviteMessage(group.getPrimaryId(), player));
		}
	}

	public void addPlayerToGroup(Group group, UUID player, GroupRank rank) {
		synchronized (group) {
			super.addPlayerToGroup(group, player, rank);
			database.addMember(player, group, rank);
			sendGroupUpdate(new AddMemberMessage(group.getPrimaryId(), player, rank.getId()));
		}
	}

}
