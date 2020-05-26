package vg.civcraft.mc.namelayer.commands;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;

@CivCommand(id = "nllg")
public class ListGroups extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Set<Group> groups = NameLayerPlugin.getInstance().getGroupManager().getGroupsForPlayer(player.getUniqueId());
		if (groups.isEmpty()) {
			sender.sendMessage(ChatColor.GREEN + "You are not a member of any groups");
			return true;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN);
		sb.append("Your groups are:\n");
		for(Group group : groups) {
			sb.append(ChatColor.DARK_GRAY);
			sb.append(" - ");
			sb.append(group.getColoredName());
			sb.append(ChatColor.YELLOW);
			sb.append("  (");
			sb.append(group.getRank(player.getUniqueId()).getName());
			sb.append(")\n");
		}
		sender.sendMessage(sb.toString());
		return true;
	}
	
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

}
