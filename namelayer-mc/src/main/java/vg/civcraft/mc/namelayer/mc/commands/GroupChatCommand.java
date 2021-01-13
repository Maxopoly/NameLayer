package vg.civcraft.mc.namelayer.mc.commands;

import java.util.List;

import org.bukkit.entity.Player;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import vg.civcraft.mc.civmodcore.command.AikarCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.ChatTracker;
import vg.civcraft.mc.namelayer.mc.model.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.mc.model.chat.ChatMode;
import vg.civcraft.mc.namelayer.mc.model.chat.GroupChatMode;
import vg.civcraft.mc.namelayer.mc.util.ChatStrings;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

@Description("Switches (or sends message) to a group chat.")
public class GroupChatCommand extends AikarCommand {

	private static final String ALIAS = "group|groupc|groupchat|gchat|gc|g";

	private final ChatTracker modeManager;
	private final NameLayerSettingManager settings;
	private final NameLayerPermissionManager permissions;

	public GroupChatCommand(final NameLayerPlugin plugin) {
		this.settings = plugin.getSettingsManager();
		this.permissions = plugin.getNameLayerPermissionManager();
		this.modeManager = plugin.getChatTracker();
	}

	/**
	 * Zero argument command
	 */
	@CommandAlias(ALIAS)
	public void toggleGroupChat(final Player sender) {
		final ChatMode currentMode = this.modeManager.getChatMode(sender);
		if (currentMode instanceof GroupChatMode) {
			this.modeManager.resetChatMode(sender);
		}
		else {
			final Group defaultGroup = this.settings.getDefaultGroup().getGroup(sender);
			if (defaultGroup == null) {
				// todo tell them no default group
				return;
			}
			this.modeManager.setChatMode(sender, new GroupChatMode(defaultGroup.getPrimaryId()), true);
		}
	}

	@CommandAlias(ALIAS)
	@CommandCompletion("@writeablegroups")
	public void switchToGroupChat(final Player sender, final String name) {
		final Group group = GroupAPI.getGroup(name);
		if (group == null) {
			sender.sendMessage(ChatStrings.chatGroupNotFound);
			return;
		}
		this.modeManager.setChatMode(sender, new GroupChatMode(group.getPrimaryId()), true);
	}

	@CommandAlias(ALIAS)
	@CommandCompletion("@writeablegroups @nothing")
	public void sendOneOffMessageToGroup(final Player sender, final String name, final String message) {
		final Group group = GroupAPI.getGroup(name);
		if (group == null) {
			sender.sendMessage(ChatStrings.chatGroupNotFound);
			return;
		}
		GroupChatMode.sendGroupMessage(sender, group, message);
	}

	@TabComplete("writeablegroups")
	public List<String> groupTabComplete(final BukkitCommandCompletionContext context) {
		// TODO: Update this eventually to ONLY be writable groups
		return NameLayerTabCompletion.completeGroupName(context.getInput(), context.getPlayer());
	}

}
