package vg.civcraft.mc.namelayer.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PlayerType;

@CivCommand(id = "nljg")
public class JoinGroup extends StandaloneCommand {

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		NameLayerPlugin.getInstance().getGroupInteractionManager().joinGroup(player.getUniqueId(), args[0],
				String.join(" ", Arrays.copyOfRange(args, 1, args.length)), player::sendMessage);
		return true;
	}

}
