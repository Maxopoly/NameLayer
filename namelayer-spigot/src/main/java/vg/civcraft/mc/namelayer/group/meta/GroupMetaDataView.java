package vg.civcraft.mc.namelayer.group.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonObject;

import vg.civcraft.mc.namelayer.group.Group;

public class GroupMetaDataView<T extends GroupMetaData> {
	
	private Map<Integer, T> metaData;
	private Supplier <T> defaultValue;
	private Function<JsonObject, T> decoder;
	private String identifier;
	
	public GroupMetaDataView(String identifier, Supplier <T> defaultValue, Function<JsonObject, T> decoder) {
		metaData = new HashMap<>();
		this.identifier = identifier;
		this.defaultValue = defaultValue;
		this.decoder = decoder;
	}
	
	void addValue(Integer key, T data) {
		metaData.put(key, data);
	}
	
	public T getMetaData(Group group) {
		T data = metaData.get(group.getPrimaryId());
		if (data == null) {
			JsonObject json = GroupMetaDataAPI.hitJsonCache(group.getPrimaryId(), identifier);
			if (json == null) {
				data = defaultValue.get();
			}
			else {
				data = decoder.apply(json);
				if (data == null) {
					//broken data, revert to default
					data = defaultValue.get();
				}
			}
			metaData.put(group.getPrimaryId(), data);
		}
		return data;
	}

}
