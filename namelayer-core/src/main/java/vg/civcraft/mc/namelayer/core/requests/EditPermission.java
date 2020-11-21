package vg.civcraft.mc.namelayer.core.requests;

public final class EditPermission {
	
	private EditPermission() {}
	
	public static final String REQUEST_ID = "nl_req_edit_permission";
	public static final String REPLY_ID = "nl_ans_edit_permission";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, NO_PERMISSION, RANK_DOES_NOT_EXIST, PERMISSION_DOES_NOT_EXIST,
		RANK_ALREADY_HAS_PERMISSION, RANK_LACKS_PERMISSION;
	}
}
