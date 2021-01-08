package vg.civcraft.mc.namelayer.zeus;

import com.github.maxopoly.zeus.plugin.event.ZEventHandler;
import com.github.maxopoly.zeus.plugin.event.ZeusListener;
import com.github.maxopoly.zeus.plugin.event.events.PlayerJoinServerEvent;

public class NameLayerListener implements ZeusListener {
	
	@ZEventHandler(priority = 800)
	public void playerJoinServer(PlayerJoinServerEvent event) {
		NameLayerZPlugin.getInstance().getGroupKnowledgeTracker().ensureAllGroupsAvailable(event.getPlayerData().getUUID(), event.getNewServer());
	}

}
