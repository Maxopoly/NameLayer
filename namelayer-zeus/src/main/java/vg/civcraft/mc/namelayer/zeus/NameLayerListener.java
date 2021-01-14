package vg.civcraft.mc.namelayer.zeus;

import com.github.maxopoly.zeus.plugin.event.ZEventHandler;
import com.github.maxopoly.zeus.plugin.event.ZeusListener;
import com.github.maxopoly.zeus.plugin.event.events.PlayerJoinServerEvent;
import com.github.maxopoly.zeus.plugin.event.events.ServerLoadPlayerDataEvent;

public class NameLayerListener implements ZeusListener {
	
	@ZEventHandler(priority = 800)
	public void playerJoinServer(ServerLoadPlayerDataEvent event) {
		NameLayerZPlugin.getInstance().getGroupKnowledgeTracker().ensureAllGroupsAvailable(event.getPlayer(), event.getServer());
	}

}
