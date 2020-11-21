package vg.civcraft.mc.namelayer.core.requests;

public final class LeaveGroup {
	
	private LeaveGroup() {}
	
	public static final String REQUEST_ID = "nl_req_leave_group";
	public static final String REPLY_ID = "nl_ans_leave_group";
	
	public enum FailureReason {
		NOT_A_MEMBER, NO_OTHER_OWNER, GROUP_DOES_NOT_EXIST;
	}
}
