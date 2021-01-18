package vg.civcraft.mc.namelayer.mc.model.chat;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.NameAPI;
import com.github.maxopoly.zeus.model.PlayerData;
import com.google.common.base.Preconditions;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.playersettings.impl.StringSetting;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitSendPrivateChatMessage;

public class PrivateChatMode implements ChatMode {

	private UUID partner;

	public PrivateChatMode(UUID partner) {
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
		Player senderPlayer = Bukkit.getPlayer(sender);
		if (senderPlayer == null) {
			return;
		}
		String receiverName = NameAPI.getNameLocal(receiver);
		String message = String.format("%s[PM --> ] %s%s%s: %s%s", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, receiverName,
				ChatColor.LIGHT_PURPLE, ChatColor.WHITE, msg);
		senderPlayer.sendMessage(message);
	}
	
	public static void showPMToReceiver(UUID sender, UUID receiver, String msg) {
		Player receiverPlayer = Bukkit.getPlayer(receiver);
		if (receiverPlayer == null) {
			return;
		}
		String senderName = NameAPI.getNameLocal(sender);
		String message = String.format("%s[PM <--] %s%s%s: %s%s", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, senderName,
				ChatColor.LIGHT_PURPLE, ChatColor.WHITE, msg);
		receiverPlayer.sendMessage(message);
	}
	
	public boolean equals(Object o) {
		return o instanceof PrivateChatMode && ((PrivateChatMode) o).partner.equals(this.partner);
	}
	
	@Override
	public void setInternalStorage(Player player, StringSetting modeSetting, StringSetting valueSetting) {
		modeSetting.setValue(player, ChatMode.Modes.PM.toString());
		valueSetting.setValue(player, partner.toString());
	}
}
