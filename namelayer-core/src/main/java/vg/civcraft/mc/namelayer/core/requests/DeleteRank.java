package vg.civcraft.mc.namelayer.core.requests;

public final class DeleteRank {
	
private DeleteRank() {}
	
	public static final String REQUEST_ID = "nl_req_delete_rank";
	public static final String REPLY_ID = "nl_ans_delete_rank";
	
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, RANK_DOES_NOT_EXIST, NO_PERMISSION, RANK_HAS_CHILDREN, LAST_REMAINING_RANK, STILL_HAS_MEMBERS,
		 HAS_INCOMING_LINKS, HAS_OUTGOING_LINKS, DEFAULT_NON_MEMBER_RANK;
	}

}
