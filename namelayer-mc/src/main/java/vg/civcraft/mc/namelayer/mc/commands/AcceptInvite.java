package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitAcceptInvite;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nlag")
public class AcceptInvite extends StandaloneCommand {

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupInvitedTo(args[0], (Player) sender);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args [0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args [0]);
			return true;
		}
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new RabbitAcceptInvite(player.getUniqueId(), group));
		return true;
	}
}
