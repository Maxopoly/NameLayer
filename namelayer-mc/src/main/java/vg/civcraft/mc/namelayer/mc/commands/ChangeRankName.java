package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitRenameRank;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nlrr")
public class ChangeRankName extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		GroupRankHandler handler = group.getGroupRankHandler();
		GroupRank rankToRename = handler.getRank(args[1]);
		if (rankToRename == null) {
			MsgUtils.sendRankNotExistMsg(player.getUniqueId(), group.getName(), args[1]);
			return true;
		}
		String newRankName = args[3];
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitRenameRank(player.getUniqueId(), group, rankToRename, newRankName));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		}
		if (args.length == 2) {
			return NameLayerTabCompletion.completePlayerType(args[1], (Player) sender, args[0]);
		}
		return Collections.emptyList();
	}

}
