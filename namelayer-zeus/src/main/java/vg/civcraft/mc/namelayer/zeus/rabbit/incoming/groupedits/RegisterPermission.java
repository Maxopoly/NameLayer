package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.DynamicRabbitMessage;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.DefaultPermissionLevel;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.zeus.NameLayerZPlugin;

public class RegisterPermission extends GenericInteractiveRabbitCommand {

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		String name = data.getString("name");
		DefaultPermissionLevel permLevel = DefaultPermissionLevel.valueOf(data.getString("default_perm"));
		PermissionType perm = new PermissionType(name, permLevel, "");
		NameLayerZPlugin.getInstance().getGroupTracker().registerPermission(perm);
		perm = NameLayerZPlugin.getInstance().getGroupTracker().getPermissionTracker().getPermission(name);
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("perm_id", perm.getId());
		sendReply(sendingServer, new DynamicRabbitMessage(ticket, "nl_register_permission_reply", parameter));
	}

	@Override
	public String getIdentifier() {
		return "nl_register_permission";
	}

}
