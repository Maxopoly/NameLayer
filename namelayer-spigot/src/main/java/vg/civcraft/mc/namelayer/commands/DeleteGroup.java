package vg.civcraft.mc.namelayer.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;

@CivCommand(id = "nldg")
public class DeleteGroup extends AbstractGroupCommand {
	

	@Override
	public boolean execute(Player player, Group group, String[] args) {
		if (!permsCheck(group, player, NameLayerPlugin.getInstance().getNLPermissionManager().getDeleteGroup())) {
			return true;
		}
		if (args.length < 2 || !args[2].equalsIgnoreCase("confirm") ) {
			player.sendMessage(
					ChatColor.YELLOW + "Are you sure you want to delete " + group.getColoredName() + ChatColor.YELLOW
							+ " ?. If yes run '/nldg " + group.getColoredName() + ChatColor.YELLOW + " confirm");
			return true;
		}
		NameLayerPlugin.getInstance().getGroupManager().deleteGroup(group);
		player.sendMessage(ChatColor.GREEN + group.getName() + " has been deleted");
		return true;
	}

	@Override
	public List<String> tabCompleteFurther(Player player, String[] args) {
		return Collections.emptyList();
	}
}
