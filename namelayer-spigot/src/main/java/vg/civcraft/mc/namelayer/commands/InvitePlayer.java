package vg.civcraft.mc.namelayer.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.command.TabCompleters.GroupTabCompleter;
import vg.civcraft.mc.namelayer.command.TabCompleters.MemberTypeCompleter;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.permission.PermissionType;
import vg.civcraft.mc.namelayer.permission.PlayerType;
import vg.civcraft.mc.namelayer.permission.PlayerTypeHandler;

@CivCommand(id = "nlip")
public class InvitePlayer extends AbstractGroupCommand {

	public static void sendInvitation(Group group, PlayerType pType, UUID invitedPlayer, UUID inviter,
			boolean saveToDB) {
		Player invitee = Bukkit.getPlayer(invitedPlayer);
		boolean shouldAutoAccept = NameLayerPlugin.getAutoAcceptHandler().getAutoAccept(invitedPlayer);
		if (invitee != null) {
			// invitee is online
			if (shouldAutoAccept) {
				// player auto accepts invite
				if (saveToDB) {
					group.addMember(invitedPlayer, pType);
				} else {
					group.addMember(invitedPlayer, pType, false);
				}
				invitee.sendMessage(
						ChatColor.GREEN + " You have auto-accepted invite to the group: " + group.getName());
			} else {
				group.addInvite(invitedPlayer, pType, saveToDB);
				PlayerListener.addNotification(invitedPlayer, group);
				String msg;
				if (inviter != null) {
					String inviterName = NameAPI.getCurrentName(inviter);
					msg = "You have been invited to the group " + group.getName() + " by " + inviterName + ".\n";
				} else {
					msg = "You have been invited to the group " + group.getName() + ".\n";
				}
				TextComponent message = new TextComponent(
						msg + "Click this message to accept. If you wish to toggle invites "
								+ "so they always are accepted please run /autoaccept");
				message.setColor(net.md_5.bungee.api.ChatColor.GREEN);
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nlag " + group.getName()));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("  ---  Click to accept").create()));
				invitee.spigot().sendMessage(message);
			}
		} else {
			// invitee is offline or on a different shard
			if (shouldAutoAccept) {
				if (saveToDB) {
					group.addMember(invitedPlayer, pType);
				} else {
					group.addMember(invitedPlayer, pType, false);
				}
			} else {
				// Player did not auto accept
				group.addInvite(invitedPlayer, pType, saveToDB);
			}
			PlayerListener.addNotification(invitedPlayer, group);
		}
	}

	@Override
	public boolean execute(Player player, Group group, String[] args) {
		String targetPlayer = args[1];
		String targetTypeName = args.length >= 3 ? args[2] : null;
		UUID toInvite = playerCheck(player, targetPlayer);
		if (toInvite == null) {
			return true;
		}
		PlayerTypeHandler handler = group.getPlayerTypeHandler();
		PlayerType targetType = targetType != null ? handler.getType(targetTypeName)
				: handler.getDefaultInvitationType();
		if (targetType == null) {
			player.sendMessage(ChatColor.RED
					+ "The player type you entered did not exist or you do not permission to invite to it");
			return true;
		}
		PermissionType permRequired = targetType.getInvitePermissionType();
		if (!GroupAPI.hasPermission(player, group, permRequired)) {
			player.sendMessage(ChatColor.RED
					+ "The player type you entered did not exist or you do not permission to invite to it");
			return true;
		}
		if (group.isTracked(toInvite)) {
			player.sendMessage(ChatColor.RED + "The player " + NameAPI.getCurrentName(toInvite)
					+ " is already tracked for " + group.getColoredName() + ChatColor.RED
					+ ". You have to modify their rank instead of inviting them");
			return true;
		}
		sendInvitation()
		player.sendMessage(ChatColor.GREEN + NameAPI.getCurrentName(toInvite) + " has been invited as " + targetType.getName() + " to "+ group.getColoredName());
		return true;
	}

	@Override
	public List<String> tabCompleteFurther(Player player, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
}
