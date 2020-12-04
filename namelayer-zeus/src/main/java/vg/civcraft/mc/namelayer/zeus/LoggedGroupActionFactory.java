package vg.civcraft.mc.namelayer.zeus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.impl.AcceptInvitation;
import vg.civcraft.mc.namelayer.core.log.impl.AddLink;
import vg.civcraft.mc.namelayer.core.log.impl.AddPermission;
import vg.civcraft.mc.namelayer.core.log.impl.BlacklistPlayer;
import vg.civcraft.mc.namelayer.core.log.impl.ChangeColor;
import vg.civcraft.mc.namelayer.core.log.impl.ChangeGroupName;
import vg.civcraft.mc.namelayer.core.log.impl.ChangeMemberRank;
import vg.civcraft.mc.namelayer.core.log.impl.ChangeRankName;
import vg.civcraft.mc.namelayer.core.log.impl.CreateGroup;
import vg.civcraft.mc.namelayer.core.log.impl.CreateRank;
import vg.civcraft.mc.namelayer.core.log.impl.DeleteRank;
import vg.civcraft.mc.namelayer.core.log.impl.InviteMember;
import vg.civcraft.mc.namelayer.core.log.impl.JoinGroup;
import vg.civcraft.mc.namelayer.core.log.impl.LeaveGroup;
import vg.civcraft.mc.namelayer.core.log.impl.MergeGroup;
import vg.civcraft.mc.namelayer.core.log.impl.RejectInvite;
import vg.civcraft.mc.namelayer.core.log.impl.RemoveLink;
import vg.civcraft.mc.namelayer.core.log.impl.RemoveMember;
import vg.civcraft.mc.namelayer.core.log.impl.RemovePermission;
import vg.civcraft.mc.namelayer.core.log.impl.RevokeInvite;
import vg.civcraft.mc.namelayer.core.log.impl.SetPassword;

public class LoggedGroupActionFactory {

	private Map<String, Function<LoggedGroupActionPersistence, LoggedGroupAction>> logInstanciators;
	private Map<String, Integer> identifierToInternalId;
	private NameLayerDAO dao;
	private Logger logger;

	public LoggedGroupActionFactory(NameLayerDAO dao, Logger logger) {
		this.dao = dao;
		this.logger = logger;
		this.logInstanciators = new HashMap<>();
		this.identifierToInternalId = new HashMap<>();
		registerNameLayerProviders();
	}

	public LoggedGroupAction produceAction(String identifier, LoggedGroupActionPersistence persist) {
		Function<LoggedGroupActionPersistence, LoggedGroupAction> instanciator = logInstanciators.get(identifier);
		if (instanciator == null) {
			return null;
		}
		return instanciator.apply(persist);
	}

	public void persist(Group group, LoggedGroupAction action) {
		Integer id = identifierToInternalId.get(action.getIdentifier());
		if (id != null) {
			dao.insertActionLog(group, id, action);
		} else {
			logger.error("No id was found for group log of type " + action.getIdentifier());
		}
	}

	public void registerProvider(String identifier,
			Function<LoggedGroupActionPersistence, LoggedGroupAction> instanciator) {
		if (identifierToInternalId.containsKey(identifier)) {
			throw new IllegalArgumentException("Log type " + identifier + " was already registered");
		}
		int id = dao.getOrCreateActionID(identifier);
		if (id == -1) {
			// this is pretty bad, but was already logged sufficiently at DAO level
			return;
		}
		logInstanciators.put(identifier, instanciator);
		identifierToInternalId.put(identifier, id);
	}
	
	private void registerNameLayerProviders() {
		registerProvider(AcceptInvitation.ID, AcceptInvitation::load);
		registerProvider(AddLink.ID, AddLink::load);
		registerProvider(AddPermission.ID, AddPermission::load);
		registerProvider(BlacklistPlayer.ID, BlacklistPlayer::load);
		registerProvider(ChangeColor.ID, ChangeColor::load);
		registerProvider(ChangeGroupName.ID, ChangeGroupName::load);
		registerProvider(ChangeMemberRank.ID, ChangeMemberRank::load);
		registerProvider(ChangeRankName.ID, ChangeRankName::load);
		registerProvider(CreateGroup.ID, CreateGroup::load);
		registerProvider(CreateRank.ID, CreateRank::load);
		registerProvider(DeleteRank.ID, DeleteRank::load);
		registerProvider(InviteMember.ID, InviteMember::load);
		registerProvider(JoinGroup.ID, JoinGroup::load);
		registerProvider(LeaveGroup.ID, LeaveGroup::load);
		registerProvider(MergeGroup.ID, MergeGroup::load);
		registerProvider(RejectInvite.ID, RejectInvite::load);
		registerProvider(RemoveLink.ID, RemoveLink::load);
		registerProvider(RemoveMember.ID, RemoveMember::load);
		registerProvider(RemovePermission.ID, RemovePermission::load);
		registerProvider(RevokeInvite.ID, RevokeInvite::load);
		registerProvider(SetPassword.ID, SetPassword::load);
	}
}
