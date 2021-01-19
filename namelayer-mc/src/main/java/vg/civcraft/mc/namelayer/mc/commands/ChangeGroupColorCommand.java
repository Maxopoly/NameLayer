package vg.civcraft.mc.namelayer.mc.commands;

import com.github.maxopoly.artemis.ArtemisPlugin;

import net.md_5.bungee.api.ChatColor;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitChangeGroupColor;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nlcgc")
public class ChangeGroupColorCommand extends StandaloneCommand {
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		ChatColor color = null;
		try {
			color = ChatColor.of(args[1]);
		} catch (IllegalArgumentException e) {
			MsgUtils.sendMsg(player.getUniqueId(), ChatColor.RED + "The color: " + args[1] + " is not valid.");
		}
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new RabbitChangeGroupColor(player.getUniqueId(), group.getName(), color));
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
