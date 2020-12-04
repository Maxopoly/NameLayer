package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.PermissionEdit;

public class AddPermission extends PermissionEdit {

	public static final String ID = "ADD_PERMISSION";

	public AddPermission(long time, UUID player, String rank, String permission) {
		super(time, player, rank, permission);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	public static AddPermission load(LoggedGroupActionPersistence persist) {
		return new AddPermission(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(),persist.getName());
	}
}
