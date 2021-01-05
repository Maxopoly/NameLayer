package vg.civcraft.mc.namelayer.mc;

import com.github.maxopoly.artemis.ArtemisPlugin;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.namelayer.core.GroupTracker;
import vg.civcraft.mc.namelayer.core.requests.AcceptInvite;
import vg.civcraft.mc.namelayer.core.requests.BlacklistPlayer;
import vg.civcraft.mc.namelayer.core.requests.CreateGroup;
import vg.civcraft.mc.namelayer.core.requests.CreateRank;
import vg.civcraft.mc.namelayer.core.requests.DeleteGroup;
import vg.civcraft.mc.namelayer.core.requests.DeleteRank;
import vg.civcraft.mc.namelayer.core.requests.EditPermission;
import vg.civcraft.mc.namelayer.core.requests.InvitePlayer;
import vg.civcraft.mc.namelayer.core.requests.JoinGroup;
import vg.civcraft.mc.namelayer.core.requests.LeaveGroup;
import vg.civcraft.mc.namelayer.core.requests.LinkGroups;
import vg.civcraft.mc.namelayer.core.requests.MergeGroups;
import vg.civcraft.mc.namelayer.core.requests.PromotePlayer;
import vg.civcraft.mc.namelayer.core.requests.RegisterPermission;
import vg.civcraft.mc.namelayer.core.requests.RejectInvite;
import vg.civcraft.mc.namelayer.core.requests.RemoveMember;
import vg.civcraft.mc.namelayer.core.requests.RenameGroup;
import vg.civcraft.mc.namelayer.core.requests.RenameRank;
import vg.civcraft.mc.namelayer.core.requests.RevokeInvite;
import vg.civcraft.mc.namelayer.core.requests.SetPassword;
import vg.civcraft.mc.namelayer.core.requests.UnlinkGroups;
import vg.civcraft.mc.namelayer.mc.model.NameLayerPermissionManager;

public class NameLayerPlugin extends ACivMod {

	private static NameLayerPlugin instance;

	public static NameLayerPlugin getInstance() {
		return instance;
	}

	private GroupTracker groupTracker;
	private NameLayerPermissionManager nameLayerPermManager;

	public void onEnable() {
		instance = this;
		super.onEnable();
		groupTracker = new GroupTracker();
		nameLayerPermManager = new NameLayerPermissionManager();
		ArtemisPlugin.getInstance().getRabbitInputHandler().deferCommandToStandardRequest(AcceptInvite.REPLY_ID,
				BlacklistPlayer.REPLY_ID, CreateGroup.REPLY_ID, CreateRank.REPLY_ID, DeleteGroup.REPLY_ID,
				DeleteRank.REPLY_ID, EditPermission.REPLY_ID, InvitePlayer.REPLY_ID, JoinGroup.REPLY_ID,
				LeaveGroup.REPLY_ID, LinkGroups.REPLY_ID, MergeGroups.REPLY_ID, PromotePlayer.REPLY_ID,
				RejectInvite.REPLY_ID, RemoveMember.REPLY_ID, RenameGroup.REPLY_ID, RenameRank.REPLY_ID,
				RevokeInvite.REPLY_ID, SetPassword.REPLY_ID, UnlinkGroups.REPLY_ID, RegisterPermission.REPLY_ID);
	}

	public GroupTracker getGroupTracker() {
		return groupTracker;
	}

	public NameLayerPermissionManager getNameLayerPermissionManager() {
		return nameLayerPermManager;
	}
}
