package com.yahoo.prosfis.somnusmanager.marriage.listeners;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MarriageChatListener implements Listener {

	private final Player player, spouse;

	public MarriageChatListener(Player player, Player spouse) {
		this.player = player;
		this.spouse = spouse;
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent event) {
		if (event.getPlayer().equals(player)) {
			Set<Player> recievers = event.getRecipients();
			event.setFormat(ChatColor.DARK_GREEN + "[MC]" + ChatColor.RESET
					+ player.getDisplayName() + ":" + ChatColor.GREEN);
			recievers.clear();
			recievers.add(spouse);
		}
	}

	@EventHandler
	public void logout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.equals(player) || player.equals(spouse)) {
			unregister();
		}
	}

	public void unregister() {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
	}

}
