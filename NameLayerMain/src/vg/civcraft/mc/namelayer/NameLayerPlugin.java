package vg.civcraft.mc.namelayer;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.Config;
import vg.civcraft.mc.civmodcore.annotations.CivConfig;
import vg.civcraft.mc.civmodcore.annotations.CivConfigType;
import vg.civcraft.mc.civmodcore.annotations.CivConfigs;
import vg.civcraft.mc.namelayer.command.NameLayerCommandHandler;
import vg.civcraft.mc.namelayer.database.AssociationList;
import vg.civcraft.mc.namelayer.database.Database;
import vg.civcraft.mc.namelayer.database.GroupManagerDao;
import vg.civcraft.mc.namelayer.group.DefaultGroupHandler;
import vg.civcraft.mc.namelayer.listeners.AssociationListener;
import vg.civcraft.mc.namelayer.listeners.MercuryMessageListener;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.misc.ClassHandler;
import vg.civcraft.mc.namelayer.permission.PermissionType;


public class NameLayerPlugin extends ACivMod{
	private static AssociationList associations;
	private static GroupManagerDao groupManagerDao;
	private static DefaultGroupHandler defaultGroupHandler;
	private static NameLayerPlugin instance;
	private static Database db;
	private static boolean loadGroups = true;
	private static int groupLimit = 10;
	private static boolean createGroupOnFirstJoin;
	private Config config;
	
	@CivConfigs({
		@CivConfig(name = "groups.enable", def = "true", type = CivConfigType.Bool),
		@CivConfig(name = "groups.grouplimit", def = "10", type = CivConfigType.Int),
		@CivConfig(name = "groups.creationOnFirstJoin", def = "true", type = CivConfigType.Bool)
	})
	@Override
	public void onEnable() {
		super.onEnable(); // Need to call this to properly initialize this mod
		config = GetConfig();
		loadGroups = config.get("groups.enable").getBool();
		groupLimit = config.get("groups.grouplimit").getInt();
		createGroupOnFirstJoin = config.get("groups.creationOnFirstJoin").getBool();
		instance = this;
		loadDatabases();
	    ClassHandler.Initialize(Bukkit.getServer());
		new NameAPI(new GroupManager(), associations);
		registerListeners();
		if (loadGroups){
			PermissionType.initialize();
			groupManagerDao.loadGroupsInvitations();
			defaultGroupHandler = new DefaultGroupHandler();
			handle = new NameLayerCommandHandler();
			handle.registerCommands();
		}
	}
	
	public void registerListeners(){
		getServer().getPluginManager().registerEvents(new AssociationListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		if (isMercuryEnabled()){
			getServer().getPluginManager().registerEvents(new MercuryMessageListener(), this);
		}
	}

	public void onDisable() {
		
	}
	
	public static NameLayerPlugin getInstance(){
		return instance;
	}
	
	@CivConfigs({
		@CivConfig(name = "sql.hostname", def = "localhost", type = CivConfigType.String),
		@CivConfig(name = "sql.username", def = "", type = CivConfigType.String),
		@CivConfig(name = "sql.password", def = "", type = CivConfigType.String),
		@CivConfig(name = "sql.port", def = "3306", type = CivConfigType.Int),
		@CivConfig(name = "sql.dbname", def = "namelayer", type = CivConfigType.String)
	})
	public void loadDatabases(){
		String host = config.get("sql.hostname").getString();
		int port = config.get("sql.port").getInt();
		String dbname = config.get("sql.dbname").getString();
		String username = config.get("sql.username").getString();
		String password = config.get("sql.password").getString();
		db = new Database(host, port, dbname, username, password, getLogger());
		db.connect();
		if (!db.isConnected()){
			NameLayerPlugin.log(Level.WARNING, "Could not connect to DataBase, shutting down!");
			Bukkit.getPluginManager().disablePlugin(this); // Why have it try connect, it can't
		}
		associations = new AssociationList(db);
		if (loadGroups)
			groupManagerDao = new GroupManagerDao(db);
	}
	
	public static void reconnectAndReintializeStatements(){
		if (db.isConnected())
			return;
		db.connect();
		associations.initializeStatements();
		if (loadGroups)
			groupManagerDao.initializeStatements();
	}
	
	/**
	 * @return Returns the AssocationList.
	 */
	public static AssociationList getAssociationList(){
		return associations;
	}
	/**
	 * @return Returns the GroupManagerDatabase.
	 */
	public static GroupManagerDao getGroupManagerDao(){
		return groupManagerDao;
	}
	
	public static void log(Level level, String message){
		if (level == Level.INFO)
			Bukkit.getLogger().log(level, "[NameLayer:] Info follows\n" +
			message);
		else if (level == Level.WARNING)
			Bukkit.getLogger().log(level, "[NameLayer:] Warning follows\n" +
					message);
		else if (level == Level.SEVERE)
			Bukkit.getLogger().log(level, "[NameLayer:] Stack Trace follows\n --------------------------------------\n" +
					message +
					"\n --------------------------------------");
	}
	/**
	 * Updates the version number for a plugin. You must specify what 
	 * the current version number is.
	 * @param currentVersion- The current version of the plugin.
	 * @param pluginName- The plugin name.
	 * @return Returns the new version of the db.
	 */
	public static void insertVersionNum(int currentVersion, String pluginName){
		groupManagerDao.updateVersion(currentVersion, pluginName);
	}
	/**
	 * Checks the version of a specific plugin's db.
	 * @param name- The name of the plugin.
	 * @return Returns the version of the plugin or 0 if none was found.
	 */
	public static int getVersionNum(String pluginName){
		return groupManagerDao.checkVersion(pluginName);
	}
	
	public static String getSpecialAdminGroup(){
		return "Name_Layer_Special";
	}
	
	public static boolean createGroupOnFirstJoin() {
		return createGroupOnFirstJoin;
	}

	@Override
	protected String getPluginName() {
		return "NameLayerPlugin";
	}

	public static boolean isMercuryEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Mercury");
	}
	
	public int getGroupLimit(){
		return groupLimit;
	}
	
	public static DefaultGroupHandler getDefaultGroupHandler() {
		return defaultGroupHandler;
	}
}
