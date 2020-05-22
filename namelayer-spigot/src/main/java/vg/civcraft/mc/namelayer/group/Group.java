package vg.civcraft.mc.namelayer.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.google.common.collect.Lists;

import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.database.GroupManagerDao;
import vg.civcraft.mc.namelayer.permission.PlayerType;
import vg.civcraft.mc.namelayer.permission.PlayerTypeHandler;

public class Group {

	private static GroupManagerDao db;

	private String name;
	private int id;

	private Map<UUID, PlayerType> players;
	private Map<UUID, PlayerType> invites;

	private List<GroupLink> incomingLinks;
	private List<GroupLink> outgoingLinks;

	private PlayerTypeHandler playerTypeHandler;

	private Collection<Integer> secondaryIds;

	public Group(String name, int id) {
		if (db == null) {
			db = NameLayerPlugin.getInstance().getGroupManagerDao();
		}
		this.name = name;
		this.id = id;
		this.players = new TreeMap<>();
		this.invites = new TreeMap<>();
		this.incomingLinks = new ArrayList<>();
		this.outgoingLinks = new ArrayList<>();
	}

	public void addIncomingLink(GroupLink link) {
		this.incomingLinks.add(link);
	}

	public void addOutgoingLink(GroupLink link) {
		this.outgoingLinks.add(link);
	}

	public void removeIncomingLink(GroupLink link) {
		this.incomingLinks.remove(link);
	}

	public void removeOutgoingLink(GroupLink link) {
		this.outgoingLinks.remove(link);
	}

	public List<GroupLink> getIncomingLinks() {
		return Collections.unmodifiableList(this.incomingLinks);
	}

	public List<GroupLink> getOutgoingLinks() {
		return Collections.unmodifiableList(this.outgoingLinks);
	}

	public String getColoredName() {
		return NameLayerPlugin.getInstance().getNameLayerMeta().getMetaData(this).getChatColor() + this.name;
	}

	/**
	 * Gets the uuids of all players, who are tracked by this group. This doesn't
	 * only include members of the group, but also blacklisted players. Anyone not
	 * in this list will have the default non member player type as specified in the
	 * player type handler of this group
	 * 
	 * @return All tracked players
	 */
	public List<UUID> getAllTracked() {
		return Lists.newArrayList(players.keySet());
	}
	
	public List<UUID> getAllMembers() {
		List<UUID> members = new ArrayList<>();
		for(Entry<UUID, PlayerType> entry : players.entrySet()) {
			if (playerTypeHandler.isBlackListedType(entry.getValue())) {
				continue;
			}
			members.add(entry.getKey());
		}
		return members;
	}

	/**
	 * Checks whether the given uuid is tracked by this group, either as member or
	 * blacklisted
	 * 
	 * @param uuid UUID to check for
	 * @return True if the uuid is tracked, false if not
	 */
	public boolean isTracked(UUID uuid) {
		return players.containsKey(uuid);
	}

	/**
	 * Gets the uuids of all players, who are tracked with the given player type.
	 * This will not work when being called with the default non member type
	 * 
	 * @param type PlayerType to retrieve tracked players for
	 * @return All players tracked in this group with the given player type
	 */
	public List<UUID> getAllTrackedByType(PlayerType type) {
		List<UUID> uuids = Lists.newArrayList();
		for (Map.Entry<UUID, PlayerType> entry : players.entrySet()) {
			if (entry.getValue() == type) {
				uuids.add(entry.getKey());
			}
		}
		return uuids;
	}

	/**
	 * Gives the uuids of the tracked players whose name starts with the given
	 * String, this is not case-sensitive
	 * 
	 * @param prefix start of the players name
	 * @return list of all players whose name starts with the given string and are
	 *         tracked in this group
	 */
	public List<UUID> getTrackedByName(String prefix) {
		List<UUID> uuids = Lists.newArrayList();
		List<UUID> members = getAllTracked();

		prefix = prefix.toLowerCase();
		for (UUID member : members) {
			String playerName = NameAPI.getCurrentName(member);
			if (playerName.toLowerCase().startsWith(prefix)) {
				uuids.add(member);
			}
		}
		return uuids;
	}

