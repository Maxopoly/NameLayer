package vg.civcraft.mc.namelayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import vg.civcraft.mc.namelayer.database.AssociationList;

public class NameAPI {
	
	private static NameAPI instance;
	private AssociationList associations;
	
	private Map<UUID, String> uuidsToName = new HashMap<>();
	private Map<String, UUID> nameToUUIDS = new HashMap<>();
	
	NameAPI(AssociationList ass){
		instance = this;
		associations =  ass;
		loadAllPlayerInfo();
	}
	
	public static NameAPI getInstance() {
		return instance;
	}
	
	public void loadAllPlayerInfo(){
		uuidsToName.clear();
		nameToUUIDS.clear();
		
		if (!NameLayerPlugin.getInstance().getConfigManager().cacheAllNames()) {
			return;
		}
		AssociationList.PlayerMappingInfo pmi = associations.getAllPlayerInfo();
		nameToUUIDS = pmi.nameMapping;
		uuidsToName = pmi.uuidMapping;	
	}
	
	public void changePlayerName(UUID player, String newName) {
		associations.changePlayer(newName, player);
		String oldName = uuidsToName.put(player, newName);
		nameToUUIDS.remove(oldName);
		nameToUUIDS.put(newName, player);
	}
	
	/**
	 * Returns the UUID of the player on the given server.
	 * @param playerName The playername.
	 * @return Returns the UUID of the player.
	 */
	public UUID getUUIDLocal(String playerName) {
		return nameToUUIDS.computeIfAbsent(playerName, associations::getUUID);
	}
	
	/**
	 * Returns the UUID of the player on the given server.
	 * @param playerName The playername.
	 * @return Returns the UUID of the player.
	 */
	public static UUID getUUID(String playerName) {
		return getInstance().getUUIDLocal(playerName);
	}
	
	/**
	 * Gets the playername from a given server from their uuid.
	 * @param uuid.
	 * @return Returns the PlayerName from the UUID.
	 */
	public static String getCurrentName(UUID uuid) {
		return getInstance().getCurrentNameLocal(uuid);
	}
	
	/**
	 * Gets the playername from a given server from their uuid.
	 * @param uuid.
	 * @return Returns the PlayerName from the UUID.
	 */
	public String getCurrentNameLocal(UUID uuid) {
		return uuidsToName.computeIfAbsent(uuid, associations::getCurrentName);
	}
}
