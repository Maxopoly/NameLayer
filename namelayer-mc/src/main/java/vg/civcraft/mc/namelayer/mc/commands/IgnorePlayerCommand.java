package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.AikarCommand;
import vg.civcraft.mc.civmodcore.playersettings.impl.collection.ListSetting;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.util.ChatStrings;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

public class IgnorePlayerCommand extends AikarCommand {

	private final NameLayerSettingManager settings;

	public IgnorePlayerCommand(NameLayerPlugin plugin) {
		this.settings = plugin.getSettingsManager();
	}

	@CommandAlias("ignore|ignoreplayer|i")
	@Description("Toggles ignoring a player.")
	@CommandCompletion("@players")
	public void toggleIgnorePlayer(final Player sender, final String target) {
		final Player ignored = Bukkit.getPlayer(target);
		if (ignored == null) {
			sender.sendMessage(ChatStrings.chatPlayerNotFound);
			return;
		}
		if (Objects.equals(sender.getUniqueId(), ignored.getUniqueId())) {
			sender.sendMessage(ChatStrings.chatCantIgnoreSelf);
			return;
		}
		final ListSetting<String> ignoredPlayersSetting = this.settings.getIgnoredPlayers();
		// Player removed from the list
		if (ignoredPlayersSetting.contains(sender.getUniqueId(), target)) {
			ignoredPlayersSetting.removeElement(sender.getUniqueId(), target);
			sender.sendMessage(ChatColor.GREEN + "You are now ignoring " + target + ".");
		}
		// Player added to the list
		else {
			ignoredPlayersSetting.addElement(sender.getUniqueId(), target);
			sender.sendMessage(ChatColor.GREEN + "You stopped ignoring " + target + ".");
		}
	}

}
