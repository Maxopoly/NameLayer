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
import vg.civcraft.mc.namelayer.core.requests.SendGroupChatMessage;
import vg.civcraft.mc.namelayer.core.requests.SendPrivateMessage;
import vg.civcraft.mc.namelayer.core.requests.SetPassword;
import vg.civcraft.mc.namelayer.core.requests.UnlinkGroups;
import vg.civcraft.mc.namelayer.mc.model.ChatTracker;
import vg.civcraft.mc.namelayer.mc.model.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.AddInvite;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.AddMember;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.AddPermission;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.RecacheGroup;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.RemoveInvite;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.RemovePermission;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.UpdateMemberRank;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

public class NameLayerPlugin extends ACivMod {

	private static NameLayerPlugin instance;

	public static NameLayerPlugin getInstance() {
		return instance;
	}

	private GroupTracker groupTracker;
	private NameLayerPermissionManager nameLayerPermManager;
	private NameLayerSettingManager settingsManager;
	private ChatTracker chatTracker;
	private NameLayerConfig nlConfig;

	public void onEnable() {
		instance = this;
		super.onEnable();
		groupTracker = new GroupTracker();
		nameLayerPermManager = new NameLayerPermissionManager(groupTracker.getPermissionTracker());
		ArtemisPlugin.getInstance().getRabbitInputHandler().deferCommandToStandardRequest(AcceptInvite.REPLY_ID,
				BlacklistPlayer.REPLY_ID, CreateGroup.REPLY_ID, CreateRank.REPLY_ID, DeleteGroup.REPLY_ID,
				DeleteRank.REPLY_ID, EditPermission.REPLY_ID, InvitePlayer.REPLY_ID, JoinGroup.REPLY_ID,
				LeaveGroup.REPLY_ID, LinkGroups.REPLY_ID, MergeGroups.REPLY_ID, PromotePlayer.REPLY_ID,
				RejectInvite.REPLY_ID, RemoveMember.REPLY_ID, RenameGroup.REPLY_ID, RenameRank.REPLY_ID,
				RevokeInvite.REPLY_ID, SetPassword.REPLY_ID, UnlinkGroups.REPLY_ID, RegisterPermission.REPLY_ID,
				SendGroupChatMessage.REPLY_ID, SendPrivateMessage.REPLY_ID);
		settingsManager = new NameLayerSettingManager();
		chatTracker = new ChatTracker(this);
		ArtemisPlugin.getInstance().getRabbitInputHandler().registerCommand(new AddInvite(), new AddMember(),
				new AddPermission(), new vg.civcraft.mc.namelayer.mc.rabbit.executions.CreateRank(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.DeleteGroup(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.DeleteRank(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.LinkGroups(), new RecacheGroup(), new RemoveInvite(),
				new RemovePermission(), new vg.civcraft.mc.namelayer.mc.rabbit.executions.RenameGroup(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.RenameRank(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.UnlinkGroups(), new UpdateMemberRank());
	}

	public NameLayerSettingManager getSettingsManager() {
		return settingsManager;
	}

	public GroupTracker getGroupTracker() {
		return groupTracker;
	}

	public NameLayerConfig getNameLayerConfig() {
		return nlConfig;
	}

	public ChatTracker getChatTracker() {
		return chatTracker;
	}

	public NameLayerPermissionManager getNameLayerPermissionManager() {
		return nameLayerPermManager;
	}
}
