package vg.civcraft.mc.namelayer.core.requests;

public class PromotePlayer {
	
	private PromotePlayer() {}
	
	public static final String REQUEST_ID = "nl_req_promote_player";
	public static final String REPLY_ID = "nl_ans_promote_player";
	
	public enum FailureReason {
		NO_PERMISSION, GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, RANK_DOES_NOT_EXIST, SAME_AS_CURRENT_RANK, BLACKLISTED, CANNOT_CHANGE_YOURSELF, NOT_A_MEMBER;
	}
}
