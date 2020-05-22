package vg.civcraft.mc.namelayer.permission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DefaultPermissionLevel {
	
	OWNER, ADMIN, MOD, MEMBER;
	
	public List<Integer> getAllowedRankIds() {
		switch(this) {
		case ADMIN:
			return Arrays.asList(1);
		case MEMBER:
			return Arrays.asList(1, 2, 3);
		case MOD:
			return Arrays.asList(1, 2);
		case OWNER:
			return Collections.emptyList();
		default:
			throw new IllegalStateException();
		}
	}

}
