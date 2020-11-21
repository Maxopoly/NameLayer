package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

@CivCommand(id = "nlmg")
public class MergeGroups extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		NameLayerPlugin.getInstance().getGroupInteractionManager().mergeGroups(player.getUniqueId(), args[0], args[1],
				player::sendMessage);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1 || args.length == 2) {
			return NameLayerTabCompletion.completeGroupName(args[args.length - 1], (Player) sender);
		}
		return Collections.emptyList();
	}
}
