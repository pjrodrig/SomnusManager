package com.yahoo.prosfis.somnusmanager.marriage.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.yahoo.prosfis.somnusmanager.marriage.MarriageManager;

public class LogoutListener implements Listener {

	private final Player p1, p2;
	private final MarriageManager mm;

	public LogoutListener(Player p1, Player p2, MarriageManager mm) {
		this.p1 = p1;
		this.p2 = p2;
		this.mm = mm;
	}

	@EventHandler
	public void logout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.equals(p1) || player.equals(p2)) {
			mm.cancelWedding(player);
			unregister();
		}
	}

	public void unregister() {
		PlayerQuitEvent.getHandlerList().unregister(this);
	}

}
