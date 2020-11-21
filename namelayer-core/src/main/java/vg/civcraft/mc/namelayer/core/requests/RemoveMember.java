package vg.civcraft.mc.namelayer.core.requests;

public final class RemoveMember {
	
	private RemoveMember() {}
	
	public static final String REQUEST_ID = "nl_req_remove_member";
	public static final String REPLY_ID = "nl_ans_remove_member";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, CANNOT_KICK_SELF, NOT_A_MEMBER, NO_PERMISSION;
	}
}
