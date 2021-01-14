package vg.civcraft.mc.namelayer.mc.model.chat;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.NameAPI;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.playersettings.impl.StringSetting;
import vg.civcraft.mc.civmodcore.playersettings.impl.collection.ListSetting;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitSendGroupChatMessage;

public class GroupChatMode implements ChatMode {

	private int groupID;

	public GroupChatMode(int groupID) {
		this.groupID = groupID;
	}

	public Group getGroup() {
		return GroupAPI.getGroup(groupID);
	}

	@Override
	public String getInfoText() {
		Group group = getGroup();
		return String.format("%sChatting in %s", ChatColor.AQUA, group != null ? group.getColoredName() : "unknown");
	}

	@Override
	public void processInput(String text, Player player) {
		Group group = getGroup();
		if (group == null) {
			player.sendMessage(ChatColor.RED + "The group you were chatting in no longer exists");
			NameLayerPlugin.getInstance().getChatTracker().resetChatMode(player);
			return;
		}
		sendGroupMessage(player, group, text);
	}

	public static void doLocalGroupChatMessageDistribute(Group group, UUID sender, String msg) {
		String senderName = NameAPI.getName(sender);
		String message = String.format("%s[%s%s] %s%s: %s%s", ChatColor.GRAY, group.getColoredName(), ChatColor.GRAY, senderName, ChatColor.GRAY, ChatColor.WHITE, msg);
		PermissionType readPerm = NameLayerPlugin.getInstance().getNameLayerPermissionManager().getReadChat();
		ListSetting<String> ignoredGroups = NameLayerPlugin.getInstance().getSettingsManager().getIgnoredGroups();
		ListSetting<String> ignoredPlayers = NameLayerPlugin.getInstance().getSettingsManager().getIgnoredPlayers();
		for (Player player : Bukkit.getOnlinePlayers()) {
			// loop players, not group members based on assumption of shard player count and
			// average group size
			if (!GroupAPI.hasPermission(player, group, readPerm)) {
				continue;
			}
			if (ignoredGroups.getValue(player).contains(group.getName())) {
				continue;
			}
			if (ignoredPlayers.getValue(player).contains(senderName)) {
				continue;
			}
			player.sendMessage(message);
		}
	}

	/**
	 * Initiates broadcast of a message to a group by sending the appropriate
	 * request to Zeus
	 *
	 * @param sender  Playwer sending the message
	 * @param group   Group to send the message too
	 * @param message Message to send to the group
	 */
	public static void sendGroupMessage(final Player sender, final Group group, final String message) {
		Preconditions.checkArgument(sender != null, "Sender cannot be null!");
		Preconditions.checkArgument(group != null, "Group cannot be null!");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(message), "Message must not be null or empty!");
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new RabbitSendGroupChatMessage(sender.getUniqueId(), group.getName(), message));
	}
	
	public boolean equals(Object o) {
		return o instanceof GroupChatMode && ((GroupChatMode) o).groupID == this.groupID;
	}

	@Override
	public void setInternalStorage(Player player, StringSetting modeSetting, StringSetting valueSetting) {
		modeSetting.setValue(player, ChatMode.Modes.GROUP.toString());
		valueSetting.setValue(player, String.valueOf(groupID));
	}

}
