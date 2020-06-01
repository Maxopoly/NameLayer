package vg.civcraft.mc.namelayer.gui;

import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.group.GroupInteractionManager;
import vg.civcraft.mc.namelayer.group.GroupManager;

/**
 * Abstract utility class, which provides some functionality needed for all guis
 *
 */
public abstract class NameLayerGroupGUI {
	private final Group group;
	private final Player player;
	private final GroupManager groupManager;
	private final GroupInteractionManager interactionManager;
	
	public NameLayerGroupGUI(Group g, Player p) {
		this.groupManager = NameLayerPlugin.getInstance().getGroupManager();
		this.interactionManager = NameLayerPlugin.getInstance().getGroupInteractionManager();
		this.group = g;
		this.player = p;
	}
	
	private Player getPlayer() {
		return player;
	}
	
	private Group getGroup() {
		return group;
	}

}
