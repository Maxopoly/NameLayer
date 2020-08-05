package vg.civcraft.mc.namelayer.gui;

import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.group.GroupInteractionManager;
import vg.civcraft.mc.namelayer.group.GroupManager;
import vg.civcraft.mc.namelayer.permission.NameLayerPermissionManager;

/**
 * Abstract utility class, which provides some functionality needed for all guis
 *
 */
public abstract class NameLayerGroupGUI {
	protected final Group group;
	protected final Player player;
	protected final GroupManager groupManager;
	protected final GroupInteractionManager interactionManager;
	protected final NameLayerPermissionManager permMan;
	
	public NameLayerGroupGUI(Group g, Player p) {
		this.groupManager = NameLayerPlugin.getInstance().getGroupManager();
		this.interactionManager = NameLayerPlugin.getInstance().getGroupInteractionManager();
		this.permMan = NameLayerPlugin.getInstance().getNLPermissionManager();
		this.group = g;
		this.player = p;
	}
	
	public void setupIn(ComponableInventory inv) {
		
	}
	
	protected Player getPlayer() {
		return player;
	}
	
	protected Group getGroup() {
		return group;
	}

}
