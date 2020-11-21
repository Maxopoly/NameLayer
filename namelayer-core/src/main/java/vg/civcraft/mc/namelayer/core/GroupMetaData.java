package vg.civcraft.mc.namelayer.core;

public abstract class GroupMetaData {
	
	private Group group;
	
	public GroupMetaData(Group group) {
		this.group = group;
	}
	
	protected void setData(String key, String data) {
		this.group.setMetaData(key, data);
	}
	
	protected String getRawData(String key) {
		return group.getMetaData(key);
	}

}
