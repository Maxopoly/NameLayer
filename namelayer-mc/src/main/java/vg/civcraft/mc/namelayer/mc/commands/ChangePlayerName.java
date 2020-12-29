package vg.civcraft.mc.namelayer.mc.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.github.civcraft.artemis.NameAPI;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "nlcpn")
public class ChangePlayerName extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		UUID player = NameAPI.getUUID(args[0]);
		if (player == null) {
			sender.sendMessage(args[0] + " has never logged in");
			return false;
		}
		String oldName = NameAPI.getCurrentName(player);
		String newName = args[1].length() >= 16 ? args[1].substring(0, 16) : args[1];
		NameAPI.getInstance().changePlayerName(player, newName);
		sender.sendMessage("Changed name of " + player + " from " + oldName + " to " + newName
				+ ". They have to relog for this to apply.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return NameLayerTabCompletion.completePlayer(args [0]);
	}
}
