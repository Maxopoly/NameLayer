package vg.civcraft.mc.namelayer.zeus;

import com.github.civcraft.zeus.plugin.ZeusLoad;
import com.github.civcraft.zeus.plugin.ZeusPlugin;

@ZeusLoad(name = "NameLayer", version = "1.0", description = "Player driven group management")
public class NameLayerZPlugin extends ZeusPlugin {

	private static NameLayerZPlugin instance;

	public static NameLayerZPlugin getInstance() {
		return instance;
	}

	private ZeusGroupTracker groupTracker;
	private ServerGroupKnowledgeTracker groupKnowledgeTracker;
	private NameLayerDAO dao;

	@Override
	public boolean onEnable() {
		instance = this;
		dao = new NameLayerDAO(getName(), logger);
		if (!dao.updateDatabase()) {
			return false;
		}
		groupTracker = new ZeusGroupTracker(dao);
		groupKnowledgeTracker = new ServerGroupKnowledgeTracker(groupTracker, dao);
		return true;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}
	
	public NameLayerDAO getDAO() {
		return dao;
	}

	public ServerGroupKnowledgeTracker getGroupKnowledgeTracker() {
		return groupKnowledgeTracker;
	}

	public ZeusGroupTracker getGroupTracker() {
		return groupTracker;
	}

}
