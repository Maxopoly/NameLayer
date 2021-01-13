package vg.civcraft.mc.namelayer.mc.util;


import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.playersettings.impl.DisplayLocationSetting;
import vg.civcraft.mc.civmodcore.scoreboard.bottom.BottomLine;
import vg.civcraft.mc.civmodcore.scoreboard.bottom.BottomLineAPI;
import vg.civcraft.mc.civmodcore.scoreboard.side.CivScoreBoard;
import vg.civcraft.mc.civmodcore.scoreboard.side.ScoreBoardAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.chat.ChatMode;

public class NLScoreBoard {
	private final BottomLine chatBottomLine;
	private final CivScoreBoard chatBoard;
	private final NameLayerSettingManager settingMan;

	public NLScoreBoard() {
		this.chatBoard = ScoreBoardAPI.createBoard("CivChatDisplay");
		this.chatBottomLine = BottomLineAPI.createBottomLine("CivChatDisplay", 3);
		this.settingMan = NameLayerPlugin.getInstance().getSettingsManager();
	}

	/** Updates the scoreboard to display the players currently in use private message channel or chat group
	 *
	 * @param p player to update scoreboard for
	 */
	public void updateScoreboardHUD(Player p) {
		if (!settingMan.getShowChatGroup(p.getUniqueId())) {
			chatBoard.hide(p);
			chatBottomLine.removePlayer(p);
		} else {
			DisplayLocationSetting locSetting = settingMan.getChatGroupLocation();
			ChatMode mode = NameLayerPlugin.getInstance().getChatTracker().getChatMode(p);
			String text = mode.getInfoText();
			if (locSetting.showOnActionbar(p.getUniqueId())) {
				chatBottomLine.updatePlayer(p, text);
			}
			if (locSetting.showOnSidebar(p.getUniqueId())) {
				chatBoard.set(p, text);
			}
		}
	}

}
