package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.PermissionTracker;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

@CivCommand(id = "nllp")
public class ListPermissions extends NameLayerCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			sender.sendMessage(String.format("%sThe group %s does not exist", ChatColor.RED, args[0]));
			return true;
		}
		GroupRank rank = group.getRank(player.getUniqueId());
		if (!group.getGroupRankHandler().isMemberRank(rank)) {
			sender.sendMessage(String.format("%sYou are not a member of ", ChatColor.RED, group.getColoredName()));
			return true;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN);
		sb.append("You are ");
		sb.append(ChatColor.GOLD);
		sb.append(rank.getName());
		sb.append(ChatColor.GREEN);
		sb.append(" on ");
		sb.append(group.getColoredName());
		sb.append(ChatColor.GREEN);
		if (rank == group.getGroupRankHandler().getOwnerRank()) {
			sb.append(" with all permissions");
			sender.sendMessage(sb.toString());
			return true;
		}
		sb.append(" with the following permissions:\n");
		PermissionTracker permTracker = NameLayerPlugin.getInstance().getGroupTracker().getPermissionTracker();
		for (Integer permID : rank.getAllPermissions()) {
			PermissionType perm = permTracker.getPermission(permID);
			if (perm == null) {
				continue;
			}
			sb.append(ChatColor.GRAY);
			sb.append(" - ");
			sb.append(ChatColor.GOLD);
			sb.append(perm.getName());
			sb.append('\n');
		}
		player.sendMessage(sb.toString());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		}
		return Collections.emptyList();
	}

}
