package vg.civcraft.mc.namelayer;

import org.bukkit.configuration.ConfigurationSection;

import vg.civcraft.mc.civmodcore.CoreConfigManager;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

public class NameLayerConfigManager extends CoreConfigManager {
	
	private ManagedDatasource db;
	private boolean createGroupOnFirstJoin;
	private int groupLimit;
	private boolean loadAllNames;

	public NameLayerConfigManager(NameLayerPlugin plugin) {
		super(plugin);
	}

	@Override
	protected boolean parseInternal(ConfigurationSection config) {
		db = (ManagedDatasource) config.get("database");
		createGroupOnFirstJoin = config.getBoolean("groups.creationOnFirstJoin", true);
		groupLimit = config.getInt("groups.grouplimit", 25);
		loadAllNames = config.getBoolean("load_all_names", true);
		return true;
	}
	
	public ManagedDatasource getDatabase() {
		return db;
	}
	
	public boolean cacheAllNames() {
		return loadAllNames;
	}

	public boolean createGroupOnFirstJoin() {
		return createGroupOnFirstJoin;
	}

	public int getGroupLimit(){
		return groupLimit;
	}
}
