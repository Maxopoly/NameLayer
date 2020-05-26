package vg.civcraft.mc.namelayer.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.NameLayerPlugin;

@CivCommand(id = "nlbl")
public class BlacklistPlayer extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String rank = args.length == 3 ? args[2] : null;
		Player player = (Player) sender;
		NameLayerPlugin.getInstance().getGroupInteractionManager().blacklistPlayer(player.getUniqueId(), args[0],
				args[1], rank, player::sendMessage);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		case 2:
			return NameLayerTabCompletion.completePlayer(args[1]);
		case 3:
			return NameLayerTabCompletion.completePlayerType(args[2], (Player) sender, args[0]);
		default:
			return Collections.emptyList();
		}
	}

}
