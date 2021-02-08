package vg.civcraft.mc.namelayer.mc.commands;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitSetGroupPassword;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id="nlsp")
public class SetPassword extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		if (args.length == 1) {
			player.sendMessage(ChatColor.RED + "You cannot set a blank password");
			return true;
		}
		String password = args[1];
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitSetGroupPassword(player.getUniqueId(), group, password));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		}
		return Collections.emptyList();
	}
}
