package vg.civcraft.mc.namelayer.mc.model;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.chat.ChatMode;
import vg.civcraft.mc.namelayer.mc.util.NLScoreBoard;

public class ChatTracker {
	
	private final HashMap<UUID, ChatMode> chatChannels;
	private final HashMap<UUID, UUID> replyChannels;
	private final NLScoreBoard scoreboardHUD;

	public ChatTracker(NameLayerPlugin plugin) {
		this.replyChannels = new HashMap<>();
		this.chatChannels = new HashMap<>();
		this.scoreboardHUD = new NLScoreBoard();
	}
	
	public void setChatMode(Player player, ChatMode mode) {
		if (mode == null) {
			chatChannels.remove(player.getUniqueId());
		}
	}
	
	public void resetChatMode(Player player) {
		setChatMode(player, null);
	}

	public void updateHUD(final Player player) {
		Preconditions.checkArgument(player != null, "Player cannot be null!");
		this.scoreboardHUD.updateScoreboardHUD(player);
	}

	/**
	 * Gets the player to send reply to
	 *
	 * @param player the person sending reply command
	 * @return the UUID of the person to reply to, null if none
	 */
	public UUID getReplyChannel(final Player player) {
		Preconditions.checkArgument(player != null, "Player cannot be null!");
		return this.replyChannels.get(player.getUniqueId());
	}

	/**
	 * Add a player to the replyList
	 *
	 * @param sender The player using the reply command.
	 * @param receiver The the player that will receive the reply
	 */
	public void setReplyChannel(final Player sender, final Player receiver) {
		Preconditions.checkArgument(sender != null, "Sender cannot be null!");
		Preconditions.checkArgument(receiver != null, "Receiver cannot be null!");
		this.replyChannels.put(sender.getUniqueId(), receiver.getUniqueId());
	}

	/**
	 * Removes the channel from the channel storage
	 *
	 * @param player Player Name of the channel
	 */
	public void exitReplyChannel(final Player player) {
		Preconditions.checkArgument(player != null, "Player cannot be null!");
		this.replyChannels.remove(player.getUniqueId());
	}
}
