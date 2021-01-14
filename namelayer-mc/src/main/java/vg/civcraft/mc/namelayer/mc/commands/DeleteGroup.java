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
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitDeleteGroup;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nldg")
public class DeleteGroup extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
			sender.sendMessage(String.format("%sAre you sure you want to delete %s%s? If yes, run '%s/nldg %s%s confirm'",
					ChatColor.YELLOW, group.getColoredName(), ChatColor.YELLOW, ChatColor.AQUA, group.getName(),
					ChatColor.AQUA));
			return true;
		}
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new RabbitDeleteGroup(player.getUniqueId(), group.getName()));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		}
		return Collections.emptyList();
	}
}
