package vg.civcraft.mc.namelayer.mc.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.AikarCommand;

public class WhoAmICommand extends AikarCommand {

	@CommandAlias("whoami")
	@Description("Tells you who you are.")
	public void whoAmI(final CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "You are: " + ChatColor.WHITE + sender.getName());
	}

}
