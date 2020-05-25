package vg.civcraft.mc.namelayer.group.log.abstr;

import java.util.UUID;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;

public abstract class PermissionEdit extends MemberRankChange {

	protected final String permission;
	
	public PermissionEdit(long time, UUID player, String rank, String permission) {
		super(time, player, rank);
		Preconditions.checkNotNull(permission);
		this.permission = permission;
	}
	
	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player, rank, permission, null);
	}
	
	public String getPermission() {
		return permission;
	}
}
