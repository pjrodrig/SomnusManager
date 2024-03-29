package com.yahoo.prosfis.somnusmanager.marriage.listeners;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.marriage.MarriageManager;

public class IDoListener implements Listener {

	private final SomnusManager sm;
	private final MarriageManager mm;
	private final Location loc;
	private final Player p1, p2, priest;
	private Player turn;
	private boolean listen, first;
	private BukkitTask timer;

	public IDoListener(final Player p1, final Player p2, final Player priest, Location loc,
			final MarriageManager mm, final SomnusManager sm) {
		this.sm = sm;
		this.mm = mm;
		this.p1 = p1;
		this.p2 = p2;
		this.priest = priest;
		this.loc = loc;
		listen = false;
		first = true;
		final Server server = sm.getServer();
		server.broadcastMessage(ChatColor.GRAY + priest.getName() + ": " + ChatColor.GRAY
				+ "The wedding will commence shortly.");
		server.getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				broadcastNear(ChatColor.YELLOW + priest.getName() + ": Do you, " + p1.getName()
						+ ", take " + p2.getName() + " to be your life long partner in marriage?");
				turn = p1;
				listen = true;
				timer = server.getScheduler().runTaskLater(sm, new Runnable() {
					public void run() {
						server.broadcastMessage(ChatColor.LIGHT_PURPLE + p1.getName()
								+ "failed to say 'I do'. The wedding was canceled.");
						mm.cancelWedding();
						timer = null;
						unregister();
					}
				}, 20 * 20);
			}
		}, 20 * 15);
	}

	@EventHandler
	public void logout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.equals(p1) || player.equals(p2) || player.equals(priest)) {
			mm.cancelWedding(player);
			unregister();
		}
	}

	@EventHandler
	public void iDo(AsyncPlayerChatEvent event) {
		if (listen) {
			if (event.getPlayer().equals(turn)) {
				String message = event.getMessage().toUpperCase();
				if (message.matches("\\s*I DO\\s*")) {
					sm.getServer().getScheduler().runTaskLater(sm, new Runnable() {
						public void run() {
							next();
						}
					}, 20);
				}
			}
		}
	}

	private void next() {
		timer.cancel();
		if (first) {
			turn = p2;
			first = false;
			broadcastNear(ChatColor.GRAY + priest.getName() + ": " + ChatColor.YELLOW
					+ priest.getName() + ": ..and do you, " + p2.getName() + ", take "
					+ p1.getName() + " to be your life long partner in marriage?");
			final Server server = sm.getServer();
			timer = server.getScheduler().runTaskLater(sm, new Runnable() {
				public void run() {
					server.broadcastMessage(ChatColor.LIGHT_PURPLE + p2.getName()
							+ "failed to say 'I do'. The wedding was canceled.");
					mm.cancelWedding();
					timer = null;
					unregister();
				}
			}, 20 * 30);
		} else {
			mm.marry(p1, p2);
			unregister();
		}
	}

	private void broadcastNear(String message) {
		Iterator<? extends Player> iter = sm.getServer().getOnlinePlayers().iterator();
		Player player;
		while (iter.hasNext()) {
			player = iter.next();
			if (player.getLocation().distance(loc) <= 50) {
				player.sendMessage(message);
			}
		}
	}

	public void unregister() {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
	}
}
