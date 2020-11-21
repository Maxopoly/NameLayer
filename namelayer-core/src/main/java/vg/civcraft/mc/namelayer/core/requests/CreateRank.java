package vg.civcraft.mc.namelayer.core.requests;

public final class CreateRank {
	
	private CreateRank() {}
	
	public static final String REQUEST_ID = "nl_req_create_rank";
	public static final String REPLY_ID = "nl_ans_create_rank";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, RANK_ALREADY_EXISTS, NO_PERMISSION, RANK_LIMIT_REACHED, PARENT_RANK_DOES_NOT_EXIST, INVALID_RANK_NAME;
	}
}
