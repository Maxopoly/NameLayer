package vg.civcraft.mc.namelayer.events;

import org.bukkit.event.Event;

import vg.civcraft.mc.namelayer.group.Group;

public abstract class GroupMergeEvent extends Event {
	private Group notToKeep; // the group that will join into another
	private Group toKeep; // the group that is receiving the other

	public GroupMergeEvent(Group toKeep, Group notToKeep) {
		this.toKeep = toKeep;
		this.notToKeep = notToKeep;
	}

	/**
	 * @return Returns the group to be merged, which will be gone after the merging
	 */
	public Group getToBeMerged() {
		return notToKeep;
	}

	/**
	 * @return Returns the group that will be left after the merging
	 */
	public Group getMergingInto() {
		return toKeep;
	}
}
