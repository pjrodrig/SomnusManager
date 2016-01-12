package com.yahoo.prosfis.somnusmanager.events.events;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Maps;
import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.util.PlayerUtil;

public class MineContestEvent implements Event, Listener {

	private final SomnusManager sm;
	private Scoreboard scoreboard;
	private Objective objective;
	private HashMap<UUID, Score> scores;
	private Player topPlayer;
	private int topScore;

	public MineContestEvent(SomnusManager sm) {
		this.sm = sm;
	}

	public void start() {
		topScore = 0;
		final Server server = sm.getServer();
		final BukkitScheduler scheduler = server.getScheduler();
		server.broadcastMessage(ChatColor.GOLD + "Mine Contest: " + ChatColor.DARK_GREEN
				+ "The player to break the most blocks in 1 minute wins!");
		scheduler.runTaskLater(sm, new Runnable() {
			public void run() {
				server.broadcastMessage(ChatColor.RED + "Get ready!");
				scheduler.runTaskLater(sm, new Runnable() {
					public void run() {
						server.broadcastMessage(ChatColor.GOLD + "Get set!");
						scheduler.runTaskLater(sm, new Runnable() {
							public void run() {
								startContest();
								server.broadcastMessage(ChatColor.GREEN + "GO!");
							}
						}, 20 * 1);
					}
				}, 20 * 1);
			}
		}, 20 * 1);
	}

	public void startContest() {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("Blocks Broken", "Blocks Broken");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GOLD + "Blocks Broken");
		scores = Maps.newHashMap();
		Server server = sm.getServer();
		for (Player player : server.getOnlinePlayers()) {
			scores.put(player.getUniqueId(), null);
			player.setScoreboard(scoreboard);
		}
		server.getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				stop();
			}
		}, 20 * 30);
		server.getPluginManager().registerEvents(this, sm);
	}

	public void stop() {
		Server server = sm.getServer();
		PlayerJoinEvent.getHandlerList().unregister(this);
		BlockBreakEvent.getHandlerList().unregister(this);
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		for (Player player : server.getOnlinePlayers()) {
			player.setScoreboard(scoreboard);
		}
		server.broadcastMessage(ChatColor.DARK_GREEN + "The Mine Contest event is over!");
		if (topPlayer == null) {
			server.broadcastMessage(ChatColor.DARK_GREEN + "No players participated.");
		} else {
			server.broadcastMessage(ChatColor.DARK_GREEN + "Winner: " + ChatColor.GOLD
					+ topPlayer.getName() + ChatColor.DARK_GREEN + " with a score of "
					+ ChatColor.GOLD + topScore + ChatColor.DARK_GREEN + " blocks broken!");
			PlayerUtil.giveItem(topPlayer, new ItemStack(Material.GOLD_INGOT, 10));
		}
		topPlayer = null;
		topScore = 0;
	}

	@EventHandler
	public void mine(BlockBreakEvent event) {
		Player player = event.getPlayer();
		UUID id = player.getUniqueId();
		Score score = scores.get(id);
		if (score == null) {
			score = objective.getScore(player.getName());
			score.setScore(0);
			scores.put(id, score);
		}
		int currentScore = score.getScore() + 1;
		score.setScore(currentScore);
		if (topScore < currentScore) {
			if (topPlayer == null || !(topPlayer.equals(player))) {
				topPlayer = player;
			}
			topScore = currentScore;
		}
	}

	@EventHandler
	public void login(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.setScoreboard(scoreboard);
		player.sendMessage(ChatColor.GREEN + "You joined during a mine contest!");
		scores.put(player.getUniqueId(), null);
	}

}