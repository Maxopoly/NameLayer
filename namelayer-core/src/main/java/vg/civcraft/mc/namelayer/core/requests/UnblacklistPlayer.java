package vg.civcraft.mc.namelayer.core.requests;

public final class UnblacklistPlayer {

	private UnblacklistPlayer() {}

	public static final String REQUEST_ID = "nl_req_unblacklist_player";
	public static final String REPLY_ID = "nl_ans_unblacklist_player";

	public enum FailureReason {
		NO_PERMISSION, GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, PLAYER_NOT_BLACKLISTED;
	}
}
