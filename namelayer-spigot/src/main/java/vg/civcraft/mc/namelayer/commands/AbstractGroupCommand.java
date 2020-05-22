package vg.civcraft.mc.namelayer.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public abstract class AbstractGroupCommand extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Group group = GroupAPI.getGroupByName(args[0]);
		if (group == null) {
			sender.sendMessage(ChatColor.RED + "The group " + args[0] + " does not exist");
			return true;
		}
		return execute((Player) sender, group, args);
	}

	public abstract boolean execute(Player player, Group group, String[] args);

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 0) {
			return NameLayerTabCompletion.completeGroupName("", (Player) sender);
		}
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		}
		return tabCompleteFurther((Player) sender, args);
	}

	protected boolean permsCheck(Group group, Player player, PermissionType perm) {
		if (GroupAPI.hasPermission(player, group, perm)) {
			return true;
		}
		player.sendMessage(ChatColor.RED + "To do this you need the permission " + ChatColor.YELLOW + perm.getName()
				+ ChatColor.RED + " on the group " + group.getColoredName() + ChatColor.RED + " which you do not have");
		return false;
	}
	
	protected UUID playerCheck(Player sender, String victimName) {
		UUID uuid = NameAPI.getUUID(victimName);
		if (uuid == null) {
			sender.sendMessage(ChatColor.RED + "The player " + ChatColor.YELLOW + victimName + ChatColor.RED + " does not exist");
		}
		return uuid;
	}

	public abstract List<String> tabCompleteFurther(Player player, String[] args);

}
