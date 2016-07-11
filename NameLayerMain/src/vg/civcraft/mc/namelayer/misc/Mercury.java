package vg.civcraft.mc.namelayer.misc;

import vg.civcraft.mc.mercury.MercuryAPI;

public class Mercury {

	/**
	 * Invalidates a group across all servers.
	 * @param message- The message to send.
	 */
	public static void invalidatGroup(String message){
		MercuryAPI.sendGlobalMessage(message, "namelayer");
	}
}
