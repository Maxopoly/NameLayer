package vg.civcraft.mc.namelayer.zeus;

import com.github.maxopoly.zeus.plugin.ZeusLoad;
import com.github.maxopoly.zeus.plugin.ZeusPlugin;

import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.AcceptInviteHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.BlacklistPlayerHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.CreateGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.CreateRankHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.DeleteGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.DeleteRankHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.EditPermissionHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.InvitePlayerHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.JoinGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.LeaveGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.LinkGroupsHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.MergeGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.PromotePlayerHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RegisterPermissionHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RejectInviteHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RemoveMemberHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RenameGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RenameRankHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RevokeInviteHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.SetPasswordHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.UnlinkGroupsHandler;

@ZeusLoad(name = "NameLayer", version = "1.0", description = "Player driven group management")
public class NameLayerZPlugin extends ZeusPlugin {

	private static NameLayerZPlugin instance;

	public static NameLayerZPlugin getInstance() {
		return instance;
	}

	private ZeusGroupTracker groupTracker;
	private ServerGroupKnowledgeTracker groupKnowledgeTracker;
	private NameLayerDAO dao;

	@Override
	public boolean onEnable() {
		instance = this;
		dao = new NameLayerDAO(getName(), logger);
		if (!dao.updateDatabase()) {
			return false;
		}
		groupTracker = new ZeusGroupTracker(dao);
		groupKnowledgeTracker = new ServerGroupKnowledgeTracker(groupTracker, dao);
		registerRabbitListeners();
		return true;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	private void registerRabbitListeners() {
		registerRabbitListener(new AcceptInviteHandler(), new BlacklistPlayerHandler(), new CreateGroupHandler(),
				new CreateRankHandler(), new DeleteGroupHandler(), new DeleteRankHandler(), new EditPermissionHandler(),
				new InvitePlayerHandler(), new JoinGroupHandler(), new LeaveGroupHandler(), new LinkGroupsHandler(),
				new MergeGroupHandler(), new PromotePlayerHandler(), new RegisterPermissionHandler(), new RejectInviteHandler(),
				new RemoveMemberHandler(), new RenameGroupHandler(), new RenameRankHandler(), new RevokeInviteHandler(),
				new SetPasswordHandler(), new UnlinkGroupsHandler());
	}

	public NameLayerDAO getDAO() {
		return dao;
	}

	public ServerGroupKnowledgeTracker getGroupKnowledgeTracker() {
		return groupKnowledgeTracker;
	}

	public ZeusGroupTracker getGroupTracker() {
		return groupTracker;
	}

}
