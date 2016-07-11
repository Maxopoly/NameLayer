package vg.civcraft.mc.namelayer.groupRequests;

public class RequestResultFactory {
    
    public static RequestResult groupNameWasNull() {
	return new RequestResult("You entered no groupname", false);
    }
    
    public static RequestResult groupDoesNotExist(String enteredName) {
	return new RequestResult("There is no group with the name " + enteredName, false);
    }
    
    public static RequestResult groupIsDisciplined() {
	return new RequestResult("You may not modify this group, because it is disciplined", false);
    }
    
    public static RequestResult playerDoesNotExist(String name) {
	return new RequestResult("The player " + name + " does not exist", false);
    }
    
    public static RequestResult playerTypeDoesNotExist(String name, String groupName) {
	return new RequestResult(name + " is not valid player type for the group " + groupName, false);
    }
    
    public static RequestResult playerDoesNotHavePermission() {
	return new RequestResult("You don't have the required permissions to do this", false);
    }
    
    public static RequestResult playerActionNotAllowed() {
	return new RequestResult("You are not allowed to do this", false);
    }
}
