package vg.civcraft.mc.namelayer.mc.commands;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitChangeGroupColour;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nlcgc")
public class ChangeGroupColourCommand extends StandaloneCommand {
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		ChatColor colour = ChatColor.valueOf(args[1]);
		if (colour == null) {
			MsgUtils.sendMsg(player.getUniqueId(), ChatColor.RED + "The colour: " + colour.toString() + " is not valid.");
			return true;
		}
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitChangeGroupColour(player.getUniqueId(), group.getName(), colour));
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
