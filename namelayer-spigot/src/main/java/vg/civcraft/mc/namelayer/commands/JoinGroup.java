package vg.civcraft.mc.namelayer.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PlayerType;

import java.util.Collections;
import java.util.List;

@CivCommand(id="nljg")
public class JoinGroup extends AbstractGroupCommand {

	@Override
	public boolean execute(Player player, Group group, String[] args) {
		if (group.isTracked(player.getUniqueId())) {
			player.sendMessage(
					ChatColor.RED + "You are either already a member or blacklisted on " + group.getColoredName());
			return true;
		}
		String password = NameLayerPlugin.getInstance().getNameLayerMeta().getMetaData(group).getPassword();
		if (password == null) {
			player.sendMessage(group.getColoredName()
					+ " does not have a password set and can thus not be joined via this command");
			return true;
		}
		if (!password.equals(args[1])) {
			player.sendMessage(ChatColor.RED + "Wrong password");
			return true;
		}
		PlayerType targetType = group.getPlayerTypeHandler().getDefaultPasswordJoinType();
		group.addToTracking(player.getUniqueId(), targetType);
		player.sendMessage(ChatColor.GREEN + "You have been added to " + group.getColoredName() + ChatColor.GREEN
				+ " as " + targetType.getName());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

	@Override
	public List<String> tabCompleteFurther(Player player, String[] args) {
		return null; //NO OP
	}

}
