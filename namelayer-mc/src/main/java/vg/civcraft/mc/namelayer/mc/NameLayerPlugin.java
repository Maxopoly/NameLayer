package vg.civcraft.mc.namelayer.mc;

import org.bukkit.Bukkit;

import com.github.maxopoly.artemis.ArtemisPlugin;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupTracker;
import vg.civcraft.mc.namelayer.core.NameLayerMetaData;
import vg.civcraft.mc.namelayer.core.log.abstr.GroupActionLogFactory;
import vg.civcraft.mc.namelayer.core.requests.AcceptInvite;
import vg.civcraft.mc.namelayer.core.requests.BlacklistPlayer;
import vg.civcraft.mc.namelayer.core.requests.ChangeGroupColor;
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
import vg.civcraft.mc.namelayer.core.requests.UnblacklistPlayer;
import vg.civcraft.mc.namelayer.core.requests.UnlinkGroups;
import vg.civcraft.mc.namelayer.mc.listeners.ChatListener;
import vg.civcraft.mc.namelayer.mc.listeners.LoginAnnouncementListener;
import vg.civcraft.mc.namelayer.mc.listeners.MurderBroadcastListener;
import vg.civcraft.mc.namelayer.mc.model.AikarCommandRegistrar;
import vg.civcraft.mc.namelayer.mc.model.ChatTracker;
import vg.civcraft.mc.namelayer.mc.model.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.AddInvite;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.AddMember;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.AddPermission;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.AddToActionLog;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.GroupMetaDataUpdate;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.RecacheGroup;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.RemoveInvite;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.RemovePermission;
import vg.civcraft.mc.namelayer.mc.rabbit.executions.SendLocalMessage;
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
	private GroupActionLogFactory actionLogFactory;

	public void onEnable() {
		instance = this;
		super.onEnable();
		this.nlConfig = new NameLayerConfig(this);
		if (!this.nlConfig.parse()) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		groupTracker = new GroupTracker();
		this.actionLogFactory = new GroupActionLogFactory();
		Group.setActionLogFactory(this.actionLogFactory);
		nameLayerPermManager = new NameLayerPermissionManager(groupTracker.getPermissionTracker());
		ArtemisPlugin.getInstance().getRabbitInputHandler().deferCommandToStandardRequest(AcceptInvite.REPLY_ID,
				BlacklistPlayer.REPLY_ID, CreateGroup.REPLY_ID, CreateRank.REPLY_ID, DeleteGroup.REPLY_ID,
				DeleteRank.REPLY_ID, EditPermission.REPLY_ID, InvitePlayer.REPLY_ID, JoinGroup.REPLY_ID,
				LeaveGroup.REPLY_ID, LinkGroups.REPLY_ID, MergeGroups.REPLY_ID, PromotePlayer.REPLY_ID,
				RejectInvite.REPLY_ID, RemoveMember.REPLY_ID, RenameGroup.REPLY_ID, RenameRank.REPLY_ID,
				RevokeInvite.REPLY_ID, SetPassword.REPLY_ID, UnlinkGroups.REPLY_ID, RegisterPermission.REPLY_ID,
				SendGroupChatMessage.REPLY_ID, SendPrivateMessage.REPLY_ID, ChangeGroupColor.REPLY_ID,
				UnblacklistPlayer.REPLY_ID);
		settingsManager = new NameLayerSettingManager();
		GroupAPI.registerMetaDataDefault(NameLayerMetaData.CHAT_COLOR_KEY, ChatColor.WHITE.toString());
		chatTracker = new ChatTracker(settingsManager);
		ArtemisPlugin.getInstance().getRabbitInputHandler().registerCommand(new AddInvite(), new AddMember(),
				new AddPermission(), new vg.civcraft.mc.namelayer.mc.rabbit.executions.CreateRank(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.DeleteGroup(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.DeleteRank(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.LinkGroups(), new RecacheGroup(), new RemoveInvite(),
				new RemovePermission(), new vg.civcraft.mc.namelayer.mc.rabbit.executions.RemoveMember(), new vg.civcraft.mc.namelayer.mc.rabbit.executions.RenameGroup(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.RenameRank(), new vg.civcraft.mc.namelayer.mc.rabbit.executions.MergeGroups(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.UnlinkGroups(), new UpdateMemberRank(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.SendGroupChatMessage(),
				new vg.civcraft.mc.namelayer.mc.rabbit.executions.SendPrivateMessage(), new SendLocalMessage(),
				new GroupMetaDataUpdate(), new AddToActionLog());
		new AikarCommandRegistrar(this);
		registerListener(new ChatListener(this));
		registerListener(new LoginAnnouncementListener(this));
		registerListener(new MurderBroadcastListener(this));
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
	
	public GroupActionLogFactory getActionLogFactory() {
		return actionLogFactory;
	}

	public ChatTracker getChatTracker() {
		return chatTracker;
	}

	public NameLayerPermissionManager getNameLayerPermissionManager() {
		return nameLayerPermManager;
	}
}
