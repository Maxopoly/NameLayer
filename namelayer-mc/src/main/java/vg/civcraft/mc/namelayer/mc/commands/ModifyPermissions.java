package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

@CivCommand(id = "nlmp")
public class ModifyPermissions extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if ("add".equalsIgnoreCase(args[3])) {
			NameLayerPlugin.getInstance().getGroupInteractionManager().editPermission(player.getUniqueId(), args[0],
					true, args[1], args[2], player::sendMessage);
		} else if ("remove".equalsIgnoreCase(args[3])) {
			NameLayerPlugin.getInstance().getGroupInteractionManager().editPermission(player.getUniqueId(), args[0],
					false, args[1], args[2], player::sendMessage);
		} else {
			player.sendMessage(ChatColor.RED + "Third argument needs to be 'add' or 'remove'");
			return false;
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		switch(args.length) {
		case 1:
			return NameLayerTabCompletion.completeGroupName(args [0], (Player) sender);
		case 2:
			return NameLayerTabCompletion.completePlayerType(args[1], (Player) sender, args[0]);
		case 3:
			return NameLayerTabCompletion.staticComplete(args[2], "add", "remove");
		case 4:
			return NameLayerTabCompletion.completePermission(args[3]);
		default:
			return Collections.emptyList();
		}
		
	}
}
