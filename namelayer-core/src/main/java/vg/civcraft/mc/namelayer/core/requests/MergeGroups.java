package vg.civcraft.mc.namelayer.core.requests;

public class MergeGroups {
	
	private MergeGroups() {}
	
	public static final String REQUEST_ID = "nl_req_merge_groups";
	public static final String REPLY_ID = "nl_ans_merge_groups";
	
	public enum FailureReason {
		NO_PERMISSION_ORIG_GROUP, NO_PERMISSION_TARGET_GROUP, CANNOT_MERGE_INTO_SELF, ORIGINAL_GROUP_DOES_NOT_EXIST, TARGET_GROUP_DOES_NOT_EXIST, HAS_ACTIVE_LINKS;
	}
}
