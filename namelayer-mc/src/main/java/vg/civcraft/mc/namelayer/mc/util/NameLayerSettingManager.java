package vg.civcraft.mc.namelayer.mc.util;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import vg.civcraft.mc.civmodcore.playersettings.PlayerSettingAPI;
import vg.civcraft.mc.civmodcore.playersettings.gui.MenuSection;
import vg.civcraft.mc.civmodcore.playersettings.impl.BooleanSetting;
import vg.civcraft.mc.civmodcore.playersettings.impl.DisplayLocationSetting;
import vg.civcraft.mc.civmodcore.playersettings.impl.JsonSetting;
import vg.civcraft.mc.civmodcore.playersettings.impl.collection.ListSetting;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

public class NameLayerSettingManager {

	private PlayerGroupSetting defaultGroup;
	private BooleanSetting autoAcceptInvites;
	private JsonSetting folderStorage;
	private BooleanSetting sortGUIByRank;
	
	private ListSetting<String> ignoredPlayers;
	private ListSetting<String> ignoredGroups;
	private BooleanSetting showJoins;
	private BooleanSetting showLeaves;
	private BooleanSetting sendOwnKills;
	private BooleanSetting receiveKills;
	private BooleanSetting receiveKillsFromIgnoredPlayers;
	private BooleanSetting showChatGroup;
	private DisplayLocationSetting chatGroupLocation;

	public NameLayerSettingManager() {
		initSettings();
	}

	private void initSettings() {
		NameLayerPlugin plugin = NameLayerPlugin.getInstance();
		MenuSection menu = PlayerSettingAPI.getMainMenu().createMenuSection("NameLayer",
				"NameLayer and group related settings");
		defaultGroup = new PlayerGroupSetting(plugin, "Default group", "nlDefaultGroup",
				new ItemStack(Material.GOLDEN_HELMET),
				"The default group which will be used by commands if you don't specify one");
		PlayerSettingAPI.registerSetting(defaultGroup, menu);

		autoAcceptInvites = new BooleanSetting(plugin, false, "Auto accept group invites",
				"nlAutoAcceptInvites", "Should group invites be automatically accepted");
		PlayerSettingAPI.registerSetting(autoAcceptInvites, menu);
		
		folderStorage = new JsonSetting(plugin, new JsonObject(), "nlFolderStorage");
		PlayerSettingAPI.registerSetting(folderStorage, null);
		
		sortGUIByRank = new BooleanSetting(plugin, false, "Should the GUI be sorted by rank",
				"nlSortGuiByRank", "If set to false, it will be sorted by member name");
		PlayerSettingAPI.registerSetting(sortGUIByRank, menu);
		
		this.ignoredPlayers = new ListSetting<>(plugin, new ArrayList<>(), "Ignored Players", "ignoredPlayers",
				new ItemStack(Material.BARRIER), "The list of players you are ignoring.", String.class);
		this.ignoredGroups = new ListSetting<>(plugin, new ArrayList<>(), "Ignored Groups", "ignoredGroups",
				new ItemStack(Material.BARRIER), "The list of groups you are ignoring.", String.class);
		this.showJoins = new BooleanSetting(plugin, true, "Show Player Joins", "showJoins",
				"Should player join messages be shown?");
		this.showLeaves = new BooleanSetting(plugin, true, "Show Players Leaving", "showLeaves",
				"Should player leave messages be shown?");
		this.sendOwnKills = new BooleanSetting(plugin, true, "Broadcast your kills", "civChatBroadcastKills",
				"Should kills you make be broadcast to nearby players?");
		this.receiveKills = new BooleanSetting(plugin, true, "Receive kill broadcasts",
				"civChatReceiveKills", "Do you want to receive broadcasts for nearby kills");
		this.receiveKillsFromIgnoredPlayers = new BooleanSetting(plugin, false,
				"Receive kill broadcasts from ignored players", "civChatReceiveKillsIgnored",
				"Do you want to receive kill broadcasts from killers you have ignored");
		this.showChatGroup = new BooleanSetting(plugin, true, "Show current chat group", "showChatGroup",
				"Should player chat group be shown?");
		this.chatGroupLocation = new DisplayLocationSetting(plugin, DisplayLocationSetting.DisplayLocation.SIDEBAR,
				"Chat Group Location", "chatGroupLocation", new ItemStack(Material.ARROW), "the current chat group");
		MenuSection chatMenu = PlayerSettingAPI.getMainMenu().createMenuSection(
				"CivChat", "All options related to CivChat.");
		PlayerSettingAPI.registerSetting(this.ignoredPlayers, chatMenu);
		PlayerSettingAPI.registerSetting(this.ignoredGroups, chatMenu);
		PlayerSettingAPI.registerSetting(this.showJoins, chatMenu);
		PlayerSettingAPI.registerSetting(this.showLeaves, chatMenu);
		PlayerSettingAPI.registerSetting(this.sendOwnKills, chatMenu);
		PlayerSettingAPI.registerSetting(this.receiveKills, chatMenu);
		PlayerSettingAPI.registerSetting(this.receiveKillsFromIgnoredPlayers, chatMenu);
		PlayerSettingAPI.registerSetting(this.showChatGroup, chatMenu);
		PlayerSettingAPI.registerSetting(this.chatGroupLocation, chatMenu);
	}
	
	public JsonSetting getFolderStorage() {
		return folderStorage;
	}
	
	public BooleanSetting getGUISortSetting() {
		return sortGUIByRank;
	}

	public PlayerGroupSetting getDefaultGroup() {
		return defaultGroup;
	}
	
	public BooleanSetting getAutoAcceptInvites() {
		return autoAcceptInvites;
	}
	
	public boolean getShowJoins(UUID uuid) {
		return this.showJoins.getValue(uuid);
	}

	public boolean getShowLeaves(UUID uuid) {
		return this.showLeaves.getValue(uuid);
	}
	
	public boolean getSendOwnKills(UUID uuid) {
		return this.sendOwnKills.getValue(uuid);
	}
	
	public boolean getReceiveKills(UUID uuid) {
		return this.receiveKills.getValue(uuid);
	}
	
	public boolean getReceiveKillsFromIgnored(UUID uuid) {
		return this.receiveKillsFromIgnoredPlayers.getValue(uuid);
	}

	public boolean getShowChatGroup(UUID uuid) {
		return this.showChatGroup.getValue(uuid);
	}

	public DisplayLocationSetting getChatGroupLocation() {
		return this.chatGroupLocation;
	}
	
	public ListSetting<String> getIgnoredPlayers() {
		return this.ignoredPlayers;
	}

	public ListSetting<String> getIgnoredGroups() {
		return this.ignoredGroups;
	}

}
