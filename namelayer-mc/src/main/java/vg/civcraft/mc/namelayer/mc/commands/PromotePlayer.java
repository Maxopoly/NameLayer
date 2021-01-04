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
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitPromotePlayer;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id="nlpp")
public class PromotePlayer extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		String targetPlayerName = args[1];
		GroupRankHandler handler = group.getGroupRankHandler();
		GroupRank targetRank = handler.getRank(args[2]);
		if (targetRank == null) {
			MsgUtils.sendRankNotExistMsg(player.getUniqueId(), group.getColoredName(), args[2]);
			return true;
		}
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitPromotePlayer(player.getUniqueId(), group, targetPlayerName, targetRank));
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
		}
		return Collections.emptyList();
	}

}