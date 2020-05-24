package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

public abstract class MemberRankChange extends LoggedGroupAction {

	protected final String rank;
	
	public MemberRankChange(long time, UUID player, String rank) {
		super(time, player);
		Preconditions.checkNotNull(rank, "Rank may not be null");
		this.rank = rank;
	}
	
	public String getRank() {
		return rank;
	}

	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player, rank, null, null);
	}
}
