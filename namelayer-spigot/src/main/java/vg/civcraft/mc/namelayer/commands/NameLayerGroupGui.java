package vg.civcraft.mc.namelayer.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.gui.GUIGroupOverview;
import vg.civcraft.mc.namelayer.gui.MainGroupGUI;

@CivCommand(id="nl")
public class NameLayerGroupGui extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			GUIGroupOverview gui = new GUIGroupOverview((Player) sender, null);
			gui.showScreen();
			return true;
		}
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			sender.sendMessage(String.format("%sThe group %s does not exist", ChatColor.RED, args[0]));
			return true;
		}
		MainGroupGUI gui = new MainGroupGUI(null, (Player) sender, group);
		gui.showScreen();
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
