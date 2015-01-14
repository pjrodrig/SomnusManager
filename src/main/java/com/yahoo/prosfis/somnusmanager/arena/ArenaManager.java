package com.yahoo.prosfis.somnusmanager.arena;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.util.ConfigUtil;

public class ArenaManager {

	private final SomnusManager sm;
	private Location warp, redSpawn, blueSpawn;
	private FileConfiguration arenaConfig = null;
	private File arenaConfigFile = null;
	private final Map<UUID, ArrayList<UUID>> challenges;
	private final LinkedList<Match> queue;
	private final LinkedList<UUID> queued;
	private boolean active;
	private UUID red, blue;

	public ArenaManager(SomnusManager sm) {
		this.sm = sm;
		this.challenges = Maps.newHashMap();
		this.queue = new LinkedList<Match>();
		this.queued = new LinkedList<UUID>();
		active = false;
		init();
	}

	public void init() {
		Server server = sm.getServer();
		FileConfiguration config = getArenaConfig();
		warp = ConfigUtil.loadLocation(server, config, "Warp.");
		redSpawn = ConfigUtil.loadLocation(server, config, "Spawn.Red.");
		blueSpawn = ConfigUtil.loadLocation(server, config, "Spawn.Blue.");
	}

	public void setWarp(Location warp) {
		this.warp = warp;
		FileConfiguration config = getArenaConfig();
		String path = "Warp.";
		ConfigUtil.saveLocation(config, path, warp);
		saveArenaConfig();
	}

	public void setRedSpawn(Location redSpawn) {
		this.redSpawn = redSpawn;
		FileConfiguration config = getArenaConfig();
		String path = "Spawn.Red.";
		ConfigUtil.saveLocation(config, path, redSpawn);
		saveArenaConfig();
	}

	public void setBlueSpawn(Location blueSpawn) {
		this.blueSpawn = blueSpawn;
		FileConfiguration config = getArenaConfig();
		String path = "Spawn.Blue.";
		ConfigUtil.saveLocation(config, path, blueSpawn);
		saveArenaConfig();
	}

	public void warp(Player player) {
		player.teleport(warp);
	}

	public void challenge(Player challenger, Player challengee) {
		UUID challengerId = challenger.getUniqueId(), challengeeId = challengee
				.getUniqueId();
		if (canChallenge(challenger, challengee, challengerId, challengeeId)) {
			if (isAccepting(challengerId, challengeeId)) {
				challenges.remove(challengerId);
				challenges.remove(challengeeId);
				addMatch(challenger, challengee, challengerId, challengeeId);
			} else if (challenges.containsKey(challengerId)) {
				ArrayList<UUID> list = challenges.get(challengerId);
				if (list.contains(challengee)) {
					challenger.sendMessage(ChatColor.RED
							+ "You have already challenged this player. ");
					challenger.sendMessage(ChatColor.RED
							+ "You must wait to challenge them again.");
				} else {
					addChallenge(challenger, challengee, challengerId,
							challengeeId);
				}
			} else {
				challenges.put(challengerId, new ArrayList<UUID>());
				addChallenge(challenger, challengee, challengerId, challengeeId);
			}
		}
	}

	private boolean canChallenge(Player challenger, Player challengee,
			UUID challengerId, UUID challengeeId) {
		boolean canChallenge = false;
		if (!(challengerId.equals(red) || challengerId.equals(blue)
				|| challengeeId.equals(red) || challengeeId.equals(blue))) {
			if (queued.contains(challengerId)) {
				challenger
						.sendMessage(ChatColor.RED
								+ "You are already queued to fight. '/arena quit' to leave the queue.");
			} else if (queued.contains(challengeeId)) {
				challenger.sendMessage(ChatColor.RED
						+ "That player is already queued to fight.");
			} else {
				canChallenge = true;
			}
		}
		return canChallenge;
	}

	private boolean isAccepting(UUID challengerId, UUID challengeeId) {
		boolean isAccepting = false;
		if (challenges.containsKey(challengeeId)) {
			if (challenges.get(challengeeId).contains(challengerId)) {
				isAccepting = true;
			}
		}
		return isAccepting;
	}

	private void addChallenge(Player challenger, Player challengee,
			UUID challengerId, UUID challengeeId) {
		challenges.get(challengerId).add(challengeeId);
		challenger.sendMessage(ChatColor.GRAY + "You have challenged "
				+ ChatColor.LIGHT_PURPLE + challengee.getName()
				+ ChatColor.GRAY + " to a duel.");
		challengee
				.sendMessage(ChatColor.LIGHT_PURPLE
						+ challenger.getName()
						+ ChatColor.GRAY
						+ " has challenged you to a duel. Right click them with a sword to accept.");
	}

