package vg.civcraft.mc.namelayer.mc.model.chat;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.NameAPI;
import com.github.maxopoly.artemis.util.BukkitConversion;
import com.github.maxopoly.zeus.model.ZeusLocation;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.playersettings.impl.collection.ListSetting;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitSendLocalChatMessage;

public class LocalChatMode implements ChatMode {

	@Override
	public String getInfoText() {
		return ChatColor.AQUA + "Local chat";
	}

	@Override
	public void processInput(String text, Player player) {
		sendLocalMessage(player, text);
	}

	/**
	 * Initiates sending a message in local chat by making the appropriate rabbit
	 * request
	 * 
	 * @param sender  Player sending the message
	 * @param message Message to send
	 */
	public static void sendLocalMessage(Player sender, String message) {
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitSendLocalChatMessage(sender.getUniqueId(),
				BukkitConversion.convertLocation(sender.getLocation()), calculateRange(sender.getLocation()), message));
	}

	private static final double calculateRange(Location location) {
		return NameLayerPlugin.getInstance().getNameLayerConfig().getLocalChatRange(); // TODO height changes?
	}

	/**
	 * Method to broadcast a message in local chat only on this server
	 * 
	 * @param sender  Player who sent the message
	 * @param message Message to send
	 */
	public static void broadcastLocalMessage(UUID sender, ZeusLocation location, String msg) {
		String senderName = NameAPI.getName(sender);
		String message = String.format("%s[Local] %s%s%s: %s%s", ChatColor.GRAY, ChatColor.WHITE, senderName,
				ChatColor.GRAY, ChatColor.WHITE, msg);
		Location loc = BukkitConversion.convertLocation(location);
		int maxDistance = NameLayerPlugin.getInstance().getNameLayerConfig().getLocalChatRange();
		ListSetting<String> ignoredPlayers = NameLayerPlugin.getInstance().getSettingsManager().getIgnoredPlayers();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.getWorld().equals(loc.getWorld())) {
				continue;
			}
			if (player.getLocation().distance(loc) > maxDistance) {
				continue;
			}
			if (ignoredPlayers.getValue(player).contains(senderName)) {
				continue;
			}
			player.sendMessage(message);
		}
	}

}
