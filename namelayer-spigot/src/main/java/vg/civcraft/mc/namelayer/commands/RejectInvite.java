package vg.civcraft.mc.namelayer.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.command.TabCompleters.InviteTabCompleter;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.permission.PlayerType;

@CivCommand(id = "nlri")
public class RejectInvite extends AbstractGroupCommand {

	@Override
	public boolean execute(Player player, Group group, String[] args) {
		PlayerType type = group.getInvite(player.getUniqueId());
		if (type == null) {
			player.sendMessage(ChatColor.RED + "You have not been invited to " + group.getColoredName());
			return true;
		}
		player.sendMessage(ChatColor.GREEN + "You rejected the invite to " + group.getColoredName() + ChatColor.GREEN
				+ " as " + ChatColor.YELLOW + type.getName());
		group.removeInvite(player.getUniqueId());
		return true;
	}

	@Override
	public List<String> tabCompleteFurther(Player player, String[] args) {
		return InviteTabCompleter.complete(args[0], player);
	}

}
