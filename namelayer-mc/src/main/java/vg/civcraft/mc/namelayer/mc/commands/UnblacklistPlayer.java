package vg.civcraft.mc.namelayer.mc.commands;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitUnblacklistPlayer;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;


//nlubl <group> <player>
@CivCommand(id = "nlubl")
public class UnblacklistPlayer extends NameLayerCommand {
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player executor = (Player) sender;
		Group group = GroupAPI.getGroup(args [0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(executor.getUniqueId(), args[0]);
			return true;
		}
		String targetPlayer = args[1];
		ArtemisPlugin
				.getInstance().getRabbitHandler().sendMessage(new RabbitUnblacklistPlayer(executor.getUniqueId(), group.getName(), targetPlayer));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		switch (args.length) {
			case 0:
				return NameLayerTabCompletion.completeGroupName("", (Player) sender);
			case 1:
				return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
			case 2:
				return NameLayerTabCompletion.completePlayer(args[1]);
			default:
				return Collections.emptyList();
		}
	}
}
