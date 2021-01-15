package vg.civcraft.mc.namelayer.core.requests;

public class ChangeGroupColor {

	private ChangeGroupColor() {}

	public static final String REQUEST_ID = "nl_req_change_group_color";
	public static final String REPLY_ID = "nl_ans_change_group_color";

	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, COLOR_NOT_VALID, NO_PERMISSION;
	}
}
