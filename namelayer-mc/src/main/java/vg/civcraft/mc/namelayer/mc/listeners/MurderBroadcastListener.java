package vg.civcraft.mc.namelayer.mc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.api.ItemNames;
import vg.civcraft.mc.civmodcore.util.TextUtil;
import vg.civcraft.mc.namelayer.mc.NameLayerConfig;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

public class MurderBroadcastListener implements Listener {
	
	private final NameLayerSettingManager settings;
	private final NameLayerConfig config;

	public MurderBroadcastListener(NameLayerPlugin plugin) {
		this.settings = plugin.getSettingsManager();
		this.config = plugin.getNameLayerConfig();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKill(final PlayerDeathEvent event) {
		final Player victim = event.getEntity();
		final Player killer = victim.getKiller();
		// Player died from non-player reasons
		if (killer == null) {
			return;
		}
		// If the killer is not a sooth brain, then prevent broadcast
		if (!this.settings.getSendOwnKills(killer.getUniqueId())) {
			return;
		}
		// If the range is 0 then disable broadcasts
		final int range = this.config.getKillBroadcastRange();
		if (range == 0) {
			return;
		}
		// Generate announcement text
		final ItemStack weapon = killer.getInventory().getItemInMainHand();
		final String weaponText = ItemAPI.isValidItem(weapon) ? "by hand" :
				"with " + ItemNames.getItemName(weapon);
		final TextComponent broadcast = TextUtil.textComponent("", ChatColor.GOLD);
		broadcast.addExtra(TextUtil.textComponent(victim.getDisplayName()));
		broadcast.addExtra(TextUtil.textComponent(" was killed by "));
		broadcast.addExtra(TextUtil.textComponent(killer.getDisplayName()));
		broadcast.addExtra(TextUtil.textComponent(weaponText));
		//
		final String killersName = killer.getName();
		final Location deathLocation = victim.getLocation();
		for (final Player recipient : Bukkit.getOnlinePlayers()) {
			if (range > 0) {
				if (!recipient.getWorld().equals(deathLocation.getWorld())) {
					continue;
				}
				final int distance = (int) deathLocation.distance(recipient.getLocation());
				if (distance < 0 || distance > this.config.getKillBroadcastRange()) {
					continue;
				}
			}
			if (!this.settings.getReceiveKills(recipient.getUniqueId())) {
				continue;
			}
			if (!this.settings.getReceiveKillsFromIgnored(recipient.getUniqueId())
					&& this.settings.getIgnoredPlayers().contains(recipient.getUniqueId(), killersName)) {
				continue;
			}
			recipient.sendMessage(broadcast.getText());
		}
	}

}
