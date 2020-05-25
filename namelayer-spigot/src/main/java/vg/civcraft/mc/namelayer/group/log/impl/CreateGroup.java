package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.LoggedGroupAction;

public class CreateGroup extends LoggedGroupAction {

	public static final String ID = "CREATE_GROUP";

	private String name;

	public CreateGroup(long time, UUID player, String name) {
		super(time, player);
		Preconditions.checkNotNull(name, "Name may not be null");
		this.name = name;
	}
	
	public CreateGroup(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank());
	}

	public String getName() {
		return name;
	}

	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player, name, null, null);
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.CAT_SPAWN_EGG);
		ItemAPI.setDisplayName(is, String.format("%s%s%s created %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.WHITE, name));
		enrichItem(is);
		ItemAPI.addLore(is, String.format("%sGroup name: %s%s", ChatColor.GOLD, ChatColor.AQUA, name));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s created %s%s", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN, ChatColor.WHITE,
				name);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
