package vg.civcraft.mc.namelayer.core.requests;

public class RevokeInvite {
	
	private RevokeInvite() {}
	
	public static final String REQUEST_ID = "nl_req_revoke_invite";
	public static final String REPLY_ID = "nl_ans_revoke_invite";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, RANK_DOES_NOT_EXIST, NO_PERMISSION;
	}
}
