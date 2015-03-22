package com.yahoo.prosfis.somnusmanager.arena.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.yahoo.prosfis.somnusmanager.arena.ArenaManager;

public class ArenaListener implements Listener {

	private final ArenaManager am;

	public ArenaListener(ArenaManager am) {
		this.am = am;
	}

	@EventHandler
	public void challenge(PlayerInteractEntityEvent event) {
		Entity ent = event.getRightClicked();
		if (ent instanceof Player) {
			Player challenger = event.getPlayer();
			Material item = challenger.getItemInHand().getType();
			if (item == Material.WOOD_SWORD || item == Material.IRON_SWORD
					|| item == Material.GOLD_SWORD
					|| item == Material.DIAMOND_SWORD) {
				am.challenge(challenger, (Player) ent);
			}
		}
	}

	@EventHandler
	public void leave(PlayerQuitEvent event) {
		am.quit(event.getPlayer(), false);
	}
}
