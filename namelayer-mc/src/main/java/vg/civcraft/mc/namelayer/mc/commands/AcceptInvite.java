package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.civcraft.artemis.ArtemisPlugin;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitAcceptInvite;

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
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new RabbitAcceptInvite(player.getUniqueId(), args[0]));
		return true;
	}
}
