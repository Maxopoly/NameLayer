package vg.civcraft.mc.namelayer.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.permission.PlayerType;

@CivCommand(id = "nlag")
public class AcceptInvite extends AbstractGroupCommand {

	@Override
	public boolean execute(Player player, Group group, String[] args) {
		UUID uuid = NameAPI.getUUID(player.getName());
		PlayerType type = group.getInvite(uuid);
		if (type == null) {
			player.sendMessage(ChatColor.RED + "You were not invited to " + group.getColoredName());
			return true;
		}
		if (group.isMember(uuid)) {
			player.sendMessage(ChatColor.RED + "You are already a member or blacklisted you cannot join again.");
			group.removeInvite(uuid);
			return true;
		}
		group.addToTracking(uuid, type, true);
		group.removeInvite(uuid);
		PlayerListener.removeNotification(uuid, group);
		player.sendMessage(ChatColor.GREEN + "You have successfully been added to " + group.getColoredName() + " as a "
				+ type.getName());
		return true;
	}
	

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 0) {
			return NameLayerTabCompletion.completeGroupInvitedTo("", (Player) sender);
		}
		return NameLayerTabCompletion.completeGroupInvitedTo(args[0], (Player) sender);
	}

	@Override
	public List<String> tabCompleteFurther(Player player, String[] args) {
		//NO OP, overriding tab complete itself
		return null;
	}
}
