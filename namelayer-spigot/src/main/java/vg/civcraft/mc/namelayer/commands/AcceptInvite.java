package vg.civcraft.mc.namelayer.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.NameLayerPlugin;

@CivCommand(id = "nlag")
public class AcceptInvite extends StandaloneCommand {

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupInvitedTo(args[0], (Player) sender);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		NameLayerPlugin.getInstance().getGroupInteractionManager().acceptInvite(player.getUniqueId(), args[0],
				player::sendMessage);
		return true;
	}
}
