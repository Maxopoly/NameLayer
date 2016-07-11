package vg.civcraft.mc.namelayer.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.database.GroupManagerDao;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.misc.Mercury;

public class PlayerListener implements Listener{

	private static Map<UUID, List<Group>> notifications = new HashMap<UUID, List<Group>>();
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void playerJoinEvent(PlayerJoinEvent event){
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		
		if (!p.hasPlayedBefore()) {
			handleFirstJoin(p);
		}
		GroupManagerDao db = NameLayerPlugin.getGroupManagerDao();
		
		if (!notifications.containsKey(uuid) || notifications.get(uuid).isEmpty())
			return;
		
		String x = null;
				
		boolean shouldAutoAccept = db.shouldAutoAcceptGroups(uuid);
		if(shouldAutoAccept){
			x = "You have auto-accepted invitation from the following groups while you were away: ";
		}
		else{
			x = "You have been invited to the following groups while you were away. You can accept each invitation by using the command: /nlag [groupname].  ";
		}			
		
		for (Group g:notifications .get(uuid)){
			x += g.getName() + ", ";
		}
		x = x.substring(0, x.length()- 2);
		x += ".";
		p.sendMessage(ChatColor.YELLOW + x);
	}
	
	public static void addNotification(UUID u, Group g){
		if (!notifications.containsKey(u))
			notifications.put(u, new ArrayList<Group>());
		notifications.get(u).add(g);
	}

	public static List<Group> getNotifications(UUID player) {
		return notifications.get(player);
	}
	
	public static void removeNotification(UUID u, Group g){
		if (!notifications.containsKey(u))
			notifications.put(u, new ArrayList<Group>());
		notifications.get(u).remove(g);
	}
	
	public static String getNotificationsInStringForm(UUID u){
		if (!notifications.containsKey(u))
			notifications.put(u, new ArrayList<Group>());
		String groups = "";
		for (Group g: notifications.get(u))
			groups += g.getName() + ", ";
		if (groups.length() == 0)
			return ChatColor.GREEN + "You have no notifications.";
		groups = groups.substring(0, groups.length()- 2);
		groups = ChatColor.GREEN + "Your current groups are: " + groups + ".";
		return groups;
	}
	
	private void handleFirstJoin(Player p) {
		if (!NameLayerPlugin.createGroupOnFirstJoin()) {
			return;
		}
		Group g = null;
		if (GroupManager.getGroup(p.getName()) == null) {
			g = createNewFriendGroup(p.getName(), p.getUniqueId());
		}
		for(int i = 0; i < 20 && g == null ; i++) {
			if (GroupManager.getGroup(p.getName() + String.valueOf(i)) == null) {
				g = createNewFriendGroup(p.getName() + String.valueOf(i), p.getUniqueId());
			}
		}
		if (g != null) {
			g.setDefaultGroup(p.getUniqueId());
		}
		
	}
	
	private Group createNewFriendGroup(String name, UUID owner) {
		GroupManager gm = NameAPI.getGroupManager();
		Group g = new Group(name, owner, false, null, -1);
		int id = gm.createGroup(g);
		if (id == -1) { // failure
			NameLayerPlugin.log(Level.WARNING, "Newfriend automatic group creation failed for " + name + " " + owner);
			return null;
		}
		g.setGroupId(id);
		if (NameLayerPlugin.isMercuryEnabled()){
			String message = "recache " + g.getName();
			Mercury.invalidateGroup(message);
		}
		return g;
	}
}
