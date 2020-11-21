package vg.civcraft.mc.namelayer.mc.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

public final class NameLayerTabCompletion {

	private NameLayerTabCompletion() {
	}
	
	public static List<String> staticComplete(String prefix, String ... possibleChoice) {
		return complete(prefix, Arrays.asList(possibleChoice), s -> s);
	}

	public static List<String> completePlayer(String prefix) {
		return complete(prefix, Bukkit.getOnlinePlayers(), Player::getName);
	}
	
	public static List<String> completeGroupName(String prefix, Player p) {
		Set<Group> playersGroups = NameLayerPlugin.getInstance().getGroupManager().getGroupsForPlayer(p.getUniqueId());
		if (playersGroups == null) {
			return Collections.emptyList();
		}
		return complete(prefix, playersGroups, Group::getName);
	}
	
	public static List<String> completePlayerType(String prefix, Player player, String groupName) {
		Group group = GroupAPI.getGroup(groupName);
		if (group == null) {
			return Collections.emptyList();
		}
		return completePlayerType(prefix, player.getUniqueId(), group);
	}
	
	public static List<String> completePlayerType(String prefix, UUID player, Group group) {
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		List<GroupRank> ranks = new ArrayList<>();
		for(GroupRank rank : rankHandler.getAllRanks()) {
			if (rank == rankHandler.getDefaultNonMemberRank()) {
				continue;
			}
			if(GroupAPI.hasPermission(player, group, rank.getInvitePermissionType())) {
				ranks.add(rank);
			}
		}
		return complete(prefix, ranks, GroupRank::getName);
	}
	
	public static List<String> completeGroupInvitedTo(String prefix, Player sender) {
		return complete(prefix, PlayerListener.getNotifications(sender.getUniqueId()), Group::getName);
	}
	
	public static List<String> completePermission(String prefix) {
		return complete(prefix, PermissionType.getAllPermissions(), PermissionType::getName);
	}
	
	private static <T> List<String> complete(String prefix, Collection<T> data, Function<T,String> reMapper) {
		String lower = prefix.toLowerCase();
		return data.stream().map(reMapper).filter(s -> s.startsWith(lower)).collect(Collectors.toList());
	}

}
