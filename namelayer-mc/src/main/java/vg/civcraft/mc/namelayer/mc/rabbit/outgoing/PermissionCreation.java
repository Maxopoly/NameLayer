package vg.civcraft.mc.namelayer.mc.rabbit.outgoing;

import org.json.JSONObject;

import com.github.civcraft.artemis.rabbit.MCStandardRequest;

import vg.civcraft.mc.namelayer.core.DefaultPermissionLevel;

public class PermissionCreation extends MCStandardRequest {
	
	private String registeringPlugin;
	private String name;
	private DefaultPermissionLevel defaultPermLevel;
	
	public PermissionCreation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleReply(JSONObject reply) {
		int id = reply.getInt("perm_id");
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("registering_plugin", registeringPlugin);
		json.put("name", name);
		json.put("default_perm", defaultPermLevel.toString());
	}

}
