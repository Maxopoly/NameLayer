package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitMergeGroup;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nlmg")
public class MergeGroups extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group groupToKeep = GroupAPI.getGroup(args[0]);
		if (groupToKeep == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		Group groupToDelete = GroupAPI.getGroup(args[1]);
		if (groupToDelete == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[1]);
			return true;
		}
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitMergeGroup(player.getUniqueId(), groupToKeep, groupToDelete));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1 || args.length == 2) {
			return NameLayerTabCompletion.completeGroupName(args[args.length - 1], (Player) sender);
		}
		return Collections.emptyList();
	}
}
