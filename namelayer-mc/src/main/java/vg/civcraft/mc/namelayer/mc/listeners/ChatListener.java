package vg.civcraft.mc.namelayer.mc.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.chat.ChatMode;
import vg.civcraft.mc.namelayer.mc.model.chat.GroupChatMode;
import vg.civcraft.mc.namelayer.mc.model.chat.LocalChatMode;
import vg.civcraft.mc.namelayer.mc.model.chat.PrivateChatMode;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

public class ChatListener implements Listener {
	
	private NameLayerSettingManager settings;
	
	public ChatListener(NameLayerPlugin plugin) {
		this.settings = plugin.getSettingsManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoin(PlayerJoinEvent event) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Player player = event.getPlayer();
				ChatMode.Modes mode = ChatMode.Modes.valueOf(settings.getChatModeSetting().getValue(player));
				String value = settings.getChatModeValueSetting().getValue(player);
				ChatMode modeToSet = null;
				switch (mode) {
				case GROUP:
					int id = Integer.parseInt(value);
					Group group = GroupAPI.getGroup(id);
					if (group != null) {
						modeToSet = new GroupChatMode(id);
					}
					break;
				case LOCAL:
					modeToSet = new LocalChatMode();
					break;
				case PM:
					UUID partner = UUID.fromString(value);
					modeToSet = new PrivateChatMode(partner);
					break;
				default:
					break;
				}
				if (modeToSet != null) {
					NameLayerPlugin.getInstance().getChatTracker().setChatMode(player, modeToSet, false);
				}
			}
		}.runTaskLater(NameLayerPlugin.getInstance(), 1L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChatEvent(final AsyncPlayerChatEvent asyncPlayerChatEvent) {

		asyncPlayerChatEvent.setCancelled(true);
		// This needs to be done sync to avoid a rare deadlock due to minecraft
		// internals
		new BukkitRunnable() {

			@Override
			public void run() {
				NameLayerPlugin.getInstance().getChatTracker().getChatMode(asyncPlayerChatEvent.getPlayer())
						.processInput(asyncPlayerChatEvent.getMessage(), asyncPlayerChatEvent.getPlayer());
			}
		}.runTask(NameLayerPlugin.getInstance());
	}
}
