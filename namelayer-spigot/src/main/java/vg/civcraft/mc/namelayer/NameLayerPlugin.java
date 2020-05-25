package vg.civcraft.mc.namelayer;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.namelayer.database.AssociationList;
import vg.civcraft.mc.namelayer.database.GroupManagerDao;
import vg.civcraft.mc.namelayer.group.NameLayerMetaData;
import vg.civcraft.mc.namelayer.group.meta.GroupMetaDataAPI;
import vg.civcraft.mc.namelayer.group.meta.GroupMetaDataView;
import vg.civcraft.mc.namelayer.listeners.AssociationListener;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.misc.ClassHandler;
import vg.civcraft.mc.namelayer.misc.NameLayerSettingManager;
import vg.civcraft.mc.namelayer.permission.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class NameLayerPlugin extends ACivMod {
	private AssociationList associations;
	private GroupManagerDao groupManagerDao;
	private static NameLayerPlugin instance;
	private NameLayerConfigManager configManager;
	private GroupMetaDataView<NameLayerMetaData> nameLayerMeta;
	private NameLayerSettingManager settingManager;
	private GroupManager groupManager;
	private NameLayerPermissionManager permissionManager;
	private GroupInteractionManager groupInteractManager;

	@Override
	public void onEnable() {
		super.onEnable(); // Need to call this to properly initialize this mod
		instance = this;
		configManager = new NameLayerConfigManager(this);
		if (!configManager.parse()) {
			getLogger().log(Level.SEVERE, "Failed to read NL config, terminating");
			Bukkit.shutdown();
			return;
		}
		groupManagerDao = new GroupManagerDao(this, configManager.getDatabase());
		if (!groupManagerDao.updateDatabase()) {
			getLogger().log(Level.SEVERE, "Database update failed, terminating");
			Bukkit.shutdown();
			return;
		}
		ClassHandler.initialize(Bukkit.getServer());
		new NameAPI(associations);
		groupManager = groupManagerDao.loadAllGroups();
		registerListeners();
		PermissionType.initialize();
		permissionManager = new NameLayerPermissionManager();
		groupManagerDao.loadGroupsInvitations();
		nameLayerMeta = GroupMetaDataAPI.registerGroupMetaData(this.getName(), NameLayerMetaData::createNew,
				NameLayerMetaData::load);
		settingManager = new NameLayerSettingManager();
		groupInteractManager =new GroupInteractionManager(groupManager, permissionManager, settingManager, nameLayerMeta);
	}

	public void registerListeners() {
		getServer().getPluginManager().registerEvents(new AssociationListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(groupManager), this);
	}

	public static NameLayerPlugin getInstance() {
		return instance;
	}

	public AssociationList getAssociationList() {
		return associations;
	}
	
	public NameLayerPermissionManager getNLPermissionManager() {
		return permissionManager;
	}

	public NameLayerSettingManager getSettingsManager() {
		return settingManager;
	}
	
	public GroupInteractionManager getGroupInteractionManager() {
		return groupInteractManager;
	}

	public GroupMetaDataView<NameLayerMetaData> getNameLayerMeta() {
		return nameLayerMeta;
	}

	public GroupManagerDao getGroupManagerDao() {
		return groupManagerDao;
	}

	public GroupManager getGroupManager() {
		return groupManager;
	}

	public NameLayerConfigManager getConfigManager() {
		return configManager;
	}
}
