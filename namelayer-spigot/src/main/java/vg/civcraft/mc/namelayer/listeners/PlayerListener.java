package vg.civcraft.mc.namelayer.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.group.GroupManager;

public class PlayerListener implements Listener{

	private static Map<UUID, Set<Group>> notifications = new HashMap<>();
	private GroupManager groupManager;
	
	public PlayerListener(GroupManager groupManager) {
		this.groupManager = groupManager;
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void playerJoinEvent(PlayerJoinEvent event){
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		
		if (!p.hasPlayedBefore()) {
			handleFirstJoin(p);
		}
		
		if (!notifications.containsKey(uuid) || notifications.get(uuid).isEmpty()) {
			return;
		}
		
		StringBuilder messageToSend = new StringBuilder(ChatColor.YELLOW.toString());
				
		boolean shouldAutoAccept = NameLayerPlugin.getInstance().getSettingsManager().getAutoAcceptInvites().getValue(p);
		if(shouldAutoAccept){
			messageToSend.append("You have auto-accepted invitation from the following groups while you were away: ");
		}
		else{
			messageToSend.append("You have been invited to the following groups while you were away. You can accept each invitation by using the command: /nlag [groupname].  ");
		}			
		List<String> groupName = getNotifications(uuid).stream().map(g -> g.getName()).collect(Collectors.toList());
		messageToSend.append(String.join(", ", groupName));
		p.sendMessage(messageToSend.toString());
	}
	
	public static void addNotification(UUID u, Group g) {
		getNotifications(u).add(g);
	}

	public static Set<Group> getNotifications(UUID player) {
		return notifications.computeIfAbsent(player, e -> new HashSet<>());
	}
	
	public static void removeNotification(UUID u, Group g){
		getNotifications(u).remove(g);
	}
	
	public static String getNotificationsInStringForm(UUID u){
		String groups = "";
		for (Group g: getNotifications(u)) {
			groups += g.getName() + ", ";
		}
		if (groups.length() == 0) {
			return ChatColor.GREEN + "You have no notifications.";
		}
		groups = groups.substring(0, groups.length()- 2);
		groups = ChatColor.GREEN + "Your current groups are: " + groups + ".";
		return groups;
	}
	
	private void handleFirstJoin(Player p) {
		if (!NameLayerPlugin.getInstance().getConfigManager().createGroupOnFirstJoin()) {
			return;
		}
		String name = p.getName();
		Group group = groupManager.getGroup(name);
		if (group != null) {
			for(int i = 1; i <= 20;i++) {
				name = p.getName() + String.valueOf(i);
				group = groupManager.getGroup(name);
				if (group == null) {
					break;
				}
			}
		}
		if (group != null) {
			NameLayerPlugin.getInstance().getLogger().warning("Newfriend automatic group creation failed for " + p.getName());
			return;
		}
		groupManager.createGroupAsync(name, p.getUniqueId(), g -> {
			NameLayerPlugin.getInstance().getSettingsManager().getDefaultGroup().setGroup(p, g);
		});
	}
}
