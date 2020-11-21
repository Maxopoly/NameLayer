package vg.civcraft.mc.namelayer.zeus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.database.DBConnection;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupLink;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;

public class GroupManagerDao {
	private Logger logger;
	private DBConnection db;

	public GroupManagerDao(Logger logger, DBConnection db) {
		this.logger = logger;
		this.db = db;
	}

	public boolean createTables() {
		try (Connection conn = db.getConnection()) {
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS nl_groups (group_id INT(11) NOT NULL AUTO_INCREMENT, group_name VARCHAR(32) NOT NULL UNIQUE, PRIMARY KEY(group_id))")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS nl_ranks(group_id INT(11) NOT NULL, rank_id INT(11) NOT NULL, rank_name VARCHAR(32) NOT NULL, parent_rank_id INT(11), CONSTRAINT UNIQUE (group_id, rank_id), CONSTRAINT UNIQUE (group_id, rank_name), PRIMARY KEY(group_id,rank_id), FOREIGN KEY(group_id) REFERENCES nl_groups (group_id) ON DELETE CASCADE)")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS nl_members(group_id INT(11) NOT NULL, player UUID NOT NULL, rank_id INT(11) NOT NULL, UNIQUE KEY group_player_key (group_id,player), FOREIGN KEY(group_id) REFERENCES nl_groups (group_id) ON DELETE CASCADE, FOREIGN KEY(rank_id) REFERENCES nl_ranks (rank_id) ON DELETE CASCADE)")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS nl_merged_groups (old_group_id INT(11) NOT NULL, new_group_id INT(11) NOT NULL, FOREIGN KEY(new_group_id) REFERENCES nl_groups (group_id) ON DELETE CASCADE, PRIMARY KEY(old_group_id))")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS nl_global_permissions(perm_id INT(11) NOT NULL AUTO_INCREMENT, perm_name TEXT not null, PRIMARY KEY(perm_id), UNIQUE KEY perm_name_key (perm_name))")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS nl_group_permissions(group_id INT(11) NOT NULL, rank_ID INT(11) NOT NULL, perm_id INT(11) NOT NULL, PRIMARY KEY(group_id,role,perm_id), FOREIGN KEY(group_id) REFERENCES nl_groups (group_id) ON DELETE CASCADE, FOREIGN KEY(rank_id) REFERENCES nl_ranks (rank_id) ON DELETE CASCADE, FOREIGN KEY(perm_id) REFERENCES nl_global_permissions (perm_id) ON DELETE CASCADE)")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS group_invitation(player uuid NOT NULL, group_id INT(11) NOT NULL, rank_id INT(11) NOT NULL, PRIMARY KEY(group_id, player))")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS nl_inks (link_id INT(11) NOT NULL AUTO_INCREMENT, originating_group_id INT(11) NOT NULL, originating_rank_id INT(11) NOT NUL, target_group_id INT(11) NOT NUL, target_rank_id INT(11) NOT NUL, foreign key (originating_group_id, originating_type_id) REFERENCES nl_ranks(group_id, rank_id) on delete cascade, FOREIGN KEY (target_group_id, target_type_id) REFERENCES nl_ranks(group_id, rank_id) on delete cascade, unique (originating_group_id, originating_type_id, target_group_id, target_type_id), index(originating_group_id), index(target_group_id))")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS nl_global_actions(type_id INT(11) NOT NULL AUTO_INCREMENT, type_name text not null, PRIMARY KEY(id), CONSTRAINT UNIQUE unique(name))")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS nl_action_log (action_id INT(11) NOT NULL AUTO_INCREMENT, type_id INT(11) NOT NULL, player UUID NOT NULL, group_id INT(11) NOT NULL, time DATETIME NOT NULL, rank varchar(255) default null, name varchar(255) default null, extra text default null, PRIMARY KEY(action_id), FOREIGN KEY (type_id) REFERENCES nl_global_actions(type_id) on delete cascade,FOREIGN KEY (group_id) REFERENCES nl_groups(group_id) on delete cascade)")) {
				prep.execute();
			}

		} catch (SQLException e) {
			logger.error("Failed to setup tables and procedures", e);
			return false;
		}
		return true;
	}

	public Map<String, Map<Integer, List<LoggedGroupActionPersistence>>> loadAllGroupsLogs() {
		Map<String, Map<Integer, List<LoggedGroupActionPersistence>>> result = new HashMap<>();
		try (Connection insertConn = db.getConnection();
				PreparedStatement loadLog = insertConn
						.prepareStatement("select ga.name,al.player, al.group_id, al.time, al.rank, al.name, al.extra"
								+ " from nl_action_log al inner join nl_group_actions ga on al.type_id = ga.id");
				ResultSet rs = loadLog.executeQuery()) {
			while (rs.next()) {
				String actionName = rs.getString(1);
				String player = rs.getString(2);
				int groupId = rs.getInt(3);
				long time = rs.getTimestamp(4).getTime();
				String rank = rs.getString(5);
				String name = rs.getString(6);
				String extra = rs.getString(7);
				LoggedGroupActionPersistence persist = new LoggedGroupActionPersistence(time, UUID.fromString(player),
						rank, name, extra);
				Map<Integer, List<LoggedGroupActionPersistence>> forType = result.computeIfAbsent(actionName,
						s -> new HashMap<>());
				List<LoggedGroupActionPersistence> perGroup = forType.computeIfAbsent(groupId, s -> new ArrayList<>());
				perGroup.add(persist);
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Failed to load group logs", e);
			return null;
		}
		return result;
	}

	public int getOrCreateActionID(String name) {
		try (Connection insertConn = db.getConnection();
				PreparedStatement selectId = insertConn
						.prepareStatement("select id from nl_group_actions where name = ?;")) {
			selectId.setString(1, name);
			try (ResultSet rs = selectId.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Failed to check for existence of action type in db: " + e);
			return -1;
		}
		try (Connection insertConn = db.getConnection();
				PreparedStatement insertAction = insertConn.prepareStatement(
						"insert into nl_group_actions (name) values(?);", Statement.RETURN_GENERATED_KEYS);) {
			insertAction.setString(1, name);
			insertAction.execute();
			try (ResultSet rs = insertAction.getGeneratedKeys()) {
				if (!rs.next()) {
					logger.info("Failed to insert group log acion type");
					return -1;
				}
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Failed to insert action type into db:", e);
			return -1;
		}
	}

	public void insertActionLog(Group group, int typeID, LoggedGroupAction change) {
		try (Connection connection = db.getConnection();
				PreparedStatement addLog = connection.prepareStatement(
						"insert into nl_action_log(type_id, player, group_id, time,rank, name, extra) values(?,?,?,?,?,?,?)")) {
			LoggedGroupActionPersistence persist = change.getPersistence();
			addLog.setInt(1, typeID);
			addLog.setString(2, persist.getPlayer().toString());
			addLog.setInt(3, group.getPrimaryId());
			addLog.setTimestamp(4, new Timestamp(persist.getTimeStamp()));
			addLog.setString(5, persist.getRank());
			addLog.setString(6, persist.getName());
			addLog.setString(7, persist.getExtraText());
			addLog.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem inserting log", e);
		}
	}

	public int createGroup(String group, UUID creator) {
		try (Connection connection = db.getConnection();
				PreparedStatement createGroup = connection.prepareStatement("call createGroup(?,?)")) {
			createGroup.setString(1, group);
			createGroup.setString(2, creator.toString());
			createGroup.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem setting up query to create group " + group, e);
			return -1;
		}
		try (Connection connection = db.getConnection();
				PreparedStatement getGroup = connection
						.prepareStatement("select group_id from faction_id where group_name=?")) {
			getGroup.setString(1, group);
			try (ResultSet rs = getGroup.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
				return -1;
			}
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem setting up query to create group " + group, e);
			return -1;
		}
	}

	public void renameGroup(Group group, String newName) {
		try (Connection connection = db.getConnection();
				PreparedStatement renameGroup = connection
						.prepareStatement("update nl_groups set group_name = ? where group_id = ?")) {
			renameGroup.setString(1, newName);
			renameGroup.setInt(2, group.getPrimaryId());
			renameGroup.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem updating groupname", e);
		}
	}

	public void addMember(UUID member, Group group, GroupRank role) {
		try (Connection connection = db.getConnection();
				PreparedStatement addMember = connection
						.prepareStatement("insert into faction_member(group_id, rank_id, member_name) values(?,?,?)")) {
			addMember.setInt(1, group.getPrimaryId());
			addMember.setInt(2, role.getId());
			addMember.setString(3, member.toString());
			addMember.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN,
					"Problem adding " + member + " as " + role.toString() + " to group " + group.getName(), e);
		}
	}

	public void updateMember(UUID member, Group group, GroupRank role) {
		try (Connection connection = db.getConnection();
				PreparedStatement updateMember = connection.prepareStatement(
						"update faction_member set rank_id = ? where group_id = ? and member_name = ?")) {
			updateMember.setInt(1, role.getId());
			updateMember.setInt(2, group.getPrimaryId());
			updateMember.setString(3, member.toString());
			updateMember.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN,
					"Problem updating " + member + " as " + role.toString() + " for group " + group.getName(), e);
		}
	}

	public void removeMember(UUID member, Group group) {
		try (Connection connection = db.getConnection();
				PreparedStatement removeMember = connection
						.prepareStatement("delete from faction_member where member_name = ? and group_id = ?")) {
			removeMember.setString(1, member.toString());
			removeMember.setInt(2, group.getPrimaryId());
			removeMember.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem removing " + member + " from group " + group, e);
		}
	}

	public void addAllPermissions(int groupId, Map<GroupRank, List<PermissionType>> perms) {
		try (Connection connection = db.getConnection();
				PreparedStatement addPermissionById = connection
						.prepareStatement("insert into permissionByGroup(group_id,rank_id,perm_id) values(?, ?, ?)")) {
			for (Entry<GroupRank, List<PermissionType>> entry : perms.entrySet()) {
				int typeId = entry.getKey().getId();
				for (PermissionType perm : entry.getValue()) {
					addPermissionById.setInt(1, groupId);
					addPermissionById.setInt(2, typeId);
					addPermissionById.setInt(3, perm.getId());
					addPermissionById.addBatch();
				}
			}

			int[] res = addPermissionById.executeBatch();
			if (res == null) {
				logger.log(Level.WARN, "Failed to add all permissions to group {0}", groupId);
			} else {
				int count = 0;
				for (int r : res) {
					count += r;
				}
				logger.log(Level.INFO, "Added {0} of {1} permissions to group {2}",
						new Object[] { count, res.length, groupId });
			}
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem adding all permissions to group " + groupId, e);
		}
	}

	public void removeAllPermissions(Group g, Map<GroupRank, List<PermissionType>> perms) {
		int groupId = g.getPrimaryId();
		try (Connection connection = db.getConnection();
				PreparedStatement removePermissionById = connection.prepareStatement(
						"delete from permissionByGroup where group_id = ? and rank_id = ? and perm_id = ?")) {
			for (Entry<GroupRank, List<PermissionType>> entry : perms.entrySet()) {
				int typeId = entry.getKey().getId();
				for (PermissionType perm : entry.getValue()) {
					removePermissionById.setInt(1, groupId);
					removePermissionById.setInt(2, typeId);
					removePermissionById.setInt(3, perm.getId());
					removePermissionById.addBatch();
				}
			}

			int[] res = removePermissionById.executeBatch();
			if (res == null) {
				logger.log(Level.WARN, "Failed to remove all permissions from group {0}", groupId);
			} else {
				int cnt = 0;
				for (int r : res)
					cnt += r;
				logger.log(Level.INFO, "Removed {0} of {1} permissions from group {2}",
						new Object[] { cnt, res.length, groupId });
			}
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem removing all permissions from group " + groupId, e);
		}
	}

	public void addPermission(Group group, GroupRank type, PermissionType perm) {
		try (Connection connection = db.getConnection();
				PreparedStatement addPermission = connection
						.prepareStatement("insert into permissionByGroup(group_id,rank_id,perm_id) values(?, ?, ?)")) {
			addPermission.setInt(1, group.getPrimaryId());
			addPermission.setInt(2, type.getId());
			addPermission.setInt(3, perm.getId());
			addPermission.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem adding " + type + " with " + perm + " to group " + group.getName(), e);
		}
	}

	public Map<GroupRank, List<PermissionType>> getPermissions(Group group) {
		Map<GroupRank, List<PermissionType>> perms = new HashMap<>();
		try (Connection connection = db.getConnection();
				PreparedStatement getPermission = connection
						.prepareStatement("select rank_id, perm_id from permissionByGroup where group_id = ?")) {
			getPermission.setInt(1, group.getPrimaryId());
			GroupRankHandler handler = group.getGroupRankHandler();
			try (ResultSet set = getPermission.executeQuery();) {
				while (set.next()) {
					GroupRank type = handler.getRank(set.getInt(1));
					List<PermissionType> listPerm = perms.get(type);
					if (listPerm == null) {
						listPerm = new ArrayList<>();
						perms.put(type, listPerm);
					}
					int id = set.getInt(2);
					PermissionType perm = PermissionType.getPermission(id);
					if (perm != null && !listPerm.contains(perm)) {
						listPerm.add(perm);
					}
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem getting permissions for group " + group, e);
		}
		return perms;
	}

	public void removePermission(Group group, GroupRank pType, PermissionType perm) {
		try (Connection connection = db.getConnection();
				PreparedStatement removePermission = connection.prepareStatement(
						"delete from permissionByGroup where group_id = ? and rank_id = ? and perm_id = ?")) {
			removePermission.setInt(1, group.getPrimaryId());
			removePermission.setInt(2, pType.getId());
			removePermission.setInt(3, perm.getId());
			removePermission.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.WARN,
					"Problem removing permissions for group " + group + " on playertype " + pType.getName(), e);
		}
	}

	public void registerPermission(PermissionType perm) {
		try (Connection connection = db.getConnection();
				PreparedStatement registerPermission = connection
						.prepareStatement("insert into permissionIdMapping(perm_id, name) values(?, ?)")) {
			registerPermission.setInt(1, perm.getId());
			registerPermission.setString(2, perm.getName());
			registerPermission.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem register permission " + perm.getName(), e);
		}
	}

	public Map<Integer, String> getPermissionMapping() {
		Map<Integer, String> perms = new TreeMap<>();
		try (Connection connection = db.getConnection();
				Statement getPermissionMapping = connection.createStatement()) {
			try (ResultSet res = getPermissionMapping.executeQuery("select * from permissionIdMapping")) {
				while (res.next()) {
					perms.put(res.getInt(1), res.getString(2));
				}
			} catch (SQLException e) {
				logger.log(Level.WARN, "Problem getting permissions from db", e);
			}
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem forming statement to get permissions from db", e);
		}
		return perms;
	}

	public void registerPlayerType(Group g, GroupRank type) {
		try (Connection connection = db.getConnection();
				PreparedStatement addType = connection.prepareStatement(
						"insert into nl_group_ranks (group_id, rank_id, type_name, parent_rank_id) values(?,?,?,?)");) {
			addType.setInt(1, g.getPrimaryId());
			addType.setInt(2, type.getId());
			addType.setString(3, type.getName());
			if (type.getParent() == null) {
				addType.setNull(4, Types.INTEGER);
			} else {
				addType.setInt(4, type.getParent().getId());
			}
			addType.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem adding player type " + type.getName() + " for " + g.getName(), e);
		}
	}

	public void removePlayerType(Group g, GroupRank type) {
		try (Connection connection = db.getConnection();
				PreparedStatement removeType = connection
						.prepareStatement("delete from nl_group_ranks where group_id = ? and rank_id = ?");) {
			removeType.setInt(1, g.getPrimaryId());
			removeType.setInt(2, type.getId());
			removeType.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem removing player type " + type.getName() + " from " + g.getName(), e);
		}
	}

	public void updatePlayerTypeName(Group g, GroupRank type) {
		try (Connection connection = db.getConnection();
				PreparedStatement renameType = connection.prepareStatement(
						"update nl_group_ranks set type_name = ? where group_id = ? and rank_id = ?");) {
			renameType.setString(1, type.getName());
			renameType.setInt(2, g.getPrimaryId());
			renameType.setInt(3, type.getId());
			renameType.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem updating player type name " + type.getName() + " from " + g.getName(), e);
		}
	}

	public void mergeGroup(Group groupThatStays, Group groupToMerge) {
		try (Connection connection = db.getConnection();
				PreparedStatement mergeGroup = connection.prepareStatement("call mergeintogroup(?,?)");) {
			mergeGroup.setInt(1, groupThatStays.getPrimaryId());
			mergeGroup.setInt(2, groupToMerge.getPrimaryId());
			mergeGroup.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN,
					"Problem merging group " + groupToMerge.getName() + " into " + groupThatStays.getName(), e);
		}
	}

	public void addGroupInvitation(UUID uuid, Group group, GroupRank role) {
		try (Connection connection = db.getConnection();
				PreparedStatement addGroupInvitation = connection.prepareStatement(
						"insert into group_invitation(uuid, group_id, rank_id) values(?, ?, ?) on duplicate key update rank_id=values(rank_id), date=now()")) {
			addGroupInvitation.setString(1, uuid.toString());
			addGroupInvitation.setInt(2, group.getPrimaryId());
			addGroupInvitation.setInt(3, role.getId());
			addGroupInvitation.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.WARN,
					"Problem adding group " + group.getName() + " invite for " + uuid + " with role " + role, e);
		}
	}

	public void removeGroupInvitation(UUID uuid, Group group) {
		try (Connection connection = db.getConnection();
				PreparedStatement removeGroupInvitation = connection
						.prepareStatement("delete from group_invitation where uuid = ? and group_id = ?");) {
			removeGroupInvitation.setString(1, uuid.toString());
			removeGroupInvitation.setInt(2, group.getPrimaryId());
			removeGroupInvitation.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem removing group " + group.getName() + " invite for " + uuid, e);
		}
	}

	/**
	 * Use this method to load all invitations to all groups.
	 */
	public void loadGroupsInvitations() {
		try (Connection connection = db.getConnection();
				PreparedStatement loadGroupsInvitations = connection
						.prepareStatement("select uuid, group_id, rank_id from group_invitation");
				ResultSet set = loadGroupsInvitations.executeQuery();) {
			while (set.next()) {
				String uuid = set.getString(1);
				int groupId = set.getInt(2);
				int rankId = set.getInt(3);
				UUID playerUUID = UUID.fromString(uuid);
				Group g = GroupAPI.getGroup(groupId);
				GroupRank type = null;
				if (g != null) {
					type = g.getGroupRankHandler().getRank(rankId);
				}
				if (type != null) {
					g.addInvite(playerUUID, type);
					PlayerListener.addNotification(playerUUID, g);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem loading all group invitations", e);
		}
	}

	public void addLink(GroupLink link) {
		try (Connection insertConn = db.getConnection();
				PreparedStatement insertLink = insertConn.prepareStatement(
						"insert into nl_group_links (originating_group_id, originating_type_id, target_group_id, target_type_id) "
								+ "values(?,?, ?,?);",
						Statement.RETURN_GENERATED_KEYS)) {
			insertLink.setInt(1, link.getOriginatingGroup().getPrimaryId());
			insertLink.setInt(2, link.getOriginatingType().getId());
			insertLink.setInt(3, link.getTargetGroup().getPrimaryId());
			insertLink.setInt(4, link.getTargetType().getId());
			insertLink.execute();
			try (ResultSet rs = insertLink.getGeneratedKeys()) {
				if (!rs.next()) {
					throw new IllegalStateException("Inserting link did not generate an id");
				}
				link.setID(rs.getInt(1));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Failed to insert new link: ", e);
		}
	}

	public void removeLink(GroupLink link) {
		if (link.getID() == -1) {
			throw new IllegalStateException("Link id was not set");
		}
		try (Connection insertConn = db.getConnection();
				PreparedStatement removeLink = insertConn
						.prepareStatement("delete from nl_group_links where link_id = ?")) {
			removeLink.setInt(1, link.getID());
			removeLink.execute();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Failed to remove link: ", e);
		}
	}

	public GroupManager loadAllGroups() {
		Map<Integer, Group> groupById = new HashMap<>();
		Map<String, Group> groupByName = new HashMap<>();
		Map<Integer, JsonObject> groupMetaById = new HashMap<>();
		JsonParser jsonParser = new JsonParser();
		try (Connection connection = db.getConnection();
				PreparedStatement getGroups = connection
						.prepareStatement("select group_name, group_id, meta_data from faction_id");
				ResultSet groups = getGroups.executeQuery()) {
			while (groups.next()) {
				String name = groups.getString(1);
				Integer id = groups.getInt(2);
				String rawMeta = groups.getString(3);
				Group group = new Group(name, id);
				groupById.put(id, group);
				groupByName.put(name.toLowerCase(), group);
				if (rawMeta != null) {
					groupMetaById.put(id, (JsonObject) jsonParser.parse(rawMeta));
				} else {
					groupMetaById.put(id, new JsonObject());
				}
			}
		} catch (SQLException e) {
			logger.severe("Failed to load groups: " + e.toString());
			return null;
		}
		Map<Integer, Map<Integer, GroupRank>> retrievedTypes = new TreeMap<>();
		Map<Integer, Map<Integer, List<GroupRank>>> pendingParents = new TreeMap<>();
		// load all player types without linking them in any way initially
		try (Connection connection = db.getConnection();
				PreparedStatement getTypes = connection
						.prepareStatement("select type_name, group_id, rank_id, parent_rank_id from nl_group_ranks");
				ResultSet types = getTypes.executeQuery()) {
			while (types.next()) {
				String name = types.getString(1);
				int groupId = types.getInt(2);
				int rankId = types.getInt(3);
				int parentId = types.getInt(4);
				GroupRank type = new GroupRank(name, rankId, null, groupById.get(groupId));
				Map<Integer, GroupRank> typeMap = retrievedTypes.computeIfAbsent(groupId, i -> new TreeMap<>());
				typeMap.put(rankId, type);
				Map<Integer, List<GroupRank>> parentMap = pendingParents.computeIfAbsent(groupId, i -> new TreeMap<>());
				List<GroupRank> brothers = parentMap.computeIfAbsent(parentId, i -> new ArrayList<>());
				brothers.add(type);
			}
		} catch (SQLException e) {
			logger.severe("Failed to load player types: " + e.toString());
			return null;
		}
		// properly map player type children/parents
		for (Entry<Integer, Map<Integer, GroupRank>> entry : retrievedTypes.entrySet()) {
			Map<Integer, GroupRank> loadedTypes = entry.getValue();
			Map<Integer, List<GroupRank>> loadedParents = pendingParents.get(entry.getKey());
			Group group = groupById.get(entry.getKey());
			if (group == null) {
				logger.log(Level.WARN, "Found player types, but no group for id " + entry.getKey());
				continue;
			}
			GroupRank root = loadedTypes.get(GroupRankHandler.OWNER_ID);
			GroupRankHandler handler = new GroupRankHandler(root, group);
			Queue<GroupRank> toHandle = new LinkedList<>();
			toHandle.add(root);
			while (!toHandle.isEmpty()) {
				GroupRank parent = toHandle.poll();
				List<GroupRank> children = loadedParents.get(parent.getId());
				loadedParents.remove(parent.getId());
				if (children == null) {
					continue;
				}
				for (GroupRank child : children) {
					// parent is intentionally non modifiable, so we create a new instance
					GroupRank type = new GroupRank(child.getName(), child.getId(), parent, group);
					parent.addChild(type);
					handler.putRank(type);
					toHandle.add(type);
					loadedTypes.remove(type.getId());
				}
			}
			if (!loadedTypes.isEmpty()) {
				// a type exists for this group, which is not part of the tree rooted at perm 0
				logger.log(Level.WARN,
						"A total of " + loadedTypes.values().size() + " player types could not be loaded for group "
								+ group.getName() + ", because they arent part of the normal tree structure");
			}
			group.setGroupRankHandler(handler);
		}
		// load members
		try (Connection connection = db.getConnection();
				PreparedStatement getTypes = connection
						.prepareStatement("select group_id, member_name, rank_id from faction_member");
				ResultSet types = getTypes.executeQuery()) {
			while (types.next()) {
				int groupId = types.getInt(1);
				String memberUUIDString = types.getString(2);
				UUID member = null;
				if (memberUUIDString != null) {
					member = UUID.fromString(memberUUIDString);
				} else {
					logger.log(Level.WARN, "Found invalid uuid " + memberUUIDString + " for group " + groupId);
					continue;
				}
				int rankId = types.getInt(3);
				Group group = groupById.get(groupId);
				if (group == null) {
					logger.log(Level.WARN, "Couldnt not load group " + groupId + " from cache to add member");
					continue;
				}
				group.addToTracking(member, group.getGroupRankHandler().getRank(rankId));
			}
		} catch (SQLException e) {
			logger.severe("Failed to load group members: " + e.toString());
		}
		// load permissions
		try (Connection connection = db.getConnection();
				PreparedStatement getTypes = connection
						.prepareStatement("select group_id,rank_id,perm_id from permissionByGroup");
				ResultSet types = getTypes.executeQuery()) {
			while (types.next()) {
				int groupId = types.getInt(1);
				int rankId = types.getInt(2);
				int permId = types.getInt(3);
				PermissionType perm = PermissionType.getPermission(permId);
				Group group = groupById.get(groupId);
				if (group == null) {
					logger.log(Level.WARN, "Couldnt not load group " + groupId + " from cache to add permission");
					continue;
				}
				if (perm != null) {
					group.getGroupRankHandler().getRank(rankId).addPermission(perm, false);
				}
			}
		} catch (SQLException e) {
			logger.severe("Failed to load group permissions: " + e.toString());
		}
		// load merged group remapping
		try (Connection connection = db.getConnection();
				PreparedStatement getTypes = connection
						.prepareStatement("select oldGroup, newGroup from nl_merged_groups");
				ResultSet types = getTypes.executeQuery()) {
			while (types.next()) {
				int oldId = types.getInt(1);
				int newId = types.getInt(2);
				Group group = groupById.get(newId);
				if (group == null) {
					logger.log(Level.WARN, "Inconsistent mapping from " + oldId + " to " + newId + " found");
				} else {
					groupById.put(oldId, group);
				}
			}
		} catch (SQLException e) {
			logger.severe("Failed to merged group links " + e.toString());
		}
		// load group link
		try (Connection connection = db.getConnection();
				PreparedStatement getTypes = connection
						.prepareStatement("select link_id, originating_group_id, originating_type_id, "
								+ "target_group_id, target_type_id from nl_group_links");
				ResultSet links = getTypes.executeQuery()) {
			while (links.next()) {
				int linkId = links.getInt(1);
				int originatingGroupId = links.getInt(2);
				int originatingTypeId = links.getInt(3);
				int targetGroupId = links.getInt(4);
				int targetTypeId = links.getInt(5);
				Group originatingGroup = groupById.get(originatingGroupId);
				if (originatingGroup == null) {
					logger.log(Level.ERROR, "Link loaded had no group: " + linkId);
					continue;
				}
				Group targetGroup = groupById.get(targetGroupId);
				if (targetGroup == null) {
					logger.log(Level.ERROR, "Link loaded had no group: " + linkId);
					continue;
				}
				GroupRank originatingPlayerType = originatingGroup.getGroupRankHandler().getRank(originatingTypeId);
				if (originatingPlayerType == null) {
					logger.log(Level.ERROR, "Link loaded had no og type: " + linkId);
					continue;
				}
				GroupRank targetPlayerType = targetGroup.getGroupRankHandler().getRank(targetTypeId);
				if (targetPlayerType == null) {
					logger.log(Level.ERROR, "Link loaded had no target type: " + linkId);
					continue;
				}
				GroupLink link = new GroupLink(originatingGroup, originatingPlayerType, targetGroup, targetPlayerType);
				link.setID(linkId);
				originatingGroup.addOutgoingLink(link);
				targetGroup.addIncomingLink(link);
			}
		} catch (SQLException e) {
			logger.severe("Failed to load group links " + e.toString());
		}
		logger.log(Level.INFO, "Loaded a total of " + groupByName.size() + " groups with " + groupById.size()
				+ " ids from the database");
		GroupMetaDataAPI.offerRawMeta(groupMetaById);
		return new GroupManager(this, groupByName, groupById);
	}

	public boolean updateDatabase() {
		return db.updateDatabase();
	}
}
