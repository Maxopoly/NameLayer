package vg.civcraft.mc.namelayer.gui;

import java.util.ArrayList;
import java.util.List;

public class MenuUtils {
	
	/**
	 * Doesn't work
	 *
	 * @param lore the lore to split
	 * @return a split list of lores
	 */
	public static List<String> splitLore(String lore) {
		List<String> splitLore = new ArrayList<>();
		int maxLineLength = 50;
		StringBuilder sb = new StringBuilder();
		String [] split = lore.split(" ");
		for(int i = 0; i < split.length; i++) {
			String word = split [i];
			if ((sb.length() + word.length()) > maxLineLength) {
				//max line length reached
				if (sb.length() == 0) {
					//if empty, the word alone fills the line length so put it in anyway
					sb.append(word);
				}
				else {
					//include word in next run
					i--;
				}
				//add finished line
				splitLore.add(sb.toString());
				sb = new StringBuilder();
			}
			else {
				//just append, line not full yet
				sb.append(" ");
				sb.append(word);
			}
		}
		return splitLore;
	}
}
