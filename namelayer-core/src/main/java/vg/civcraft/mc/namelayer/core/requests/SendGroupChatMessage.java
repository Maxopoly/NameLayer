package vg.civcraft.mc.namelayer.core.requests;

public final class SendGroupChatMessage {
	
	public static final String REQUEST_ID = "nl_req_send_gc";
	public static final String REPLY_ID = "nl_ans_send_gc";
	
	private SendGroupChatMessage() {}	
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, NO_PERMISSION;
	}

}
