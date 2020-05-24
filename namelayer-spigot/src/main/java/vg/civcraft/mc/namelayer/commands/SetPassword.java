package vg.civcraft.mc.namelayer.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;

@CivCommand(id="nlsp")
public class SetPassword extends AbstractGroupCommand {

	@Override
	public boolean execute(Player player, Group group, String[] args) {
		if (!permsCheck(group, player, NameLayerPlugin.getInstance().getNLPermissionManager().getPassword())) {
			return true;
		}
		String password = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		NameLayerPlugin.getInstance().getNameLayerMeta().getMetaData(group).setPassword(password);
		return true;
	}

	@Override
	public List<String> tabCompleteFurther(Player player, String[] args) {
		return Collections.emptyList();
	}
}
