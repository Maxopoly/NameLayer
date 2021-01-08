package vg.civcraft.mc.namelayer.zeus;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.google.common.collect.Sets;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RecacheGroupMessage;

public class ServerGroupKnowledgeTracker {
	
	private Map<ArtemisServer, Set<Integer>> knownGroups;
	private Map<Integer, Set<ArtemisServer>> groupToServers;
	private NameLayerDAO dao;
	private ZeusGroupTracker groupTracker;
	
	public ServerGroupKnowledgeTracker(ZeusGroupTracker groupTracker, NameLayerDAO dao) {
		this.knownGroups = new ConcurrentHashMap<>();
		this.groupToServers = new ConcurrentHashMap<>();
		this.groupTracker = groupTracker;
		this.dao = dao;
	}
	
	public void initializeServer(ArtemisServer server) {
		knownGroups.put(server, Sets.newSetFromMap(new ConcurrentHashMap<>()));
	}
	
	public void globalRecache(Group group) {
		sendToInterestedServers(group, () -> new RecacheGroupMessage(group));
	}
	
	public void ensureIsCached(Group group, ArtemisServer server) {
		Set<Integer> matchingGroups = knownGroups.computeIfAbsent(server, g -> Sets.newSetFromMap(new ConcurrentHashMap<>()));
		if (matchingGroups.contains(group.getPrimaryId())) {
			return;
		}
		matchingGroups.add(group.getPrimaryId());
		RecacheGroupMessage msgToSend = new RecacheGroupMessage(group);
		ZeusMain.getInstance().getRabbitGateway().sendMessage(server, msgToSend);
	}
	
	public void ensureServerOfCaches(Group group, UUID uuid) {
		GlobalPlayerData playerData = ZeusMain.getInstance().getPlayerManager().getOnlinePlayerData(uuid);
		if (playerData == null) {
			return; //offline
		}
		ArtemisServer server = playerData.getMCServer();
		if (server == null) {
			return;
		}
		ensureIsCached(group, server);
	}
	
	public void sendToInterestedServers(Group group, Supplier<RabbitMessage> messages) {
		Set<ArtemisServer> servers = groupToServers.get(group.getPrimaryId());
		if (servers == null) {
			return;
		}
		for(ArtemisServer server : servers) {
			//new rabbit message per object so each has its own transaction id
			ZeusMain.getInstance().getRabbitGateway().sendMessage(server, messages.get());
		}
	}
	
	
	public void ensureAllGroupsAvailable(UUID player, ArtemisServer server) {
		for(int groupID : dao.getGroupsByPlayer(player)) {
			Group group = groupTracker.loadOrGetGroup(groupID);
			ensureIsCached(group, server);
		}
	}

}
