package vg.civcraft.mc.namelayer.mc.model;


import vg.civcraft.mc.civmodcore.command.AikarCommandManager;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.commands.GroupChatCommand;
import vg.civcraft.mc.namelayer.mc.commands.IgnoreGroupCommand;
import vg.civcraft.mc.namelayer.mc.commands.IgnoreListCommand;
import vg.civcraft.mc.namelayer.mc.commands.IgnorePlayerCommand;
import vg.civcraft.mc.namelayer.mc.commands.LocalChatCommand;
import vg.civcraft.mc.namelayer.mc.commands.PrivateChatCommand;
import vg.civcraft.mc.namelayer.mc.commands.ReplyCommand;
import vg.civcraft.mc.namelayer.mc.commands.WhoAmICommand;

public class AikarCommandRegistrar extends AikarCommandManager {

	private final NameLayerPlugin plugin;

	public AikarCommandRegistrar(NameLayerPlugin plugin) {
		super(plugin, false);
		this.plugin = plugin;
	}

	@Override
	public void registerCommands() {
		registerCommand(new GroupChatCommand(this.plugin));
		registerCommand(new IgnoreGroupCommand(this.plugin));
		registerCommand(new IgnoreListCommand(this.plugin));
		registerCommand(new IgnorePlayerCommand(this.plugin));
		registerCommand(new LocalChatCommand(this.plugin));
		registerCommand(new PrivateChatCommand(this.plugin));
		registerCommand(new ReplyCommand(this.plugin));
		registerCommand(new WhoAmICommand());
	}

}