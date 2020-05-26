package vg.civcraft.mc.namelayer.group;

import vg.civcraft.mc.namelayer.permission.GroupRank;

public class GroupLink {

	private Group originating;
	private GroupRank originatingType;
	private Group target;
	private GroupRank targetType;
	private int id;
	
	public GroupLink(Group originating, GroupRank originatingType, Group target, GroupRank targetType) {
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

	public GroupRank getOriginatingType() {
		return originatingType;
	}

	public Group getTargetGroup() {
		return target;
	}

	public GroupRank getTargetType() {
		return targetType;
	}
	

}
