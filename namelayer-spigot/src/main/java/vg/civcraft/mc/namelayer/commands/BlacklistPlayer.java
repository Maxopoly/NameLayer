package vg.civcraft.mc.namelayer.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id="nlbl")
public class BlacklistPlayer extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
