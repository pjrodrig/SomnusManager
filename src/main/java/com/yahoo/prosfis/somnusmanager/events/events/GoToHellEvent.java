package com.yahoo.prosfis.somnusmanager.events.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.util.PlayerUtil;

public class GoToHellEvent implements Event, Listener {

	private final SomnusManager sm;
	private Player winner;
	private BukkitTask task;

	public GoToHellEvent(SomnusManager sm) {
		this.sm = sm;
	}

	public void start() {
		winner = null;
		Server server = sm.getServer();
		server.getPluginManager().registerEvents(this, sm);
		server.broadcastMessage(ChatColor.GOLD + "Go To Hell: " + ChatColor.DARK_GREEN
				+ "The first player to suicide in lava in the nether wins! You have 3 minutes!");
		task = server.getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				stop();
			}
		}, 20 * 60 * 3);
	}

	public void stop() {
		EntityDamageEvent.getHandlerList().unregister(this);
		PlayerJoinEvent.getHandlerList().unregister(this);
		Server server = sm.getServer();
		server.broadcastMessage(ChatColor.DARK_GREEN + "The event is over!");
		if (winner == null) {
			server.broadcastMessage(
					ChatColor.DARK_GREEN + "No one completed the challenge before time ran out!");
		} else {
			server.broadcastMessage(
					ChatColor.GOLD + winner.getName() + ChatColor.DARK_GREEN + " made it to hell!");
			PlayerUtil.giveItem(winner, new ItemStack(Material.GOLD_INGOT, 10));
		}
	}

	@EventHandler
	public void death(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player && event.getCause() == DamageCause.LAVA) {
			if (((Player) entity).getHealth() <= event.getFinalDamage()) {
				winner = (Player) entity;
				task.cancel();
				stop();
			}
		}
	}

	@EventHandler
	public void respawn(PlayerRespawnEvent event) {
		if (winner != null && winner.equals(event.getPlayer())) {
			PlayerUtil.giveItem(winner, new ItemStack(Material.GOLD_INGOT, 10));
			PlayerRespawnEvent.getHandlerList().unregister(this);
		}
	}

	@EventHandler
	public void login(PlayerJoinEvent event) {
		event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You've joined during a "
				+ ChatColor.GOLD + "Go To Hell" + ChatColor.DARK_GREEN + " event.");
	}
}
