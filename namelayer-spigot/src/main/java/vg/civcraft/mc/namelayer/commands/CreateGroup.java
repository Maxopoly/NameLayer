package vg.civcraft.mc.namelayer.commands;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;

@CivCommand(id="nlcg")
public class CreateGroup extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		String name = args[0];
		
		//enforce regulations on the name
		if (!isConform(name, player)) {
			return true;
		}
		
		Group existing = GroupAPI.getGroupByName(name);
		if (existing != null) {
			player.sendMessage(ChatColor.RED + "The group " + existing.getColoredName() + " already exists");
			return true;
		}
		player.sendMessage(ChatColor.GREEN + "Creating group...");
		NameLayerPlugin.getInstance().getGroupManager().createGroupAsync(name, player.getUniqueId(), g -> {
			if (g == null) {
				player.sendMessage(ChatColor.RED + "That group name is already taken or creation failed.");
				return;
			}
			player.sendMessage(ChatColor.GREEN + "The group " + g.getName() + " was successfully created.");
			NameLayerPlugin.getInstance().getLogger().log(Level.INFO, "Group " + g.getName() + " was created by " + player.getName());
		});
		return true;
	}
	
	public static boolean isConform(String name, Player toReplyTo) {
		if (name.length() > 32) {
			toReplyTo.sendMessage(ChatColor.RED + "The group name is not allowed to contain more than 32 characters");
			return false;
		}
		Charset latin1 = StandardCharsets.ISO_8859_1;
		boolean invalidChars = false;
		if (!latin1.newEncoder().canEncode(name)) {
			invalidChars = true;
		}
		
		for(char c:name.toCharArray()) {
			if (Character.isISOControl(c)) {
				invalidChars = true;
			}
		}
		if (invalidChars) {
			toReplyTo.sendMessage(ChatColor.RED + "You used characters, which are not allowed");
			return false;
		}
		return true;
	}

	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}
}
