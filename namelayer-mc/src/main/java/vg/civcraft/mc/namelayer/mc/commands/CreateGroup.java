package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitCreateGroup;

@CivCommand(id = "nlcg")
public class CreateGroup extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group != null) {
			player.sendMessage(String.format("%sThe group %s%s already exists", ChatColor.RED, args[0],
					ChatColor.RED));
			return true;
		}	
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitCreateGroup(player.getUniqueId(), args[0]));
		return true;
	}

	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}
}
