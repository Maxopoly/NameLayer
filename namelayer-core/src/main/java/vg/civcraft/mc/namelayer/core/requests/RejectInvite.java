package vg.civcraft.mc.namelayer.core.requests;

public final class RejectInvite {
	
	private RejectInvite() {}
	
	public static final String REQUEST_ID = "nl_req_reject_invite";
	public static final String REPLY_ID = "nl_ans_reject_invite";
	
	public enum FailureReason {
		NOT_INVITED, GROUP_DOES_NOT_EXIST;
	}
}
