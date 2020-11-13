package vg.civcraft.mc.namelayer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;

public class GroupActionLog {

	private final List<LoggedGroupAction> changes;
	private final Group group;

	public GroupActionLog(Group group) {
		this.group = group;
		this.changes = new ArrayList<>();
	}

	public void addAction(LoggedGroupAction action) {
		changes.add(action);
	}

	public void sortLog() {
		Collections.sort(changes, (l1, l2) -> Long.compare(l1.getTimeStamp(), l2.getTimeStamp()));
	}

	public List<LoggedGroupAction> getActions() {
		return Collections.unmodifiableList(changes);
	}

	public Group getGroup() {
		return group;
	}

}
