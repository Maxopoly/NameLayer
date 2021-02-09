package vg.civcraft.mc.namelayer.mc.commands;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitJoinGroup;

@CivCommand(id = "nljg")
public class JoinGroup extends StandaloneCommand {

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		String submittedPassword = args[1];
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitJoinGroup(player.getUniqueId(), args[0], submittedPassword));
		return true;
	}

}
