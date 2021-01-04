package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id="nllci")
public class ListCurrentInvites extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		return true;
	}
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

}
