package vg.civcraft.mc.namelayer.zeus;

import java.util.function.Function;

import vg.civcraft.mc.namelayer.core.log.abstr.GroupActionLogFactory;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;

public class ZeusGroupActionLogFactory extends GroupActionLogFactory {
	
	private NameLayerDAO dao;
	
	public ZeusGroupActionLogFactory(NameLayerDAO dao) {
		this.dao = dao;
	}
	
	public void registerInstanciator(String id, Function<LoggedGroupActionPersistence, LoggedGroupAction> instanciator) {
		dao.getOrCreateActionID(id);
		super.registerInstanciator(id, instanciator);
	}

}
