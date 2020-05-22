package vg.civcraft.mc.namelayer.group.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonObject;

import vg.civcraft.mc.namelayer.group.Group;

public class GroupMetaDataAPI {

	private static Map<String, GroupMetaDataView<?>> views = new HashMap<>();
	private static Map<Integer, JsonObject> rawJsons;

	public static <T extends GroupMetaData> GroupMetaDataView<T> registerGroupMetaData(String identifier,
			Supplier<T> defaultSupplier, Function<JsonObject, T> decoder) {
		if (views.containsKey(identifier)) {
			throw new IllegalArgumentException("Meta data " + identifier + " is already registed");
		}
		GroupMetaDataView<T> view =  new GroupMetaDataView<>(identifier, defaultSupplier, decoder);
		views.put(identifier, view);
		return view;
	}
	
	public static void offerRawMeta(Map<Integer, JsonObject> rawJsons) {
		if (rawJsons != null) {
			throw new IllegalStateException("Jsons were already loaded");
		}
		GroupMetaDataAPI.rawJsons = rawJsons;
	}

	static JsonObject hitJsonCache(int groupId, String identifier) {
		JsonObject groupJson = rawJsons.get(groupId);
		if (groupJson == null) {
			throw new IllegalStateException("No group meta data existed");
		}
		if (!groupJson.has(identifier)) {
			return null;
		}
		return groupJson.getAsJsonObject(identifier);
	}

	public static JsonObject getPersistence(Group group) {
		JsonObject json = new JsonObject();
		for (Entry<String, GroupMetaDataView<?>> entry : views.entrySet()) {
			GroupMetaData meta = entry.getValue().getMetaData(group);
			if (meta == null) {
				continue;
			}
			JsonObject pluginObject = new JsonObject();
			meta.serialize(pluginObject);
			json.add(entry.getKey(), pluginObject);
		}
		return json;
	}

	public static boolean isDirty(Group group) {
		for (GroupMetaDataView<?> view : views.values()) {
			GroupMetaData meta = view.getMetaData(group);
			if (meta == null) {
				continue;
			}
			if (meta.isDirty()) {
				return true;
			}
		}
		return false;
	}

}
