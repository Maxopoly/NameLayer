package vg.civcraft.mc.namelayer.core.requests;

public final class RenameGroup {
	
	private RenameGroup() {}
	
	public static final String REQUEST_ID = "nl_req_rename_group";
	public static final String REPLY_ID = "nl_ans_rename_group";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, NO_PERMISSION, SAME_NAME, NAME_ALREADY_TAKEN;
	}
}
