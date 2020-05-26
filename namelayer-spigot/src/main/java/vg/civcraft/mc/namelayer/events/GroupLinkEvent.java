package vg.civcraft.mc.namelayer.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.GroupRank;

public class GroupLinkEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private Group originating;
	private GroupRank originatingType;
	private Group target;
	private GroupRank targetType;
	private boolean cancelled;

	public GroupLinkEvent(Group originating, GroupRank originatingType, Group target, GroupRank targetType) {
		this.originating = originating;
		this.originatingType = originatingType;
		this.target = target;
		this.targetType = targetType;
	}

	/**
	 * @return The group the link is pointing towards
	 */
	public Group getTargetGroup() {
		return target;
	}

	/**
	 * @return The group the link is pointing away from
	 */
	public Group getOriginatingGroup() {
		return originating;
	}

	/**
	 * @return Player type in the target group the link will point to
	 */
	public GroupRank getTargetType() {
		return targetType;
	}

	/**
	 * @return Player type in the originating group the link will point away from
	 */
	public GroupRank getOriginatingType() {
		return originatingType;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean value) {
		cancelled = value;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
