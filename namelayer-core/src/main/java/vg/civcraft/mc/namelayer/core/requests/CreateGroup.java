package vg.civcraft.mc.namelayer.core.requests;

public final class CreateGroup {
	
	private CreateGroup() {}
	
	public static final String REQUEST_ID = "nl_req_create_group";
	public static final String REPLY_ID = "nl_ans_create_group";
	
	public enum FailureReason {
		GROUP_ALREADY_EXISTS, GROUP_LIMIT_REACHED, NAME_INVALID, UNKNOWN_ERROR;
	}
}
