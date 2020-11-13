package vg.civcraft.mc.namelayer.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class PermissionType {
	
	private String registeringPlugin;
	private String name;
	private int id;
	private DefaultPermissionLevel defaultPermLevels;
	private String description;

	public PermissionType(String registeringPlugin, String name, int id, DefaultPermissionLevel defaultPermLevels,
			String description) {
		this.name = name;
		this.registeringPlugin = registeringPlugin;
		this.id = id;
		this.defaultPermLevels = defaultPermLevels;
		this.description = description;
	}

	/**
	 * @return Name of this permission
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Name of the plugin which registered this permission
	 */
	public String getRegisteringPlugin() {
		return registeringPlugin;
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