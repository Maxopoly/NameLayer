package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.civcraft.artemis.ArtemisPlugin;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitBlacklistPlayer;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nlbl")
public class BlacklistPlayer extends NameLayerCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String rank = args.length == 3 ? args[2] : null;
		UUID uuid = resolveUUID(sender);
		Group group = GroupAPI.getGroup(args [0]);
		if (group == null) {
			sender.sendMessage(
					ChatColor.RED + "The group you entered did not exist or you do not permission to blacklist on it");
			return false;
		}
		UUID toAdd = getPlayer(targetPlayer, callback);
		if (toAdd == null) {
			return false;
		}
		GroupRankHandler handler = group.getGroupRankHandler();
		GroupRank targetType = rank != null ? handler.getRank(rank) : handler.getDefaultInvitationRank();
		// this is designed to not reveal any names of player types to the outside
		if (targetType == null) {
			MsgUtils.sendRankNotExistMsg(player.getUniqueId(), group.getName(), rank);
			return false;
		}
		ArtemisPlugin.getInstance().getRabbitHandler()
		.sendMessage(new RabbitBlacklistPlayer(player.getUniqueId(), args[0]));
		NameLayerPlugin.getInstance().getGroupInteractionManager().blacklistPlayer(player.getUniqueId(), args[0],
				args[1], rank, player::sendMessage);
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
			return NameLayerTabCompletion.completePlayerType(args[2], (Player) sender, args[0]);
		default:
			return Collections.emptyList();
		}
	}

}
