package com.yahoo.prosfis.somnusmanager.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerUtil {

	public static boolean giveItem(Player player, ItemStack item) {
		boolean dropped = false;
		PlayerInventory inv = player.getInventory();
		if (inv.firstEmpty() != -1) {
			inv.addItem(item);
			player.updateInventory();
		} else {
			player.getWorld().dropItem(player.getLocation(), item);
			player.sendMessage(
					ChatColor.RED + "No inventory space found, item dropped at your location.");
			dropped = true;
		}
		return dropped;
	}
}
