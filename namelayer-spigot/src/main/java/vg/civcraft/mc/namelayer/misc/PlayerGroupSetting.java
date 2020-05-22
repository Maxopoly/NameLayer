package vg.civcraft.mc.namelayer.misc;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.playersettings.impl.IntegerSetting;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.group.Group;

public class PlayerGroupSetting extends IntegerSetting {
	
	public PlayerGroupSetting(JavaPlugin owningPlugin, String name, String identifier,
			ItemStack gui, String description) {
		super(owningPlugin, -1, name, identifier, gui, description, false);
	}

	@Override
	public ItemStack getGuiRepresentation(UUID player) {
		ItemStack stack = super.getGuiRepresentation(player);
		ItemAPI.setDisplayName(stack, getGroup(player).getName());
		return stack;
	}
	
	public Group getGroup(UUID player) {
		int val = getValue(player);
		return GroupAPI.getGroupById(val);
	}
	
	public Group getGroup(Player player) {
		return getGroup(player.getUniqueId());
	}
	
	public void setGroup(UUID player, Group group) {
		Preconditions.checkNotNull(group);
		setValue(player, group.getGroupId());
	}
	
	public void setGroup(Player player, Group group) {
		setGroup(player.getUniqueId(), group);
	}

	@Override
	public boolean isValidValue(String input) {
		int val;
		try {
			val = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			return false;
		}
		Group group = GroupAPI.getGroupById(val);
		return group != null;
	}

	@Override
	public String toText(Integer value) {
		Group group = GroupAPI.getGroupById(value);
		if (group == null) {
			return "null";
		}
		return group.getName();
	}

}
