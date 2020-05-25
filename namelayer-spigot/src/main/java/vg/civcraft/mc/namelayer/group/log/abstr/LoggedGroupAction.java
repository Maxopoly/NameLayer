package vg.civcraft.mc.namelayer.group.log.abstr;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;

public abstract class LoggedGroupAction {

	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	protected final UUID player;
	protected final long time;

	public LoggedGroupAction(long time, UUID player) {
		Preconditions.checkNotNull(player, "Can not store action for null player");
		Preconditions.checkArgument(time > 0, "Time stamp must be initialized");
		this.player = player;
		this.time = time;
	}

	/**
	 * @return UUID of the player who initiated this action
	 */
	public UUID getPlayer() {
		return player;
	}

	/**
	 * @return Name of the player who initiated this action
	 */
	public String getPlayerName() {
		return NameAPI.getCurrentName(player);
	}

	/**
	 * @return UNIX timestamp in ms of when this happened
	 */
	public long getTimeStamp() {
		return time;
	}

	protected void enrichItem(ItemStack item) {
		ItemAPI.addLore(item, String.format("%sPlayer: %s%s", ChatColor.YELLOW, ChatColor.GOLD, getPlayerName()),
				String.format("%sTime: %s%s", ChatColor.YELLOW, ChatColor.LIGHT_PURPLE, getFormattedTime()));
	}

	protected ItemStack getSkullFor(UUID uuid) {
		ItemStack is = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		is.setItemMeta(skullMeta);
		return is;
	}

	protected String getFormattedTime() {
		return timeFormatter.format(LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.UTC));
	}

	public abstract LoggedGroupActionPersistence getPersistence();

	public abstract ItemStack getGUIRepresentation();

	public abstract String getChatRepresentation();

}
