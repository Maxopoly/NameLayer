package vg.civcraft.mc.namelayer.command.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.command.PlayerCommandMiddle;
import vg.civcraft.mc.namelayer.command.TabCompleters.GroupMemberTabCompleter;
import vg.civcraft.mc.namelayer.command.TabCompleters.GroupTabCompleter;
import vg.civcraft.mc.namelayer.command.TabCompleters.MemberTypeCompleter;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PlayerType;
import vg.civcraft.mc.namelayer.permission.PlayerTypeHandler;
import vg.civcraft.mc.namelayer.events.GroupPromotePlayerEvent;

public class PromotePlayer extends PlayerCommandMiddle{

	public PromotePlayer(String name) {
		super(name);
		setIdentifier("nlpp");
		setDescription("Promote/Demote a Player in a Group");
		setUsage("/nlpp <group> <player> <playertype>");
		setArguments(3,3);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("How about No?");
			return true;
		}
		
		Player p = (Player) sender;
		
		UUID executor = NameAPI.getUUID(p.getName());
		
		UUID promotee = NameAPI.getUUID(args[1]);
		
		if(promotee ==null){
			p.sendMessage(ChatColor.RED + "That player does not exist");
			return true;
		}
		
		if(promotee.equals(executor)){
			p.sendMessage(ChatColor.RED + "You cannot promote yourself");
			return true;
		}
		
		Group group = GroupManager.getGroup(args[0]);
		if (groupIsNull(sender, args[0], group)) {
			return true;
		}
		if (group.isDisciplined()){
			p.sendMessage(ChatColor.RED + "This group is disiplined.");
			return true;
		}
		PlayerTypeHandler ptHandler = group.getPlayerTypeHandler();
		PlayerType promoteeCurrentType = group.getPlayerType(promotee);
		PlayerType promoteeTargetType = group.getPlayerTypeHandler().getType(args [2]);
		if(promoteeTargetType == null){
			sendPlayerTypes(group, sender, args [2]);
		}
		if(promoteeCurrentType == ptHandler.getDefaultNonMemberType()) {
			p.sendMessage(ChatColor.RED + "You can't demote to this rank, because it's not an explicit member rank");
			return true;
		}
		
		if (!group.isMember(p.getUniqueId())){
			p.sendMessage(ChatColor.RED + "You are not on that group.");
			return true;
		}
		
		
		if (promoteeCurrentType.equals(ptHandler.getDefaultNonMemberType()) && !promoteeTargetType.isBlacklistType()){ //can't edit a player who isn't in the group
			p.sendMessage(ChatColor.RED + NameAPI.getCurrentName(promotee) + " is not a member of this group, you have to invite this player instead of directly setting his rank");
			return true;
		}
		
		if (promoteeCurrentType.isBlacklistType() && !promoteeTargetType.isBlacklistType()) {
			p.sendMessage(ChatColor.RED + "You can't promote this player directly to a member rank, because he is currently blacklisted. Remove him from the blacklisted player type and"
					+ "then invite him instead");
			return true;
		}
		
		if (!canModifyRank(group, p, promoteeCurrentType)){
			p.sendMessage(ChatColor.RED + "You do not have permissions to change the current rank of this player");
			return true;
		}
		
		if (!canModifyRank(group, p, promoteeTargetType)){
			p.sendMessage(ChatColor.RED + "You do not have permissions to promote players to this rank");
			return true;
		}
		
		if (group.isOwner(promotee)){
			p.sendMessage(ChatColor.RED + "That player owns the group, you cannot "
					+ "demote the player.");
			return true;
		}
		
		OfflinePlayer prom = Bukkit.getOfflinePlayer(promotee);
		if(prom.isOnline()){
			Player oProm = (Player) prom;
			GroupPromotePlayerEvent event = new GroupPromotePlayerEvent(oProm, group, promoteeCurrentType, promoteeTargetType);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()){
				return false;
			}
			group.removeTracked(promotee);
			group.addTracked(promotee, promoteeTargetType);
			p.sendMessage(ChatColor.GREEN + NameAPI.getCurrentName(promotee) + " has been added as (PlayerType) " +
					promoteeTargetType.toString() + " to " + group.getName());
			oProm.sendMessage(ChatColor.GREEN + "You have been promoted to (PlayerType) " +
					promoteeTargetType.getName() + " in " + group.getName());
		}
		else{
			//player is offline change their perms
			group.removeTracked(promotee);
			group.addTracked(promotee, promoteeTargetType);
			p.sendMessage(ChatColor.GREEN + NameAPI.getCurrentName(promotee) + " has been added as (PlayerType) " +
					promoteeTargetType.getName() + " to " + group.getName());
		}
		checkRecacheGroup(group);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "I'm sorry baby, please run this as a player :)");
			return null;
		}
		if (args.length < 2) {
			if (args.length == 0)
				return GroupTabCompleter.complete(null, null, (Player) sender);
			else
				return GroupTabCompleter.complete(args[0], null, (Player)sender);

		} else if (args.length == 2)
			return GroupMemberTabCompleter.complete(args[0], args[1], (Player) sender);
		else if (args.length == 3) {
			Group g = GroupManager.getGroup(args [0]);
			if (g == null) {
				return null;
			}
			return MemberTypeCompleter.complete(g, args[2]);
		}
		else return null;
	}

}