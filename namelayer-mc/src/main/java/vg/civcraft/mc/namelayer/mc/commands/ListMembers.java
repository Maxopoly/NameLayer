package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.mc.NameAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

@CivCommand(id = "nllm")
public class ListMembers extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Map<GroupRank, Set<UUID>> playersByRank = NameLayerPlugin.getInstance().getGroupInteractionManager()
				.getMemberList(player.getUniqueId(), args[0], player::sendMessage);
		StringBuilder sb = new StringBuilder();
		if (playersByRank == null) {
			sender.sendMessage(String.format("%sThe group %s does not exist", ChatColor.RED, args [0]));
			return true;
		}
		if (playersByRank.isEmpty()) {
			sender.sendMessage(String.format("%sYou do not have permission to list any members of %s", ChatColor.RED, args [0]));
			return true;
		}
		for (Entry<GroupRank, Set<UUID>> entry : playersByRank.entrySet()) {
			if (entry.getValue().isEmpty()) {
				continue;
			}
			GroupRank rank = entry.getKey();
			sb.append(ChatColor.YELLOW);
			sb.append(rank.getName());
			sb.append(" has ");
			sb.append(entry.getValue().size());
			sb.append(" players:\n");
			for(UUID member : entry.getValue()) {
				sb.append(" - ");
				sb.append(NameAPI.getCurrentName(member));
				sb.append('\n');
			}
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
