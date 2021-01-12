package vg.civcraft.mc.namelayer.mc;

import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CoreConfigManager;

public class NameLayerConfig extends CoreConfigManager {
	
	private int localChatRange;
	private boolean warnNewPlayers;
	private ChatColor defaultLocalColor;
	private int killBroadcastRange;
	

	public NameLayerConfig(ACivMod plugin) {
		super(plugin);
	}
	
	protected boolean parseInternal(ConfigurationSection config) {
		// Local chat range
		this.localChatRange = config.getInt("chat.localChatRange", 1000);
		this.plugin.info("Local chat range: " + (this.localChatRange > 0 ? this.localChatRange : "disabled"));
		// Warn players of local chat rang
		this.warnNewPlayers = config.getBoolean("chat.chatRangeWarn", true);
		this.plugin.info("Warning new players: if noone is around: " + this.warnNewPlayers);
		// Default colour for local chat
		try {
			this.defaultLocalColor = ChatColor.of(Objects.requireNonNull(config.getString(
					"chat.defaultChatColor", "WHITE")));
		}
		catch (IllegalArgumentException exception) {
			this.defaultLocalColor = ChatColor.WHITE;
		}
		this.plugin.info("Default local chat color: " + this.defaultLocalColor);
		// Kill broadcast range
		this.killBroadcastRange = config.getInt("chat.killBroadcastRange",1000);
		this.plugin.info("Kill broadcast range: " +
				(this.killBroadcastRange > 0 ? this.killBroadcastRange : "disabled"));
		return true;
	}

	public int getLocalChatRange() {
		return this.localChatRange;
	}

	public boolean shouldWarnNewPlayers() {
		return this.warnNewPlayers;
	}

	public ChatColor getDefaultLocalColor() {
		return this.defaultLocalColor;
	}

	public int getKillBroadcastRange() {
		return this.killBroadcastRange;
	}

}
