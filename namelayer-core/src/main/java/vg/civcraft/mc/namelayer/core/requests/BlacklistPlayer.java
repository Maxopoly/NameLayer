package vg.civcraft.mc.namelayer.core.requests;

public class BlacklistPlayer {
	
	public static final String REQUEST_ID = "nl_req_blacklist_player";
	public static final String REPLY_ID = "nl_ans_blacklist_player";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, RANK_DOES_NOT_EXIST, NO_PERMISSION, NOT_BLACKLISTED_RANK, IS_A_MEMBER;
	}

}
