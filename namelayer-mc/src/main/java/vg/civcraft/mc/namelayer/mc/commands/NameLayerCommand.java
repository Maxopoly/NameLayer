package vg.civcraft.mc.namelayer.mc.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;

public abstract class NameLayerCommand extends StandaloneCommand {
	
	protected UUID resolveUUID(CommandSender sender) {
		if (sender instanceof ConsoleCommandSender) {
			return Group.consoleUUID;
		}
		if (sender instanceof Player) {
			return ((Player) sender).getUniqueId();
		}
		throw new IllegalArgumentException(sender + " does not have a valid UUID");
	}

}
