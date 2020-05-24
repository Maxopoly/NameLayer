package vg.civcraft.mc.namelayer.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;

import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupAction;

public class GroupActionLog {
	
	private final List<LoggedGroupAction> changes;
	private final Group group;
	
	public GroupActionLog(Group group) {
		this.group = group;
		this.changes = new ArrayList<>();
	}
	
	public void addAction(LoggedGroupAction action, boolean saveToDb) {
		changes.add(action);
		if (saveToDb) {
			Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> {});
		}
	}
	
	public List<LoggedGroupAction> getActions() {
		return Collections.unmodifiableList(changes);
	}
	
	public Group getGroup() {
		return group;
	}

}
