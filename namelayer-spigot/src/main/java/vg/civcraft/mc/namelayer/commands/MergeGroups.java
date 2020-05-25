package vg.civcraft.mc.namelayer.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.command.TabCompleters.GroupTabCompleter;
import vg.civcraft.mc.namelayer.permission.PermissionType;

@CivCommand(id="nlmg")
public class MergeGroups extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {

		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof Player))
			return null;

		if (args.length > 0)
			return GroupTabCompleter.complete(args[args.length - 1], PermissionType.getPermission("MERGE"),
					(Player) sender);
		else {
			return GroupTabCompleter.complete(null, PermissionType.getPermission("MERGE"), (Player) sender);
		}
	}
}
