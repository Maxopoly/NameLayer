package vg.civcraft.mc.namelayer.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PermissionType;
import vg.civcraft.mc.namelayer.permission.PlayerType;
import vg.civcraft.mc.namelayer.permission.PlayerTypeHandler;

@CivCommand(id = "nlip")
public class InvitePlayer extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String rank = args.length == 3 ? args[2] : null;
		Player player = (Player) sender;
		NameLayerPlugin.getInstance().getGroupInteractionManager().inviteMember(player.getUniqueId(), args[0], args[1],
				rank, player::sendMessage);
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
			// TODO complete rank
		}
		return Collections.emptyList();
	}
}
