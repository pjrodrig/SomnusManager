package com.yahoo.prosfis.somnusmanager.events.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.util.PlayerUtil;

public class QuietTimeEvent implements Event, Listener {

	private final SomnusManager sm;
	private List<UUID> losers;

	public QuietTimeEvent(SomnusManager sm) {
		this.sm = sm;
	}

	public void start() {
		losers = new ArrayList<UUID>();
		final Server server = sm.getServer();
		final BukkitScheduler scheduler = server.getScheduler();
		final Listener thisListener = this;
		server.broadcastMessage(ChatColor.GOLD + "Quiet Time: " + ChatColor.DARK_GREEN
				+ "Don't speak for 5 minutes to win!");
		server.getPluginManager().registerEvents(this, sm);
		scheduler.runTaskLater(sm, new Runnable() {
			public void run() {
				server.broadcastMessage(ChatColor.GOLD + "Quiet time starts now!");
				server.getPluginManager().registerEvents(thisListener, sm);
				scheduler.runTaskLater(sm, new Runnable() {
					public void run() {
						server.broadcastMessage(
								ChatColor.GOLD + "4" + ChatColor.DARK_GREEN + " minutes remaining");
						scheduler.runTaskLater(sm, new Runnable() {
							public void run() {
								server.broadcastMessage(ChatColor.GOLD + "3" + ChatColor.DARK_GREEN
										+ " minutes remaining");
								scheduler.runTaskLater(sm, new Runnable() {
									public void run() {
										server.broadcastMessage(ChatColor.GOLD + "2"
												+ ChatColor.DARK_GREEN + " minutes remaining");
										scheduler.runTaskLater(sm, new Runnable() {
											public void run() {
												server.broadcastMessage(
														ChatColor.GOLD + "1" + ChatColor.DARK_GREEN
																+ " minute remaining");
												scheduler.runTaskLater(sm, new Runnable() {
													public void run() {
														stop();
													}
												}, 20 * 60 * 1);
											}
										}, 20 * 60 * 1);
									}
								}, 20 * 60 * 1);
							}
						}, 20 * 60 * 1);
					}
				}, 20 * 60 * 1);
			}
		}, 20 * 3);
	}

	public void stop() {
		unregister();
		Server server = sm.getServer();
		for (Player player : server.getOnlinePlayers()) {
			if (!losers.contains(player.getUniqueId())) {
				player.sendMessage(ChatColor.DARK_GREEN + "You have successfully completed the "
						+ ChatColor.GOLD + "Quiet Time" + ChatColor.DARK_GREEN + " event!");
				PlayerUtil.giveItem(player, new ItemStack(Material.GOLD_INGOT, 10));
			}
		}
		server.broadcastMessage(
				ChatColor.DARK_GREEN + "The contest is over. All rewards have been given!");
	}

	private void unregister() {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
		PlayerJoinEvent.getHandlerList().unregister(this);
	}

	@EventHandler
	public void speak(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		UUID id = player.getUniqueId();
		if (!losers.contains(id)) {
			player.sendMessage(ChatColor.DARK_GREEN + "You have lost the Quiet Time event!");
			losers.add(id);
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent event) {
		event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You have joined during a "
				+ ChatColor.GOLD + "Quiet Time" + ChatColor.DARK_GREEN + "event.");
	}
}
