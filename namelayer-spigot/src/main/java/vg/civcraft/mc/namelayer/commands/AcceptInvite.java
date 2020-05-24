package vg.civcraft.mc.namelayer.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.permission.PlayerType;

@CivCommand(id = "nlag")
public class AcceptInvite extends StandaloneCommand {
	

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return NameLayerTabCompletion.completeGroupInvitedTo(args[0], (Player) sender);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		NameLayerPlugin.getInstance().getGroupInteractionManager().acceptInvite(player.getUniqueId(), args[0], player::sendMessage);
		return true;
	}
}
