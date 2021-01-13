package vg.civcraft.mc.namelayer.mc.commands;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.AikarCommand;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

public class IgnoreListCommand extends AikarCommand {

	private final NameLayerSettingManager settings;

	public IgnoreListCommand(final NameLayerPlugin plugin) {
		this.settings = plugin.getSettingsManager();
	}

	@CommandAlias("ignorelist|il")
	@Description("Lists the players and groups you are ignoring.")
	public void printAllIgnores(final Player sender) {
		// Send all ignored players
		final List<String> ignoredPlayers = this.settings.getIgnoredPlayers().getValue(sender);
		final String ignoredPlayersPreface = ChatColor.GREEN + "Ignored Players: ";
		if (CollectionUtils.isEmpty(ignoredPlayers)) {
			sender.sendMessage(ignoredPlayersPreface + "<none>");
		}
		else {
			sender.sendMessage(ignoredPlayersPreface + ChatColor.RESET +
					StringUtils.join(ignoredPlayers, ", "));
		}
		// Send all ignored groups
		final List<String> ignoredGroups = this.settings.getIgnoredGroups().getValue(sender);
		final String ignoredGroupsPreface = ChatColor.GREEN + "Ignored Groups: ";
		if (CollectionUtils.isEmpty(ignoredGroups)) {
			sender.sendMessage(ignoredPlayersPreface + "<none>");
		}
		else {
			sender.sendMessage(ignoredGroupsPreface + ChatColor.RESET +
					StringUtils.join(ignoredGroups, ", "));
		}
	}

}
