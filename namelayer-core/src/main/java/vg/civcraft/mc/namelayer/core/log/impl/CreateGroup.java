package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;

public class CreateGroup extends LoggedGroupAction {

	public static final String ID = "CREATE_GROUP";

	private String name;

	public CreateGroup(long time, UUID player, String name) {
		super(time, player);
		Preconditions.checkNotNull(name, "Name may not be null");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player,null, name, null);
	}
	
	public static CreateGroup load(LoggedGroupActionPersistence persist) {
		return new CreateGroup(persist.getTimeStamp(), persist.getPlayer(), persist.getName());
	}

}
