package vg.civcraft.mc.namelayer.core.log.abstr;

import java.util.UUID;

import com.google.common.base.Preconditions;

public abstract class LinkStateChange extends LoggedGroupAction {
	protected final String ownRankLinked;
	protected final String otherGroup;
	protected final String otherGroupRank;
	protected final boolean isSelfOrigin;

	public LinkStateChange(long time, UUID player, String ownRankLinked, String otherGroup, String otherGroupRank,
			boolean isSelfOrigin) {
		super(time, player);
		Preconditions.checkNotNull(ownRankLinked, "Own rank may not be null");
		Preconditions.checkNotNull(otherGroup, "Other group may not be null");
		Preconditions.checkNotNull(otherGroupRank, "Other group rank may not be null");
		this.ownRankLinked = ownRankLinked;
		this.otherGroup = otherGroup;
		this.otherGroupRank = otherGroup;
		this.isSelfOrigin = isSelfOrigin;
	}

	public String getOwnRankLinked() {
		return ownRankLinked;
	}

	public String getOtherGroup() {
		return otherGroup;
	}

	public String getRankLinkedOtherGroup() {
		return otherGroupRank;
	}

	public boolean isSelfOrigin() {
		return isSelfOrigin;
	}

	@Override
	public LoggedGroupActionPersistence getPersistence() {
		String extra = isSelfOrigin ? "t" : "f";
		extra += otherGroupRank;
		return new LoggedGroupActionPersistence(time, player, ownRankLinked, otherGroup, extra);
	}
}
