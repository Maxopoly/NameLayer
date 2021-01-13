package vg.civcraft.mc.namelayer.mc.commands;

import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.model.PlayerData;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import vg.civcraft.mc.civmodcore.command.AikarCommand;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.ChatTracker;
import vg.civcraft.mc.namelayer.mc.model.chat.PrivateChatMode;
import vg.civcraft.mc.namelayer.mc.util.ChatStrings;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

@Description("Leaves your private chat.")
public class PrivateChatCommand extends AikarCommand {

	private static final String ALIAS = "message|msg|m|pm|tell";

	private final ChatTracker modeManager;
	private final NameLayerSettingManager settings;

	public PrivateChatCommand(final NameLayerPlugin plugin) {
		this.settings = plugin.getSettingsManager();
		this.modeManager = plugin.getChatTracker();
	}

	@CommandAlias(ALIAS)
	public void togglePrivateChat(final Player sender) {
		this.modeManager.resetChatMode(sender);
	}

	@CommandAlias(ALIAS)
	@CommandCompletion("@players")
	public void switchToPrivateChat(final Player sender, final String name) {
		final PlayerData receiverData = ArtemisPlugin.getInstance().getPlayerDataManager().getOnlinePlayerData(name);
		if (receiverData == null) {
			sender.sendMessage(ChatStrings.chatRecipientNowOffline);
			return;
		}
		this.modeManager.setChatMode(sender, new PrivateChatMode(receiverData.getUUID()), true);
	}

	@CommandAlias(ALIAS)
	@CommandCompletion("@players @none")
	public void sendOneOffPrivateMessage(final Player sender, final String name, final String message) {
		final PlayerData receiverData = ArtemisPlugin.getInstance().getPlayerDataManager().getOnlinePlayerData(name);
		if (receiverData == null) {
			sender.sendMessage(ChatStrings.chatRecipientNowOffline);
			return;
		}
		PrivateChatMode.sendPrivateMessage(sender, receiverData.getUUID(), message);
	}

}