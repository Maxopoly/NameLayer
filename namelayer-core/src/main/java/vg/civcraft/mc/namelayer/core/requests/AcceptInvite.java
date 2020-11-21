package vg.civcraft.mc.namelayer.core.requests;

public final class AcceptInvite {
	
	private AcceptInvite() {}
	
	public static final String REQUEST_ID = "nl_req_accept_invite";
	public static final String REPLY_ID = "nl_ans_accept_invite";

	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, ALREADY_MEMBER, NO_EXISTING_INVITE;
	}
}
