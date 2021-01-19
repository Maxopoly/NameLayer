package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitInvitePlayer;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nlip")
public class InvitePlayer extends NameLayerCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		UUID uuid = resolveUUID(sender);
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(uuid, args[0]);
			return true;
		}
		GroupRank inviteRank = null;
		if (args.length == 2) {
			inviteRank = group.getGroupRankHandler().getDefaultInvitationRank();
			if (inviteRank == null) {
				sender.sendMessage(group.getColoredName() + ChatColor.RED + " does not have a default invitation rank set.");
				return true;
			}
		}
		else {
			inviteRank = group.getGroupRankHandler().getRank(args [2]);
			if (inviteRank == null) {
				MsgUtils.sendRankNotExistMsg(uuid, group.getColoredName(), args[2]);
				return true;
			}
		}
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitInvitePlayer(uuid, group, args [1], inviteRank));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		case 2:
			return NameLayerTabCompletion.completePlayer(args[1]);
		case 3:
			return NameLayerTabCompletion.completePlayerType(args[2], (Player) sender, args[0]);
		default:
			return Collections.emptyList();
		}
	}
}
