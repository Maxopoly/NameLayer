package vg.civcraft.mc.namelayer.commands;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;

@CivCommand(id="nlcg")
public class CreateGroup extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		NameLayerPlugin.getInstance().getGroupInteractionManager().createGroup(player.getUniqueId(), args[0], player::sendMessage);
		return true;
	}

	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}
}
