package com.yahoo.prosfis.somnusmanager.arena;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Pair;
import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class ArenaManager {

	private final SomnusManager sm;
	private Location warp, redSpawn, blueSpawn;
	private FileConfiguration arenaConfig = null;
	private File arenaConfigFile = null;
	private final Map<UUID, UUID> challenges;
	private final LinkedList<Pair<UUID, UUID>> queue;
	private final LinkedList<UUID> queued;
	private boolean active;

	public ArenaManager(SomnusManager sm) {
		this.sm = sm;
		this.challenges = Maps.newHashMap();
		this.queue = new LinkedList<Pair<UUID, UUID>>();
		this.queued = new LinkedList<UUID>();
		active = false;
	}

	public void setWarp(Location warp) {
		this.warp = warp;
		FileConfiguration config = getArenaConfig();
		String path = "Warp.";
		config.set(path + "Set", true);
		config.set(path + "World", warp.getWorld().getName());
		config.set(path + "X", warp.getX());
		config.set(path + "Y", warp.getY());
		config.set(path + "Z", warp.getZ());
		config.set(path + "Yaw", warp.getYaw());
		config.set(path + "Pitch", warp.getPitch());
		saveArenaConfig();
	}

	public void setRedSpawn(Location redSpawn) {
		this.redSpawn = redSpawn;
		FileConfiguration config = getArenaConfig();
		String path = "Spawn.Red.";
		config.set(path + "Set", true);
		config.set(path + "World", warp.getWorld().getName());
		config.set(path + "X", warp.getX());
		config.set(path + "Y", warp.getY());
		config.set(path + "Z", warp.getZ());
		config.set(path + "Yaw", warp.getYaw());
		config.set(path + "Pitch", warp.getPitch());
		saveArenaConfig();
	}

	public void setBlueSpawn(Location blueSpawn) {
		this.blueSpawn = blueSpawn;
		FileConfiguration config = getArenaConfig();
		String path = "Spawn.Blue.";
		config.set(path + "Set", true);
		config.set(path + "World", warp.getWorld().getName());
		config.set(path + "X", warp.getX());
		config.set(path + "Y", warp.getY());
		config.set(path + "Z", warp.getZ());
		config.set(path + "Yaw", warp.getYaw());
		config.set(path + "Pitch", warp.getPitch());
		saveArenaConfig();
	}

	public void warp(Player player) {
		player.teleport(warp);
	}

	public void challenge(Player challenger, Player challengee) {
		
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
}
