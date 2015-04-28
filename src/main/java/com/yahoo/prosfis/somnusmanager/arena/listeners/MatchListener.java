package com.yahoo.prosfis.somnusmanager.arena.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.yahoo.prosfis.somnusmanager.arena.ArenaManager;

public class MatchListener implements Listener {

	private final ArenaManager am;
	private final Player red, blue;

	public MatchListener(Player red, Player blue, ArenaManager am) {
		this.am = am;
		this.red = red;
		this.blue = blue;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void takeDamage(EntityDamageEvent event) {
		Entity ent = event.getEntity();
		boolean isRed = false;
		if (ent.equals(red)) {
			isRed = true;
		}
		if (isRed || ent.equals(blue)) {
			Player player = (Player) ent;
			double damage = event.getFinalDamage(), health = player.getHealth();
			if (damage >= health) {
				event.setCancelled(true);
				if (isRed)
					am.endMatch(blue, red);
				else
					am.endMatch(red, blue);
			} else {
				if (damage > 1) {
					event.setDamage(.5);
					player.setHealth(health - (damage - .5));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void death(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (player.equals(red)) {
			am.endMatch(blue, red);
			event.setKeepInventory(true);
			event.setKeepLevel(true);
		} else if (player.equals(blue)) {
			am.endMatch(red, blue);
			event.setKeepInventory(true);
			event.setKeepLevel(true);
		}
	}

	@EventHandler
	public void teleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		if (player.equals(red) || player.equals(blue)) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "Use '/arena quit' to quit.");
		}
	}

	public void unregister() {
		EntityDamageEvent.getHandlerList().unregister(this);
		PlayerDeathEvent.getHandlerList().unregister(this);
		PlayerTeleportEvent.getHandlerList().unregister(this);
	}

}
