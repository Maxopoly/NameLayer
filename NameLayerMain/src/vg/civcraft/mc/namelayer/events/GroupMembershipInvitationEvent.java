package vg.civcraft.mc.namelayer.events;

import java.util.UUID;

import vg.civcraft.mc.civmodcore.interfaces.CustomEvent;
import vg.civcraft.mc.namelayer.permission.PlayerType;

/**
 * Called whenever a player is invited to a group
 *
 */
public class GroupMembershipInvitationEvent extends CustomEvent {
	
	private String groupName;
	private PlayerType type;
	private UUID invitedPlayer;
	private UUID inviter; 
	
	public GroupMembershipInvitationEvent(String groupName, PlayerType type, UUID invitedPlayer, UUID inviter){
		this.groupName = groupName;
		this.type = type;
		this.invitedPlayer = invitedPlayer;
		this.inviter = inviter;
	}
	
	/**
	 * @return The group name.
	 */
	public String getGroupName(){
		return groupName;
	}
	
	/**
	 * @return The player type in the group the player was invited to.
	 */
	public PlayerType getPlayerType(){
		return type;
	}

	/**
	 * @return The inviter's uuid, this might be null
	 */
	public UUID getInviter(){
		return inviter;
	}
	
	
	/**
	 * @return The invited player's uuid
	 */
	public UUID getInvitedPlayer(){
		return invitedPlayer;
	}
}
