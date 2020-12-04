package vg.civcraft.mc.namelayer.core.requests;

public final class LinkGroups {
	
	private LinkGroups() {}
	
	public static final String REQUEST_ID = "nl_req_link_groups";
	public static final String REPLY_ID = "nl_ans_link_groups";
	
	public enum FailureReason {
		CANNOT_LINK_TO_SELF, NO_PERMISSION_ORIG_GROUP, NO_PERMISSION_TARGET_GROUP, ATTEMPTED_GROUP_CYCLING, ORIGINAL_GROUP_DOES_NOT_EXIST, TARGET_GROUP_DOES_NOT_EXIST, ORIGINAL_GROUP_RANK_DOES_NOT_EXIST, TARGET_GROUP_RANK_DOES_NOT_EXIST;
	}
}
