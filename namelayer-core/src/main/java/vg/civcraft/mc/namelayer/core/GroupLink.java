package vg.civcraft.mc.namelayer.core;

import java.util.Objects;

public class GroupLink {

	private Group originating;
	private GroupRank originatingType;
	private Group target;
	private GroupRank targetType;

	public GroupLink(Group originating, GroupRank originatingType, Group target, GroupRank targetType) {
		this.originating = originating;
		this.originatingType = originatingType;
		this.target = target;
		this.targetType = targetType;
	}

	public Group getOriginatingGroup() {
		return originating;
	}

	public GroupRank getOriginatingRank() {
		return originatingType;
	}

	public Group getTargetGroup() {
		return target;
	}

	public GroupRank getTargetRank() {
		return targetType;
	}

	public int hashCode() {
		return Objects.hash(originating.getPrimaryId(), originatingType.getId(), target.getPrimaryId(),
				targetType.getId());
	}

	public boolean equals(Object o) {
		if (!(o instanceof GroupLink)) {
			return false;
		}
		GroupLink link = (GroupLink) o;
		return link.originating.getPrimaryId() == this.originating.getPrimaryId()
				&& link.originatingType.getId() == this.originatingType.getId()
				&& link.target.getPrimaryId() == this.target.getPrimaryId()
				&& link.targetType.getId() == this.targetType.getId();
	}

}
