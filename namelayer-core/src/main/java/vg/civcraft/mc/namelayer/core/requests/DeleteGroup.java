package vg.civcraft.mc.namelayer.core.requests;

public final class DeleteGroup {
	
	private DeleteGroup() {}
	
	public static final String REQUEST_ID = "nl_req_delete_group";
	public static final String REPLY_ID = "nl_ans_delete_group";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, NO_PERMISSION;
	}
}
