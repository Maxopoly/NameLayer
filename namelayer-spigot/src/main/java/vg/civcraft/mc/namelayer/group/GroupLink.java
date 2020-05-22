package vg.civcraft.mc.namelayer.group;

import vg.civcraft.mc.namelayer.permission.PlayerType;

public class GroupLink {

	private Group originating;
	private PlayerType originatingType;
	private Group target;
	private PlayerType targetType;
	private int id;
	
	public GroupLink(Group originating, PlayerType originatingType, Group target, PlayerType targetType) {
		this.originating = originating;
		this.originatingType = originatingType;
		this.target = target;
		this.targetType = targetType;
		this.id = -1;
	}
	
	public void setID(int id) {
		if (id != -1) {
			throw new IllegalStateException("Id was already set");
		}
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public Group getOriginatingGroup() {
		return originating;
	}

	public PlayerType getOriginatingType() {
		return originatingType;
	}

	public Group getTargetGroup() {
		return target;
	}

	public PlayerType getTargetType() {
		return targetType;
	}
	

}
