package vg.civcraft.mc.namelayer.groupRequests;

public class RequestResult {
    
    private boolean success;
    private String result;
    
    public RequestResult(String msg, boolean success) {
	this.result = msg;
	this.success = success;
    }
    
    public boolean wasSuccessfull() {
	return success;
    }
    
    public String getResultMessage() {
	return result;
    }

}
