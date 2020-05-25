package vg.civcraft.mc.namelayer.group.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.database.GroupManagerDao;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.group.log.abstr.LoggedGroupAction;
import vg.civcraft.mc.namelayer.group.log.impl.AcceptInvitation;
import vg.civcraft.mc.namelayer.group.log.impl.AddLink;
import vg.civcraft.mc.namelayer.group.log.impl.AddPermission;
import vg.civcraft.mc.namelayer.group.log.impl.ChangeColor;
import vg.civcraft.mc.namelayer.group.log.impl.ChangeGroupName;
import vg.civcraft.mc.namelayer.group.log.impl.ChangeMemberRank;
import vg.civcraft.mc.namelayer.group.log.impl.ChangeRankName;
import vg.civcraft.mc.namelayer.group.log.impl.CreateGroup;
import vg.civcraft.mc.namelayer.group.log.impl.CreateRank;
import vg.civcraft.mc.namelayer.group.log.impl.DeleteRank;
import vg.civcraft.mc.namelayer.group.log.impl.InviteMember;
import vg.civcraft.mc.namelayer.group.log.impl.JoinGroup;
import vg.civcraft.mc.namelayer.group.log.impl.LeaveGroup;
import vg.civcraft.mc.namelayer.group.log.impl.MergeGroup;
import vg.civcraft.mc.namelayer.group.log.impl.RejectInvite;
import vg.civcraft.mc.namelayer.group.log.impl.RemoveLink;
import vg.civcraft.mc.namelayer.group.log.impl.RemoveMember;
import vg.civcraft.mc.namelayer.group.log.impl.RemovePermission;
import vg.civcraft.mc.namelayer.group.log.impl.RevokeInvite;
import vg.civcraft.mc.namelayer.group.log.impl.SetPassword;

public class LoggedGroupActionFactory {

	private Map<String, Function<LoggedGroupActionPersistence, LoggedGroupAction>> logInstanciators;
	private Map<String, Integer> identifierToInternalId;
	private Map<String, Map<Integer, List<LoggedGroupActionPersistence>>> unloadedLogs;

	public LoggedGroupActionFactory(GroupManagerDao dao) {
		this.logInstanciators = new HashMap<>();
		this.identifierToInternalId = new HashMap<>();
		this.unloadedLogs = new HashMap<>();
		registerNameLayerProviders();
		loadLogs();
	}
	
	private void loadLogs() {
		//TODO TODO load and shit
	}

	public LoggedGroupAction produceAction(String identifier, LoggedGroupActionPersistence persist) {
		Function<LoggedGroupActionPersistence, LoggedGroupAction> instanciator = logInstanciators.get(identifier);
		if (instanciator == null) {
			return null;
		}
		return instanciator.apply(persist);
	}

	public void registerProvider(String identifier,
			Function<LoggedGroupActionPersistence, LoggedGroupAction> instanciator) {
		if (identifierToInternalId.containsKey(identifier)) {
			throw new IllegalArgumentException("Log type " + identifier + " was already registered");
		}
		int id = NameLayerPlugin.getInstance().getGroupManagerDao().getOrCreateActionID(identifier);
		if (id == -1) {
			// this is pretty bad, but was already logged sufficiently at DAO level
			return;
		}
		logInstanciators.put(identifier, instanciator);
		identifierToInternalId.put(identifier, id);
		Map<Integer, List<LoggedGroupActionPersistence>> actionToLoad = unloadedLogs.get(identifier);
		if (actionToLoad == null) {
			return;
		}
		for (Entry<Integer, List<LoggedGroupActionPersistence>> entry : actionToLoad.entrySet()) {
			Group group = GroupAPI.getGroupById(entry.getKey());
			if (group == null) {
				continue;
			}
			boolean addedAny = false;
			for (LoggedGroupActionPersistence log : entry.getValue()) {
				LoggedGroupAction action = instanciator.apply(log);
				if (action != null) {
					group.getActionLog().addAction(action, false);
					addedAny = true;
				}
			}
			if (addedAny) {
				group.getActionLog().sortLog();
			}
		}
	}

	private void registerNameLayerProviders() {
		registerProvider(AcceptInvitation.ID, AcceptInvitation::new);
		registerProvider(AddLink.ID, AddLink::new);
		registerProvider(AddPermission.ID, AddPermission::new);
		registerProvider(ChangeColor.ID, ChangeColor::new);
		registerProvider(ChangeGroupName.ID, ChangeGroupName::new);
		registerProvider(ChangeMemberRank.ID, ChangeMemberRank::new);
		registerProvider(ChangeRankName.ID, ChangeRankName::new);
		registerProvider(CreateGroup.ID, CreateGroup::new);
		registerProvider(CreateRank.ID, CreateRank::new);
		registerProvider(DeleteRank.ID, DeleteRank::new);
		registerProvider(InviteMember.ID, InviteMember::new);
		registerProvider(JoinGroup.ID, JoinGroup::new);
		registerProvider(LeaveGroup.ID, LeaveGroup::new);
		registerProvider(MergeGroup.ID, MergeGroup::new);
		registerProvider(RejectInvite.ID, RejectInvite::new);
		registerProvider(RemoveLink.ID, RemoveLink::new);
		registerProvider(RemoveMember.ID, RemoveMember::new);
		registerProvider(RemovePermission.ID, RemovePermission::new);
		registerProvider(RevokeInvite.ID, RevokeInvite::new);
		registerProvider(SetPassword.ID, SetPassword::new);
	}

}
