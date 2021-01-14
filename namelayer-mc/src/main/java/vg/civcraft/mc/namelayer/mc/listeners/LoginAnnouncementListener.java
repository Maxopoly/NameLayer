package vg.civcraft.mc.namelayer.mc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

public class LoginAnnouncementListener implements Listener {

	private final NameLayerSettingManager settings;

	public LoginAnnouncementListener(NameLayerPlugin plugin) {
		this.settings = plugin.getSettingsManager();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		event.setJoinMessage(null);
		for (final Player receiver : Bukkit.getOnlinePlayers()) {
			if (this.settings.getShowJoins(receiver.getUniqueId())) {
				receiver.sendMessage(event.getPlayer().getName() +
						ChatColor.YELLOW + " has joined the game");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		event.setQuitMessage(null);
		final Player leaver = event.getPlayer();
		for (final Player receiver : Bukkit.getOnlinePlayers()) {
			if (this.settings.getShowJoins(receiver.getUniqueId())) {
				receiver.sendMessage(leaver.getName() +
						ChatColor.YELLOW + " has left the game");
			}
		}
	}

}
