package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

@CivCommand(id="nlsdg")
public class SetDefaultGroup extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Group group = GroupAPI.getGroup(args [0]);
		if (group == null) {
			sender.sendMessage(String.format("%sThe group %s does not exist", ChatColor.RED, args [0]));
			return true;
		}
		//no permission check needed, this doesn't actually enable anything special
		NameLayerPlugin.getInstance().getSettingsManager().getDefaultGroup().setGroup((Player) sender, group);
		sender.sendMessage(ChatColor.GREEN + "Set your default group to " + group.getColoredName());
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
