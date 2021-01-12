package vg.civcraft.mc.namelayer.mc.model.chat;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.util.BukkitConversion;
import com.github.maxopoly.zeus.model.ZeusLocation;

import net.md_5.bungee.api.ChatColor;
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

	public static void sendLocalMessage(Player sender, String message) {
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitSendLocalChatMessage(sender.getUniqueId(),
				BukkitConversion.convertLocation(sender.getLocation()), calculateRange(sender.getLocation()), message));
	}

	private static final double calculateRange(Location location) {
		return 1000; // TODO?
	}

	/**
	 * Method to broadcast a message in local chat on this server
	 * 
	 * @param sender  Player who sent the message
	 * @param message Message to send
	 */
	public void broadcastLocalMessage(UUID sender, ZeusLocation location, String message) {

	}

}
