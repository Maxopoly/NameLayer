package vg.civcraft.mc.namelayer.zeus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import com.github.maxopoly.zeus.plugin.ZeusPluginDatabase;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupLink;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.PermissionTracker;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;

public class NameLayerDAO extends ZeusPluginDatabase {

	public NameLayerDAO(String name, Logger logger) {
		super(name, logger);
		registerMigrations();
	}

	private void registerMigrations() {
		registerMigration(1,
				"CREATE TABLE IF NOT EXISTS nl_groups (group_id SERIAL NOT NULL, "
						+ "group_name VARCHAR(32) NOT NULL UNIQUE, PRIMARY KEY(group_id))",

				"CREATE UNIQUE INDEX lower_group_name ON nl_groups ((lower(group_name)))",

				"CREATE TABLE IF NOT EXISTS nl_ranks(group_id INT NOT NULL, rank_id INT NOT NULL, "
						+ "rank_name VARCHAR(32) NOT NULL, parent_rank_id INT, UNIQUE (group_id, rank_id), "
						+ " UNIQUE (group_id, rank_name), PRIMARY KEY(group_id,rank_id), "
						+ "CONSTRAINT fk_gid FOREIGN KEY(group_id) REFERENCES nl_groups (group_id) ON DELETE CASCADE)",

				"CREATE TABLE IF NOT EXISTS nl_members(group_id INT NOT NULL, player UUID NOT NULL, "
						+ "rank_id INT NOT NULL, UNIQUE (group_id,player), "
						+ "CONSTRAINT fk_rid FOREIGN KEY(group_id, rank_id) REFERENCES nl_ranks (group_id, rank_id) ON DELETE CASCADE)",

				"CREATE INDEX nl_members_player on nl_members (player)",

				"CREATE TABLE IF NOT EXISTS nl_merged_groups (old_group_id INT NOT NULL, new_group_id INT NOT NULL, "
						+ "CONSTRAINT fk_ngid FOREIGN KEY(new_group_id) REFERENCES nl_groups (group_id) ON DELETE CASCADE, PRIMARY KEY(old_group_id))",

				"CREATE TABLE IF NOT EXISTS nl_global_permissions(perm_id SERIAL NOT NULL, perm_name TEXT not null, "
						+ "PRIMARY KEY(perm_id), UNIQUE (perm_name))",

				"CREATE TABLE IF NOT EXISTS nl_group_permissions(group_id INT NOT NULL, rank_id INT NOT NULL, "
						+ "perm_id INT NOT NULL, PRIMARY KEY(group_id,rank_id,perm_id), "
						+ "CONSTRAINT fk_rid FOREIGN KEY(group_id, rank_id) REFERENCES nl_ranks (group_id, rank_id) ON DELETE CASCADE, "
						+ "CONSTRAINT fk_pid FOREIGN KEY(perm_id) REFERENCES nl_global_permissions (perm_id) ON DELETE CASCADE)",

				"CREATE TABLE IF NOT EXISTS nl_invitations(player uuid NOT NULL, group_id INT NOT NULL, "
						+ "rank_id INT NOT NULL, PRIMARY KEY(group_id, player))",

				"CREATE TABLE IF NOT EXISTS nl_links (originating_group_id INT NOT NULL, "
						+ "originating_rank_id INT NOT NULL, target_group_id INT NOT NULL, "
						+ "target_rank_id INT NOT NULL, "
						+ "CONSTRAINT fk_og FOREIGN KEY (originating_group_id, originating_rank_id) REFERENCES nl_ranks(group_id, rank_id) on delete cascade, "
						+ "CONSTRAINT fk_tar FOREIGN KEY (target_group_id, target_rank_id) REFERENCES nl_ranks(group_id, rank_id) on delete cascade, "
						+ "UNIQUE (originating_group_id, originating_rank_id, target_group_id, target_rank_id))",

				"CREATE INDEX nl_links_orig on nl_links (originating_group_id)",
				"CREATE INDEX nl_links_target on nl_links (target_group_id)",

				"CREATE TABLE IF NOT EXISTS nl_global_actions(type_id SERIAL NOT NULL, "
						+ "type_name text not null, PRIMARY KEY(type_id), UNIQUE (type_name))",

				"CREATE TABLE IF NOT EXISTS nl_action_log (action_id SERIAL NOT NULL, "
						+ "type_id INT NOT NULL, player UUID NOT NULL, group_id INT NOT NULL, "
						+ "time TIMESTAMP NOT NULL, rank varchar(255) default null, name varchar(255) default null, "
						+ "extra TEXT DEFAULT NULL, PRIMARY KEY(action_id), "
						+ "CONSTRAINT fk_glo FOREIGN KEY (type_id) REFERENCES nl_global_actions(type_id) on delete cascade,"
						+ "CONSTRAINT fk_gid FOREIGN KEY (group_id) REFERENCES nl_groups(group_id) on delete cascade)"

		);
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
			logger.error("Failed to load group logs", e);
			return null;
		}
		return result;
	}

	public int getOrCreateActionID(String name) {
		try (Connection insertConn = db.getConnection();
				PreparedStatement selectId = insertConn
						.prepareStatement("select id from nl_global_actions where name = ?;")) {
			selectId.setString(1, name);
			try (ResultSet rs = selectId.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.error("Failed to check for existence of action type in db", e);
			return -1;
		}
		try (Connection insertConn = db.getConnection();
				PreparedStatement insertAction = insertConn.prepareStatement(
						"insert into nl_global_actions (name) values(?);", Statement.RETURN_GENERATED_KEYS);) {
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
			logger.error("Failed to insert action type into db:", e);
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

	public Group createGroup(String groupName, UUID creator, Collection<PermissionType> allPerms) {
		int groupID = -1;
		try (Connection connection = db.getConnection();
				PreparedStatement registerPermission = connection.prepareStatement(
						"insert into nl_groups(group_name) values(?)", Statement.RETURN_GENERATED_KEYS)) {
			registerPermission.setString(1, groupName);
			registerPermission.executeUpdate();
			try (ResultSet rs = registerPermission.getGeneratedKeys()) {
				rs.next();
				groupID = rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.error("Problem creating group " + groupName, e);
			return null;
		}
		Group group = new Group(groupName, groupID);
		Map<GroupRank, List<PermissionType>> permsToSave = new HashMap<>();
		GroupRank owner = new GroupRank("Owner", GroupRankHandler.OWNER_ID, null);
		GroupRankHandler handler = new GroupRankHandler(owner);
		group.setGroupRankHandler(handler);
		GroupRank admin = new GroupRank("Admin", GroupRankHandler.DEFAULT_ADMIN_ID, owner);
		handler.putRank(admin);
		owner.addChild(admin);
		GroupRank mod = new GroupRank("Mod", GroupRankHandler.DEFAULT_MOD_ID, admin);
		handler.putRank(mod);
		admin.addChild(mod);
		GroupRank member = new GroupRank("Member", GroupRankHandler.DEFAULT_MEMBER_ID, mod);
		handler.putRank(member);
		mod.addChild(member);
		GroupRank defaultNonMember = new GroupRank("Default", GroupRankHandler.DEFAULT_NON_MEMBER_ID, owner);
		handler.putRank(defaultNonMember);
		owner.addChild(defaultNonMember);
		GroupRank blacklisted = new GroupRank("Blacklisted", GroupRankHandler.DEFAULT_BLACKLIST_ID, defaultNonMember);
		handler.putRank(blacklisted);
		defaultNonMember.addChild(blacklisted);
		for (GroupRank rank : handler.getAllRanks()) {
			createRank(group, rank);
		}
		for (GroupRank rank : handler.getAllRanks()) {
			if (rank == owner) {
				continue;
			}
			List<PermissionType> permList = new ArrayList<>();
			for (PermissionType perm : allPerms) {
				if (perm.getDefaultPermLevels().getAllowedRankIds().contains(rank.getId())) {
					rank.addPermission(perm);
					permList.add(perm);
				}
			}
			permsToSave.put(rank, permList);
		}
		handler.setDefaultPasswordJoinRank(member);
		handler.setDefaultInvitationRank(member);
		addAllPermissions(groupID, permsToSave);
		return group;
	}

	public void deleteGroup(Group group) {
		try (Connection connection = db.getConnection();
				PreparedStatement delGrp = connection.prepareStatement("delete from nl_groups where group_id = ?")) {
			delGrp.setInt(1, group.getPrimaryId());
			delGrp.execute();
		} catch (SQLException e) {
			logger.log(Level.WARN, "Problem deleting group", e);
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
			logger.error("Problem updating groupname", e);
		}
	}

	public List<Integer> getGroupsByPlayer(UUID player) {
		try (Connection connection = db.getConnection();
				PreparedStatement getGroups = connection
						.prepareStatement("select group_id from nl_members where player = ?")) {
			getGroups.setObject(1, player);
			try (ResultSet rs = getGroups.executeQuery()) {
				List<Integer> result = new ArrayList<>();
				while (rs.next()) {
					result.add(rs.getInt(1));
				}
				return result;
			}
		} catch (SQLException e) {
			logger.error("Problem adding loading all groups for player " + player, e);
			return Collections.emptyList();
		}
	}

	public void addMember(UUID member, Group group, GroupRank role) {
		try (Connection connection = db.getConnection();
				PreparedStatement addMember = connection
						.prepareStatement("insert into nl_members(group_id, rank_id, player) values(?,?,?)")) {
			addMember.setInt(1, group.getPrimaryId());
			addMember.setInt(2, role.getId());
			addMember.setObject(3, member);
			addMember.execute();
		} catch (SQLException e) {
			logger.error("Problem adding " + member + " as " + role.toString() + " to group " + group.getName(), e);
		}
	}

	public void updateMember(UUID member, Group group, GroupRank role) {
		try (Connection connection = db.getConnection();
				PreparedStatement updateMember = connection
						.prepareStatement("update nl_members set rank_id = ? where group_id = ? and player = ?")) {
			updateMember.setInt(1, role.getId());
			updateMember.setInt(2, group.getPrimaryId());
			updateMember.setObject(3, member);
			updateMember.execute();
		} catch (SQLException e) {
			logger.error("Problem updating " + member + " as " + role.toString() + " for group " + group.getName(), e);
		}
	}

	public void removeMember(UUID member, Group group) {
		try (Connection connection = db.getConnection();
				PreparedStatement removeMember = connection
						.prepareStatement("delete from nl_members where player = ? and group_id = ?")) {
			removeMember.setString(1, member.toString());
			removeMember.setInt(2, group.getPrimaryId());
			removeMember.execute();
		} catch (SQLException e) {
			logger.error("Problem removing " + member + " from group " + group, e);
		}
	}

	public void addAllPermissions(int groupId, Map<GroupRank, List<PermissionType>> perms) {
		try (Connection connection = db.getConnection();
				PreparedStatement addPermissionById = connection.prepareStatement(
						"insert into nl_group_permissions(group_id,rank_id,perm_id) values(?, ?, ?)")) {
			for (Entry<GroupRank, List<PermissionType>> entry : perms.entrySet()) {
				int typeId = entry.getKey().getId();
				for (PermissionType perm : entry.getValue()) {
					addPermissionById.setInt(1, groupId);
					addPermissionById.setInt(2, typeId);
					addPermissionById.setInt(3, perm.getId());
					addPermissionById.addBatch();
				}
			}
			addPermissionById.executeBatch();
		} catch (SQLException e) {
			logger.error("Problem adding all permissions to group " + groupId, e);
		}
	}

	public void addPermission(Group group, GroupRank type, PermissionType perm) {
		try (Connection connection = db.getConnection();
				PreparedStatement addPermission = connection.prepareStatement(
						"insert into nl_group_permissions(group_id,rank_id,perm_id) values(?, ?, ?)")) {
			addPermission.setInt(1, group.getPrimaryId());
			addPermission.setInt(2, type.getId());
			addPermission.setInt(3, perm.getId());
			addPermission.execute();
		} catch (SQLException e) {
			logger.error("Problem adding " + type + " with " + perm + " to group " + group.getName(), e);
		}
	}

	public Map<GroupRank, List<Integer>> getPermissions(Group group) {
		Map<GroupRank, List<Integer>> perms = new HashMap<>();
		try (Connection connection = db.getConnection();
				PreparedStatement getPermission = connection
						.prepareStatement("select rank_id, perm_id from nl_group_permissions where group_id = ?")) {
			getPermission.setInt(1, group.getPrimaryId());
			GroupRankHandler handler = group.getGroupRankHandler();
			try (ResultSet set = getPermission.executeQuery();) {
				while (set.next()) {
					GroupRank type = handler.getRank(set.getInt(1));
					List<Integer> listPerm = perms.computeIfAbsent(type, r -> new ArrayList<>());
					int id = set.getInt(2);
					listPerm.add(id);
				}
			}
		} catch (SQLException e) {
			logger.error("Problem getting permissions for group " + group, e);
		}
		return perms;
	}

	public void removePermission(Group group, GroupRank pType, PermissionType perm) {
		try (Connection connection = db.getConnection();
				PreparedStatement removePermission = connection.prepareStatement(
						"delete from nl_group_permissions where group_id = ? and rank_id = ? and perm_id = ?")) {
			removePermission.setInt(1, group.getPrimaryId());
			removePermission.setInt(2, pType.getId());
			removePermission.setInt(3, perm.getId());
			removePermission.executeUpdate();
		} catch (SQLException e) {
			logger.error("Problem removing permissions for group " + group + " on playertype " + pType.getName(), e);
		}
	}

	public void registerPermission(PermissionType perm) {
		try (Connection insertConn = db.getConnection();
				PreparedStatement selectId = insertConn
						.prepareStatement("select perm_id from nl_global_permissions where perm_name = ?;")) {
			selectId.setString(1, perm.getName());
			try (ResultSet rs = selectId.executeQuery()) {
				if (rs.next()) {
					int id = rs.getInt(1);
					perm.setID(id);
					return;
				}
			}
		} catch (SQLException e) {
			logger.error("Failed to check for existence of action type in db", e);
			perm.setID(-1);
			return;
		}
		try (Connection connection = db.getConnection();
				PreparedStatement registerPermission = connection.prepareStatement(
						"insert into nl_global_permissions(perm_name) values(?)", Statement.RETURN_GENERATED_KEYS)) {
			registerPermission.setString(1, perm.getName());
			registerPermission.executeUpdate();
			try (ResultSet rs = registerPermission.getGeneratedKeys()) {
				rs.next();
				int id = rs.getInt(1);
				perm.setID(id);
			}
		} catch (SQLException e) {
			logger.error("Problem registering permission " + perm.getName(), e);
			perm.setID(-1);
		}
	}

	public int getGroupIdForName(String name) {
		name = name.toLowerCase();
		try (Connection connection = db.getConnection();
				PreparedStatement getGroupId = connection
						.prepareStatement("select group_id from nl_groups where lower(group_name) = ?");) {
			getGroupId.setString(1, name);
			try (ResultSet rs = getGroupId.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				} else {
					return -1;
				}
			}
		} catch (SQLException e) {
			logger.error("Problem getting group id", e);
			return -1;
		}
	}

	public Map<Integer, String> getPermissionMapping() {
		Map<Integer, String> perms = new TreeMap<>();
		try (Connection connection = db.getConnection();
				Statement getPermissionMapping = connection.createStatement();
				ResultSet res = getPermissionMapping
						.executeQuery("select perm_id, perm_name from nl_global_permissions")) {
			while (res.next()) {
				perms.put(res.getInt(1), res.getString(2));
			}
		} catch (SQLException e) {
			logger.error("Problem getting permissions from db", e);
		}
		return perms;
	}

	public void createRank(Group g, GroupRank type) {
		try (Connection connection = db.getConnection();
				PreparedStatement addType = connection.prepareStatement(
						"insert into nl_ranks (group_id, rank_id, rank_name, parent_rank_id) values(?,?,?,?)");) {
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
			logger.error("Problem adding player type " + type.getName() + " for " + g.getName(), e);
		}
	}

	public void deleteRank(Group g, GroupRank type) {
		try (Connection connection = db.getConnection();
				PreparedStatement removeType = connection
						.prepareStatement("delete from nl_ranks where group_id = ? and rank_id = ?");) {
			removeType.setInt(1, g.getPrimaryId());
			removeType.setInt(2, type.getId());
			removeType.execute();
		} catch (SQLException e) {
			logger.error("Problem removing player type " + type.getName() + " from " + g.getName(), e);
		}
	}

	public void updateRankName(Group g, GroupRank type) {
		try (Connection connection = db.getConnection();
				PreparedStatement renameType = connection
						.prepareStatement("update nl_ranks set type_name = ? where group_id = ? and rank_id = ?");) {
			renameType.setString(1, type.getName());
			renameType.setInt(2, g.getPrimaryId());
			renameType.setInt(3, type.getId());
			renameType.execute();
		} catch (SQLException e) {
			logger.error("Problem updating player type name " + type.getName() + " from " + g.getName(), e);
		}
	}

	public void addGroupInvitation(UUID uuid, Group group, GroupRank role) {
		try (Connection connection = db.getConnection();
				PreparedStatement addGroupInvitation = connection
						.prepareStatement("insert into nl_invitations(player, group_id, rank_id) values(?, ?, ?)")) {
			addGroupInvitation.setObject(1, uuid);
			addGroupInvitation.setInt(2, group.getPrimaryId());
			addGroupInvitation.setInt(3, role.getId());
			addGroupInvitation.executeUpdate();
		} catch (SQLException e) {
			logger.error("Problem adding group " + group.getName() + " invite for " + uuid + " with role " + role, e);
		}
	}

	public void removeGroupInvitation(UUID uuid, Group group) {
		try (Connection connection = db.getConnection();
				PreparedStatement removeGroupInvitation = connection
						.prepareStatement("delete from nl_invitations where player = ? and group_id = ?");) {
			removeGroupInvitation.setObject(1, uuid);
			removeGroupInvitation.setInt(2, group.getPrimaryId());
			removeGroupInvitation.executeUpdate();
		} catch (SQLException e) {
			logger.error("Problem removing group " + group.getName() + " invite for " + uuid, e);
		}
	}

	public void addLink(GroupLink link) {
		try (Connection insertConn = db.getConnection();
				PreparedStatement insertLink = insertConn.prepareStatement(
						"insert into nl_links (originating_group_id, originating_type_id, target_group_id, target_type_id) "
								+ "values(?,?, ?,?);")) {
			insertLink.setInt(1, link.getOriginatingGroup().getPrimaryId());
			insertLink.setInt(2, link.getOriginatingRank().getId());
			insertLink.setInt(3, link.getTargetGroup().getPrimaryId());
			insertLink.setInt(4, link.getTargetRank().getId());
			insertLink.execute();
		} catch (SQLException e) {
			logger.error("Failed to insert new link: ", e);
		}
	}

	public void removeLink(GroupLink link) {
		try (Connection insertConn = db.getConnection();
				PreparedStatement removeLink = insertConn.prepareStatement(
						"delete from nl_links where originating_group_id = ? and target_group_id = ? and originating_rank_id = ? and target_rank_id = ?")) {
			removeLink.setInt(1, link.getOriginatingGroup().getPrimaryId());
			removeLink.setInt(2, link.getTargetGroup().getPrimaryId());
			removeLink.setInt(3, link.getOriginatingRank().getId());
			removeLink.setInt(4, link.getTargetRank().getId());
			removeLink.execute();
		} catch (SQLException e) {
			logger.error("Failed to remove link: ", e);
		}
	}

	public Group getGroup(int id, PermissionTracker permTracker) {
		String name;
		try (Connection connection = db.getConnection();
				PreparedStatement getGroups = connection
						.prepareStatement("select group_name from nl_groups where group_id = ?")) {
			getGroups.setInt(1, id);
			try (ResultSet rs = getGroups.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				name = rs.getString(1);
			}
		} catch (SQLException e) {
			logger.error("Failed to load groups", e);
			return null;
		}
		Group group = new Group(name, id);
		Map<Integer, GroupRank> retrievedRanks = new TreeMap<>();
		Map<Integer, List<GroupRank>> parentMapping = new TreeMap<>();
		// load all player types without linking them in any way initially
		try (Connection connection = db.getConnection();
				PreparedStatement getTypes = connection.prepareStatement(
						"select rank_name, rank_id, parent_rank_id from nl_ranks where group_id = ?")) {
			getTypes.setInt(1, group.getPrimaryId());
			try (ResultSet types = getTypes.executeQuery()) {
				while (types.next()) {
					String rankName = types.getString(1);
					int rankId = types.getInt(2);
					int parentId = types.getInt(3);
					boolean noParent = types.wasNull();
					GroupRank rank = new GroupRank(rankName, rankId, null);
					retrievedRanks.put(rankId, rank);
					if (!noParent) {
						List<GroupRank> brothers = parentMapping.computeIfAbsent(parentId, i -> new ArrayList<>());
						brothers.add(rank);
					}
				}
			}
		} catch (SQLException e) {
			logger.error("Failed to load group rank", e);
			return null;
		}

		// properly map player type children/parents
		GroupRank root = retrievedRanks.remove(GroupRankHandler.OWNER_ID);
		GroupRankHandler handler = new GroupRankHandler(root);
		Queue<GroupRank> toHandle = new LinkedList<>();
		toHandle.add(root);
		while (!toHandle.isEmpty()) {
			GroupRank parent = toHandle.poll();
			List<GroupRank> children = parentMapping.remove(parent.getId());
			if (children == null) {
				continue;
			}
			for (GroupRank child : children) {
				child.setParent(parent);
				parent.addChild(child);
				handler.putRank(child);
				toHandle.add(child);
				retrievedRanks.remove(child.getId());
			}
		}
		if (!retrievedRanks.isEmpty()) {
			// a type exists for this group, which is not part of the tree rooted at perm 0
			logger.error("A total of " + retrievedRanks.values().size() + " player types could not be loaded for group "
					+ group.getName() + ", because they arent part of the normal tree structure");
		}
		group.setGroupRankHandler(handler);
		// load members
		try (Connection connection = db.getConnection();
				PreparedStatement getTypes = connection
						.prepareStatement("select player, rank_id from nl_members where group_id = ?")) {
			getTypes.setInt(1, id);
			try (ResultSet rs = getTypes.executeQuery()) {
				while (rs.next()) {
					UUID member = (UUID) rs.getObject(1);
					int rankId = rs.getInt(2);
					group.addToTracking(member, handler.getRank(rankId));
				}
			}
		} catch (SQLException e) {
			logger.error("Failed to load group members: ", e);
		}
		//load invites
		try (Connection connection = db.getConnection();
				PreparedStatement getTypes = connection
						.prepareStatement("select player, rank_id from nl_invitations where group_id = ?")) {
			getTypes.setInt(1, id);
			try (ResultSet rs = getTypes.executeQuery()) {
				while (rs.next()) {
					UUID invited = (UUID) rs.getObject(1);
					int rankId = rs.getInt(2);
					group.addInvite(invited, handler.getRank(rankId));
				}
			}
		} catch (SQLException e) {
			logger.error("Failed to load group invitations: ", e);
		}
		// load permissions
		try (Connection connection = db.getConnection();
				PreparedStatement getTypes = connection
						.prepareStatement("select rank_id,perm_id from nl_group_permissions where group_id = ?")) {
			getTypes.setInt(1, id);
			try (ResultSet types = getTypes.executeQuery()) {
				while (types.next()) {
					int rankId = types.getInt(1);
					int permId = types.getInt(2);
					PermissionType perm = permTracker.getPermission(permId);
					if (perm != null) {
						handler.getRank(rankId).addPermission(perm);
					}
				}
			}
		} catch (SQLException e) {
			logger.error("Failed to load group permissions: ", e);
		}
		return group;
	}
}