	/**
	 * Gives the uuids of players who are tracked by this group and whose name is
	 * within the given range.
	 * 
	 * @param lowerLimit lexicographically lowest acceptable name
	 * @param upperLimit lexicographically highest acceptable name
	 * @return list of uuids of all players tracked by the group whose name is
	 *         within the given range
	 */
	public List<UUID> getTrackedInNameRange(String lowerLimit, String upperLimit) {
		List<UUID> uuids = Lists.newArrayList();
		List<UUID> members = getAllTracked();

		for (UUID member : members) {
			String name = NameAPI.getCurrentName(member);
			if (name.compareToIgnoreCase(lowerLimit) >= 0 && name.compareToIgnoreCase(upperLimit) <= 0) {
				uuids.add(member);
			}
		}
		return uuids;
	}

	/**
	 * Adds the player to be allowed to join a group into a specific PlayerType.
	 * 
	 * @param uuid     - The UUID of the player.
	 * @param type     - The PlayerType they will be joining.
	 */
	public void addInvite(UUID uuid, PlayerType type, boolean saveToDB) {
		invites.put(uuid, type);
		if (saveToDB) {
			Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> db.addGroupInvitation(uuid, this, type));
		}
		
	}

	/**
	 * Get's the PlayerType of an invited Player.
	 * 
	 * @param uuid - The UUID of the player.
	 * @return Returns the PlayerType or null.
	 */
	public PlayerType getInvite(UUID uuid) {
		return invites.get(uuid);
	}

	/**
	 * Removes the invite of a Player
	 * 
	 * @param uuid     - The UUID of the player.
	 */
	public void removeInvite(UUID uuid) {
		invites.remove(uuid);
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> db.removeGroupInvitation(uuid, this));
	}

	/**
	 * @return All invites pending
	 */
	public Map<UUID, PlayerType> dumpInvites() {
		return new HashMap<>(invites);
	}

	/**
	 * Checks if the player is a group member or not.
	 * 
	 * @param uuid - The UUID of the player.
	 * @return Returns true if the player is a member, false otherwise.
	 */
	public boolean isMember(UUID uuid) {
		PlayerType pType = players.get(uuid);
		if (pType != null) {
			// if the type is not a child node of the non member type, it is not
			// a blacklisted type, so the player is a member
			return !playerTypeHandler.isRelated(pType, playerTypeHandler.getDefaultNonMemberType());
		}
		return false;
	}

	/**
	 * Checks if the player is in the Group's PlayerType or not.
	 * 
	 * @param uuid - The UUID of the player.
	 * @param type - The PlayerType wanted.
	 * @return Returns true if the player is a member of the specific playertype,
	 *         otherwise false.
	 */
	public boolean isTracked(UUID uuid, PlayerType type) {
		PlayerType pType = players.get(uuid);
		return pType != null && pType.equals(type);
	}

	/**
	 * @param uuid - The UUID of the player.
	 * @return Returns the PlayerType of a UUID.
	 */
	public PlayerType getPlayerType(UUID uuid) {
		PlayerType member = players.get(uuid);
		if (member != null) {
			return member;
		}
		// not tracked, so default
		return playerTypeHandler.getDefaultNonMemberType();
	}

	/**
	 * Adds a member to a group.
	 * 
	 * @param uuid - The uuid of the player.
	 * @param type - The PlayerType to add. If a preexisting PlayerType is found, it
	 *             will be overwritten.
	 */

	public void addToTracking(UUID uuid, PlayerType type) {
		addToTracking(uuid, type, true);
	}

	public void addToTracking(UUID uuid, PlayerType type, boolean savetodb) {
		if (type == playerTypeHandler.getDefaultNonMemberType() && isTracked(uuid)) {
			return;
		}
		if (savetodb) {
			db.addMember(uuid, this, type);
		}
		players.put(uuid, type);
	}

	/**
	 * Removes the Player from the Group.
	 * 
	 * @param uuid - The UUID of the Player.
	 */
	public void removeFromTracking(UUID uuid) {
		removeFromTracking(uuid, true);
	}

	public void removeFromTracking(UUID uuid, boolean savetodb) {
		if (savetodb) {
			db.removeMember(uuid, this);
		}
		players.remove(uuid);
	}

	/**
	 * @return Returns the group name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the id for a group.
	 * 
	 * @return the group id for a group.
	 */
	public int getGroupId() {
		return id;
	}

	public Collection<Integer> getSecondaryIds() {
		return secondaryIds;
	}

	public void addSecondaryId(int id) {
		secondaryIds.add(id);
	}

	public PlayerTypeHandler getPlayerTypeHandler() {
		return playerTypeHandler;
	}

	public void setPlayerTypeHandler(PlayerTypeHandler handler) {
		this.playerTypeHandler = handler;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Group)) {
			return false;
		}
		Group g = (Group) obj;
		return g.getGroupId() == getGroupId();
	}
}
