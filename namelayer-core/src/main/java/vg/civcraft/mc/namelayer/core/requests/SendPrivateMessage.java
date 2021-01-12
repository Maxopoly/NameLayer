package vg.civcraft.mc.namelayer.core.requests;

public final class SendPrivateMessage {
	
	public static final String REQUEST_ID = "nl_req_send_pm";
	public static final String REPLY_ID = "nl_ans_send_pm";
	
	private SendPrivateMessage() {}
	
	
	public enum FailureReason {
		PLAYER_DOES_NOT_EXIST;
	}

}
