package vg.civcraft.mc.namelayer.mc.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.AikarCommand;
import vg.civcraft.mc.civmodcore.playersettings.impl.collection.ListSetting;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.util.ChatStrings;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

public class IgnoreGroupCommand extends AikarCommand {

	private final NameLayerSettingManager settings;

	public IgnoreGroupCommand(final NameLayerPlugin plugin) {
		this.settings = plugin.getSettingsManager();
	}

	@CommandAlias("ignoregroup|ignoreg|igroup|ig")
	@Description("Toggles ignoring a group.")
	@CommandCompletion("@readablegroups")
	public void toggleIgnoreGroup(final Player sender, final String target) {
		final Group group = GroupAPI.getGroup(target);
		if (group == null) {
			sender.sendMessage(ChatStrings.chatGroupNotFound);
			return;
		}
		final UUID ignorerUUID = sender.getUniqueId();
		final ListSetting<String> ignoredGroupsSetting = this.settings.getIgnoredGroups();
		// Group removed from the list
		if (ignoredGroupsSetting.contains(ignorerUUID, target)) {
			ignoredGroupsSetting.removeElement(ignorerUUID, target);
			sender.sendMessage(ChatColor.GREEN + "You stopped ignoring " + target + ".");
		}
		// Group added to the list
		else {
			ignoredGroupsSetting.addElement(ignorerUUID, target);
			sender.sendMessage(ChatColor.GREEN + "You are now ignoring " + target + ".");
		}
	}

	@TabComplete("readablegroups")
	public List<String> groupTabComplete(final BukkitCommandCompletionContext context) {
		// TODO: Update this eventually to ONLY be readable groups
		return NameLayerTabCompletion.completeGroupName(context.getInput(), context.getPlayer());
	}

}
