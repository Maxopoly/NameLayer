package vg.civcraft.mc.namelayer.core.requests;

public final class UnlinkGroups {
	
	private UnlinkGroups() {}
	
	public static final String REQUEST_ID = "nl_req_unlink_groups";
	public static final String REPLY_ID = "nl_ans_unlink_groups";
	
	public enum FailureReason {
		ORIGINAL_GROUP_DOES_NOT_EXIST, TARGET_GROUP_DOES_NOT_EXIST, CANNOT_UNLINK_SELF, NO_PERMISSION_ORIGINAL_GROUP, NO_PERMISSION_TARGET_GROUP,
		ORIGINAL_RANK_DOES_NOT_EXIST, TARGET_RANK_DOES_NOT_EXIST, NO_LINKS_FOUND;
	}
}
