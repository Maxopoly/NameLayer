package vg.civcraft.mc.namelayer.core;

import com.google.common.base.Preconditions;

public class PermissionType {
	
	private String name;
	private int id;
	private DefaultPermissionLevel defaultPermLevels;
	private String description;

	public PermissionType(String name, int id, DefaultPermissionLevel defaultPermLevels,
			String description) {
		this.name = name;
		this.id = id;
		this.defaultPermLevels = defaultPermLevels;
		this.description = description;
	}
	
	public PermissionType(String name, DefaultPermissionLevel defaultPermLevels,
			String description) {
		this(name, -1, defaultPermLevels, description);
	}

	/**
	 * @return Name of this permission
	 */
	public String getName() {
		return name;
	}
	
	public void setID(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return Minimum permission level which will get this permission by default
	 */
	public DefaultPermissionLevel getDefaultPermLevels() {
		return defaultPermLevels;
	}

	/**
	 * @return Id of this permission
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Description of this permission, which is displayed to players
	 */
	public String getDescription() {
		return description;
	}
}