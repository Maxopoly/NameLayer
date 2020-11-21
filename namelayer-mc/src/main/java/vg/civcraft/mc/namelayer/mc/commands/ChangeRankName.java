package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

@CivCommand(id = "nlrr")
public class ChangeRankName extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		NameLayerPlugin.getInstance().getGroupInteractionManager().renameRank(player.getUniqueId(), args[0], args[1],
				args[2], player::sendMessage);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		}
		if (args.length == 2) {
			return NameLayerTabCompletion.completePlayerType(args[1], (Player) sender, args[0]);
		}
		return Collections.emptyList();
	}

}
