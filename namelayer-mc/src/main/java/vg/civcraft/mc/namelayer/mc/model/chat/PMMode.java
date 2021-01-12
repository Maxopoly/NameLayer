package vg.civcraft.mc.namelayer.mc.model.chat;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.NameAPI;
import com.github.maxopoly.zeus.model.PlayerData;
import com.google.common.base.Preconditions;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitSendPrivateChatMessage;

public class PMMode implements ChatMode {

	private UUID partner;

	public PMMode(UUID partner) {
		Preconditions.checkNotNull(partner);
		this.partner = partner;
	}

	@Override
	public String getInfoText() {
		return ChatColor.AQUA + "PM with " + NameAPI.getName(partner);
	}

	@Override
	public void processInput(String text, Player player) {
		PlayerData onlinePartner = ArtemisPlugin.getInstance().getPlayerDataManager().getOnlinePlayerData(partner);
		if (onlinePartner == null) {
			player.sendMessage(ChatColor.RED + "The player you were talking to has gone offline");
		}
		sendPrivateMessage(player, partner, text);
	}

	/**
	 * Method to Send private message between two players
	 * 
	 * @param sender   Player sending the message
	 * @param receiver Player Receiving the message
	 * @param message  Message to send from sender to receive
	 */
	public static void sendPrivateMessage(Player sender, UUID receiver, final String message) {
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new RabbitSendPrivateChatMessage(sender.getUniqueId(), receiver, message));
	}

	public static void showPMSentToSender(UUID sender, UUID receiver, String msg) {

	}
}
