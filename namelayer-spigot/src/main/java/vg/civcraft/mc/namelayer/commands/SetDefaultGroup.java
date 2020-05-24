package vg.civcraft.mc.namelayer.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;

@CivCommand(id="nlsdg")
public class SetDefaultGroup extends AbstractGroupCommand {

	@Override
	public boolean execute(Player player, Group group, String[] args) {
		//no permission check needed, this doesn't actually enable anything special
		NameLayerPlugin.getInstance().getSettingsManager().getDefaultGroup().setGroup(player, group);
		player.sendMessage(ChatColor.GREEN + "Set your default group to " + group.getColoredName());
		return true;
	}

	@Override
	public List<String> tabCompleteFurther(Player player, String[] args) {
		return Collections.emptyList();
	}
}
