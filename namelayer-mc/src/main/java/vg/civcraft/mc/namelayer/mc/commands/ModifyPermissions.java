package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitEditPermission;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nlmp")
public class ModifyPermissions extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		GroupRankHandler handler = group.getGroupRankHandler();
		GroupRank rank = handler.getRank(args[1]);
		if (rank == null) {
			MsgUtils.sendRankNotExistMsg(player.getUniqueId(), group.getColoredName(), args[1]);
			return true;
		}
		PermissionType perm = NameLayerPlugin.getInstance().getGroupTracker().getPermissionTracker().getPermission(args[3]);
		if (perm == null) {
			MsgUtils.sendMsg(player.getUniqueId(), String.format("%sThe permission %s%s doesn't exist", ChatColor.RED, args[3], ChatColor.RED));
			return true;
		}
		if ("add".equalsIgnoreCase(args[2])) {
			ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitEditPermission(player.getUniqueId(), group, true, rank, perm));
		} else if ("remove".equalsIgnoreCase(args[2])) {
			ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitEditPermission(player.getUniqueId(), group, false, rank, perm));
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
