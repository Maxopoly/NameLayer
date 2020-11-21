package vg.civcraft.mc.namelayer.core.requests;

public final class JoinGroup {
	
	private JoinGroup() {}
	
	public static final String REQUEST_ID = "nl_req_join_group";
	public static final String REPLY_ID = "nl_ans_join_group";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, WRONG_PASSWORD, GROUP_HAS_NO_PASSWORD, ALREADY_MEMBER_OR_BLACKLISTED;
	}
}