	private void addMatch(Player challenger, Player challengee,
			UUID challengerId, UUID challengeeId) {
		if (active) {
			queue.addLast(new Match(challengerId, challengeeId));
			challenger.sendMessage(ChatColor.GOLD
					+ "The arena is in use. You have been added to the queue.");
			challenger.sendMessage(ChatColor.GOLD
					+ "Use '/arena quit' to leave the queue.");
			challengee.sendMessage(ChatColor.GOLD
					+ "The arena is in use. You have been added to the queue.");
			challengee.sendMessage(ChatColor.GOLD
					+ "Use '/arena quit' to leave the queue.");
		} else {
			startMatch(challenger, challengee);
		}
	}

	private void startNextMatch() {
		if (queue.size() > 0) {
			Match match = queue.getFirst();
			queue.removeFirst();
			Server server = sm.getServer();
			UUID redId = match.getRed(), blueId = match.getBlue();
			queued.remove(redId);
			queue.remove(blueId);
			Player red = server.getPlayer(redId), blue = server
					.getPlayer(blueId);

			if (red == null && blue != null) {
				blue.sendMessage(ChatColor.RED
						+ "The match was canceled because the other player left the game.");
			} else if (blue == null && red != null) {
				red.sendMessage(ChatColor.RED
						+ "The match was canceled because the other player left the game.");
			} else if (red != null && blue != null) {
				startMatch(red, blue);
			}
		}
	}

	private void startMatch(Player red, Player blue) {
		active = true;
		String redName = red.getName(), blueName = blue.getName();
		red.teleport(redSpawn);
		blue.teleport(blueSpawn);
		red.setHealth(red.getMaxHealth());
		blue.setHealth(blue.getMaxHealth());
		// Temporary solution
		Server server = sm.getServer();
		ConsoleCommandSender cs = server.getConsoleSender();
		server.dispatchCommand(cs, "disableClass " + redName);
		server.dispatchCommand(cs, "disableClass " + blueName);
		// end temp
		server.broadcastMessage(ChatColor.RED + redName + ChatColor.GOLD
				+ " and " + ChatColor.BLUE + blueName + ChatColor.GOLD
				+ " have agreed to fight in the arena. Type '/arena' to watch.");
	}

	public void endMatch(Player winner, Player loser) {
		String winnerName = winner.getName(), loserName = loser.getName();
		// Temporary solution
		Server server = sm.getServer();
		ConsoleCommandSender cs = server.getConsoleSender();
		server.dispatchCommand(cs, "enableClass " + winnerName);
		server.dispatchCommand(cs, "enableClass " + loserName);
		// end Temp
		winner.teleport(warp);
		loser.teleport(warp);
		winner.setFireTicks(0);
		loser.setFireTicks(0);
		winner.setHealth(winner.getMaxHealth());
		loser.setHealth(loser.getMaxHealth());
		server.broadcastMessage(ChatColor.GOLD + winnerName + ChatColor.GRAY
				+ " has defeated " + loserName + " in the arena.");
		server.getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				active = false;
				startNextMatch();
			}
		}, 60);
	}

	/*
	 * Reloads arenaConfig.yml
	 * 
	 * @ensure arenaConfigFile != null
	 */
	public void reloadArenaConfig() {
		if (arenaConfigFile == null) {
			arenaConfigFile = new File(sm.getDataFolder(), "arenaConfig.yml");
		}
		arenaConfig = YamlConfiguration.loadConfiguration(arenaConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = sm.getResource("arenaConfig.yml");
		if (defConfigStream != null) {
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			arenaConfig.setDefaults(defConfig);
		}
	}

	/*
	 * Reads arenaConfig.yml
	 * 
	 * @ensure returns arenaConfig file config
	 */
	public FileConfiguration getArenaConfig() {
		if (arenaConfig == null) {
			reloadArenaConfig();
		}
		return arenaConfig;
	}

	/*
	 * Writes to arenaConfig.yml
	 */
	public void saveArenaConfig() {
		if (arenaConfig == null || arenaConfigFile == null) {
			return;
		}
		try {
			getArenaConfig().save(arenaConfigFile);
		} catch (IOException ex) {
			sm.getLogger().log(Level.SEVERE,
					"Could not save config to " + arenaConfigFile, ex);
		}
	}

	private class Match {

		private UUID red, blue;

		public Match(UUID red, UUID blue) {
			this.red = red;
			this.blue = blue;
		}

		public UUID getRed() {
			return red;
		}

		public UUID getBlue() {
			return blue;
		}

		public boolean contains(UUID id) {
			boolean contains = false;
			if (red.equals(id) || blue.equals(id)) {
				contains = true;
			}
			return contains;
		}

	}
}
