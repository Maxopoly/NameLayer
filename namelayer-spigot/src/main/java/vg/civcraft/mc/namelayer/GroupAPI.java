package vg.civcraft.mc.namelayer;

import java.util.UUID;

import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public final class GroupAPI {
	
	private GroupAPI() {}
	
	public static Group getGroupById(int id) {
		return NameLayerPlugin.getInstance().getGroupManager().getGroup(id);
	}
	
	public static Group getGroupByName(String name) {
		return NameLayerPlugin.getInstance().getGroupManager().getGroup(name);
	}
	
	public static boolean hasPermission(UUID player, Group group, PermissionType perm) {
		return NameLayerPlugin.getInstance().getGroupManager().hasAccess(group, player, perm);
	}
	
	public static boolean hasPermission(Player player, Group group, PermissionType perm) {
		return hasPermission(player.getUniqueId(), group, perm);
	}

}
