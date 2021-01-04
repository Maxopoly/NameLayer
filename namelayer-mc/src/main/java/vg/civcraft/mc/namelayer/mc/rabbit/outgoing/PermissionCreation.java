package vg.civcraft.mc.namelayer.mc.rabbit.outgoing;

import org.json.JSONObject;

import com.github.maxopoly.artemis.rabbit.MCStandardRequest;

import vg.civcraft.mc.namelayer.core.DefaultPermissionLevel;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

public class PermissionCreation extends MCStandardRequest {
	
	private String name;
	private DefaultPermissionLevel defaultPermLevel;

	public PermissionCreation(String name, DefaultPermissionLevel defaultPermLevel) {
		this.name = name;
		this.defaultPermLevel = defaultPermLevel;
	}

	@Override
	public void handleReply(JSONObject reply) {
		int id = reply.getInt("perm_id");
		doSync(() -> NameLayerPlugin.getInstance().getGroupTracker()
				.registerPermission(new PermissionType(name, id, defaultPermLevel, name)));
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("name", name);
		json.put("default_perm", defaultPermLevel.toString());
	}

	@Override
	public String getIdentifier() {
		return "nl_register_permission";
	}

}
