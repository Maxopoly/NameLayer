package vg.civcraft.mc.namelayer.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;

public final class NameLayerTabCompletion {

	private NameLayerTabCompletion() {
	}

	public static List<String> completePlayer(String prefix) {
		return complete(prefix, Bukkit.getOnlinePlayers(), p -> p.getName());
	}
	
	public static List<String> completeGroupName(String prefix, Player p) {
		Set<Group> playersGroups = NameLayerPlugin.getInstance().getGroupManager().getGroupsForPlayer(p.getUniqueId());
		if (playersGroups == null) {
			return Collections.emptyList();
		}
		return complete(prefix, playersGroups, g -> g.getName());
	}
	
	public static List<String> completePlayerType(String prefix, Group group) {
		return complete(prefix, group.getPlayerTypeHandler().getAllTypes(), t -> t.getName());
	}
	
	public static List<String> completeGroupInvitedTo(String prefix, Player sender) {
		return complete(prefix, PlayerListener.getNotifications(sender.getUniqueId()), g -> g.getName());
	}
	
	private static <T> List<String> complete(String prefix, Collection<T> data, Function<T,String> reMapper) {
		String lower = prefix.toLowerCase();
		return data.stream().map(reMapper).filter(s -> s.startsWith(lower)).collect(Collectors.toList());
	}

}
