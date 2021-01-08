package vg.civcraft.mc.namelayer.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DefaultPermissionLevel {
	
	OWNER, ADMIN, MOD, MEMBER, SPECIAL;
	
	public List<Integer> getAllowedRankIds() {
		switch(this) {
		case ADMIN:
			return Arrays.asList(GroupRankHandler.DEFAULT_ADMIN_ID);
		case MEMBER:
			return Arrays.asList(GroupRankHandler.DEFAULT_ADMIN_ID, GroupRankHandler.DEFAULT_MOD_ID, GroupRankHandler.DEFAULT_MEMBER_ID);
		case MOD:
			return Arrays.asList(GroupRankHandler.DEFAULT_ADMIN_ID, GroupRankHandler.DEFAULT_MOD_ID);
		case OWNER:
		case SPECIAL:
			return Collections.emptyList();
		default:
			throw new IllegalStateException();
		}
	}

}
