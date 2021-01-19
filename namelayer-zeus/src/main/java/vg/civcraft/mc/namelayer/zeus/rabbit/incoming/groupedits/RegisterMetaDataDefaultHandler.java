package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.requests.RegisterMetaDataDefault;
import vg.civcraft.mc.namelayer.zeus.NameLayerZPlugin;

public class RegisterMetaDataDefaultHandler extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		String key = data.getString("key");
		String value = data.getString("value");
		NameLayerZPlugin.getInstance().getGroupTracker().registerDefaultMetaData(key, value);
	}

	@Override
	public String getIdentifier() {
		return RegisterMetaDataDefault.ID;
	}

}
