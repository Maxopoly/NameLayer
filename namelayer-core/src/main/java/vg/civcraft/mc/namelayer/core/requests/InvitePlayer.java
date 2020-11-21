package vg.civcraft.mc.namelayer.core.requests;

public class InvitePlayer {
	
	private InvitePlayer() {}
	
	public static final String REQUEST_ID = "nl_req_invite_player";
	public static final String REPLY_ID = "nl_ans_invite_player";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, NO_PERMISSION;
	}
}
