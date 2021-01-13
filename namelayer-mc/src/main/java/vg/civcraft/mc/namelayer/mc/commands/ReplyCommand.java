package vg.civcraft.mc.namelayer.mc.commands;

import java.util.UUID;

import javax.annotation.Syntax;

import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import vg.civcraft.mc.civmodcore.command.AikarCommand;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.ChatTracker;
import vg.civcraft.mc.namelayer.mc.model.chat.PrivateChatMode;
import vg.civcraft.mc.namelayer.mc.util.ChatStrings;

@Description("Replies to the last person you conversed with in private.")
@Syntax("/reply [message]")
public class ReplyCommand extends AikarCommand {

	private static final String ALIAS = "reply|r";

	private final ChatTracker modeManager;

	public ReplyCommand(final NameLayerPlugin plugin) {
		this.modeManager = plugin.getChatTracker();
	}

	@CommandAlias(ALIAS)
	public void switchToPrivate(final Player sender) {
		final UUID recipient = this.modeManager.getReplyChannel(sender);
		if (recipient == null) {
			sender.sendMessage(ChatStrings.chatNoOneToReplyTo);
			return;
		}
		this.modeManager.setChatMode(sender, new PrivateChatMode(recipient), true);
	}
	
	@CommandAlias(ALIAS)
	public void sendOneOffReply(final Player sender, final String message) {
		final UUID recipient = this.modeManager.getReplyChannel(sender);
		if (recipient == null) {
			sender.sendMessage(ChatStrings.chatNoOneToReplyTo);
			return;
		}
		PrivateChatMode.sendPrivateMessage(sender, recipient, message);
	}

}
