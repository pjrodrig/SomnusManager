package com.yahoo.prosfis.somnusmanager.dungeons.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DungeonListener implements Listener {

	public DungeonListener() {

	}

	@EventHandler
	public void throwPearl(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (item != null && item.getType() == Material.ENDER_PEARL) {
			Player player = event.getPlayer();
			if (player.getWorld().getName().equalsIgnoreCase("Dungeon")) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot use that here.");
			}
		}
	}

//	@EventHandler(ignoreCancelled = true)
//	public void takeDamage(EntityDamageEvent event) {
//		Entity ent = event.getEntity();
//		if (ent instanceof Player
//				&& ent.getWorld().getName().equalsIgnoreCase("Dungeon")) {
//			Player player = (Player) ent;
//			double damage = event.getFinalDamage(), health = player.getHealth();
//			ItemStack[] armor = player.getInventory().getArmorContents();
//			if (damage > 2) {
//				if (damage >= health)
//					damage = health;
//				event.setDamage(1);
//				player.setHealth(health - (damage - 1));
//			}
//		}
//	}
}
