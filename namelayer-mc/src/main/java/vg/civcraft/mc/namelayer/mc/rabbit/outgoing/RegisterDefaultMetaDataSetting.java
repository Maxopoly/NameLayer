package vg.civcraft.mc.namelayer.mc.rabbit.outgoing;

import org.json.JSONObject;

import com.github.maxopoly.artemis.rabbit.MCRabbitMessage;

import vg.civcraft.mc.namelayer.core.requests.RegisterMetaDataDefault;

public class RegisterDefaultMetaDataSetting extends MCRabbitMessage {
	
	private String key;
	private String value;
	
	public RegisterDefaultMetaDataSetting(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("key", key);
		json.put("value", value);
	}

	@Override
	public String getIdentifier() {
		return RegisterMetaDataDefault.ID;
	}

}
