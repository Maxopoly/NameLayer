package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.PermissionEdit;

public class RemovePermission extends PermissionEdit {
	
	public static final String ID = "REMOVE_PERMISSION";

	public RemovePermission(long time, UUID player, String rank, String permission) {
		super(time, player, rank, permission);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
	public static RemovePermission load(LoggedGroupActionPersistence persist) {
		return new RemovePermission(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(),persist.getName());
	}

}
