package vg.civcraft.mc.namelayer.core.log.abstr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import vg.civcraft.mc.namelayer.core.log.impl.AcceptInvitation;
import vg.civcraft.mc.namelayer.core.log.impl.AddLink;
import vg.civcraft.mc.namelayer.core.log.impl.AddPermission;
import vg.civcraft.mc.namelayer.core.log.impl.BlacklistPlayer;
import vg.civcraft.mc.namelayer.core.log.impl.ChangeColor;
import vg.civcraft.mc.namelayer.core.log.impl.ChangeGroupName;
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
import vg.civcraft.mc.namelayer.core.log.impl.UnblacklistPlayer;

public class GroupActionLogFactory {
	
	private final Map<String, Function<LoggedGroupActionPersistence, LoggedGroupAction>> instanciators;
	
	public GroupActionLogFactory() {
		this.instanciators = new ConcurrentHashMap<>();
		registerNameLayerActions();
	}
	
	public void registerInstanciator(String id, Function<LoggedGroupActionPersistence, LoggedGroupAction> instanciator) {
		this.instanciators.put(id, instanciator);
	}
	
	public LoggedGroupAction instanciate(String id, LoggedGroupActionPersistence persist) {
		Function<LoggedGroupActionPersistence, LoggedGroupAction> instanciator = instanciators.get(id);
		if (instanciator == null) {
			return null;
		}
		return instanciator.apply(persist);
	}
	
	private void registerNameLayerActions() {
		registerInstanciator(AcceptInvitation.ID, AcceptInvitation::load);
		registerInstanciator(AddLink.ID, AddLink::load);
		registerInstanciator(AddPermission.ID, AddPermission::load);
		registerInstanciator(BlacklistPlayer.ID, BlacklistPlayer::load);
		registerInstanciator(ChangeColor.ID, ChangeColor::load);
		registerInstanciator(ChangeGroupName.ID, ChangeGroupName::load);
		registerInstanciator(CreateGroup.ID, CreateGroup::load);
		registerInstanciator(CreateRank.ID, CreateRank::load);
		registerInstanciator(DeleteRank.ID, DeleteRank::load);
		registerInstanciator(InviteMember.ID, InviteMember::load);
		registerInstanciator(JoinGroup.ID, JoinGroup::load);
		registerInstanciator(LeaveGroup.ID, LeaveGroup::load);
		registerInstanciator(MergeGroup.ID, MergeGroup::load);
		registerInstanciator(RejectInvite.ID, RejectInvite::load);
		registerInstanciator(RemoveLink.ID, RemoveLink::load);
		registerInstanciator(RemoveMember.ID, RemoveMember::load);
		registerInstanciator(RemovePermission.ID, RemovePermission::load);
		registerInstanciator(RevokeInvite.ID, RevokeInvite::load);
		registerInstanciator(SetPassword.ID, SetPassword::load);
		registerInstanciator(UnblacklistPlayer.ID, UnblacklistPlayer::load);
		
	}
	
	

}
