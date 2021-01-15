package vg.civcraft.mc.namelayer.core.requests;

public class ChangeGroupColour {

	private ChangeGroupColour() {}

	public static final String REQUEST_ID = "nl_req_change_group_colour";
	public static final String REPLY_ID = "nl_ans_change_group_colour";

	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, COLOUR_NOT_VALID, NO_PERMISSION;
	}
}
