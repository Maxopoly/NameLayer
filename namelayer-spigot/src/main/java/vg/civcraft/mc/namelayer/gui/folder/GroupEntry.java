package vg.civcraft.mc.namelayer.gui.folder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.gui.GUIGroupOverview;

public class GroupEntry extends FolderElement {
	private Group group;
	
	public GroupEntry(FolderElement parent, String groupName) {
		super(groupName, parent);
		this.group = GroupAPI.getGroup(groupName);
	}
	
	public Group getGroup() {
		return group;
	}

	@Override
	public IClickable getGUIEntry(GUIGroupOverview gui, Player player) {
		ItemStack is = GUIGroupOverview.getHashedItem(group.getName().hashCode());
		return new LClickable(is, p -> {
			if (doMovingCheck(gui, player)) {
				return;
			}
 			//TODO
		});
	}

}
