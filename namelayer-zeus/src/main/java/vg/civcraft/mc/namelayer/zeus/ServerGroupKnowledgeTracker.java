package vg.civcraft.mc.namelayer.zeus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.rabbit.RabbitMessage;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.google.common.collect.Sets;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.RecacheGroupMessage;

public class ServerGroupKnowledgeTracker {
	
	private Map<ArtemisServer, Set<Integer>> knownGroups;
	private Map<Integer, Set<ArtemisServer>> groupToServers;
	private NameLayerDAO dao;
	private ZeusGroupTracker groupTracker;
	
	public ServerGroupKnowledgeTracker(ZeusGroupTracker groupTracker, NameLayerDAO dao) {
		this.knownGroups = new HashMap<>();
		this.groupToServers = new HashMap<>();
		this.groupTracker = groupTracker;
		this.dao = dao;
	}
	
	public void initializeServer(ArtemisServer server) {
		knownGroups.put(server, Sets.newSetFromMap(new ConcurrentHashMap<>()));
	}
	
	public void globalRecache(Group group) {
		sendToInterestedServers(group, () -> new RecacheGroupMessage(group));
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
	
	
	public void handlePlayerJoin(UUID player, ArtemisServer server) {
		Set<Integer> matchingGroups = knownGroups.get(server);
		List<Integer> toSend = new ArrayList<>();
		for(int groupID : dao.getGroupsByPlayer(player)) {
			if (!matchingGroups.contains(groupID)) {
				toSend.add(groupID);
			}
		}
		for(int groupIdToSend : toSend) {
			Group group = groupTracker.loadOrGetGroup(groupIdToSend);
			RecacheGroupMessage msgToSend = new RecacheGroupMessage(group);
			ZeusMain.getInstance().getRabbitGateway().sendMessage(server, msgToSend);
		}
	}

}
