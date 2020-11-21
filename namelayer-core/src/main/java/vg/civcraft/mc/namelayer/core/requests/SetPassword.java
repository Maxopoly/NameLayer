package vg.civcraft.mc.namelayer.core.requests;

public final class SetPassword {
	
	private SetPassword() {}
	
	public static final String REQUEST_ID = "nl_req_set_password";
	public static final String REPLY_ID = "nl_ans_set_password";
	
	public enum FailureReason {
		NO_PERMISSION, NULL_PASSWORD, GROUP_DOES_NOT_EXIST;
	}
}
