package com.yahoo.prosfis.somnusmanager.quickwarp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;
import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.quickwarp.listeners.QuickWarpListener;
import com.yahoo.prosfis.somnusmanager.util.ConfigUtil;

public class QuickWarpManager {

	private final SomnusManager sm;
	private final QuickWarpListener qwListener;
	private final Map<String, QuickWarp> warps;

	private FileConfiguration quickWarps = null;
	private File quickWarpFile = null;

	public QuickWarpManager(SomnusManager sm) {
		this.sm = sm;
		this.qwListener = new QuickWarpListener();
		sm.getServer().getPluginManager().registerEvents(qwListener, sm);
		warps = Maps.newHashMap();
		init();
	}

	public void init() {
		FileConfiguration config = getQuickWarpConfig();
		ConfigurationSection section = config.getConfigurationSection("Warps");
		if (section != null) {
			Iterator<String> iter = section.getValues(false).keySet().iterator();
			String name;
			QuickWarp warp;
			while (iter.hasNext()) {
				name = iter.next();
				warp = loadWarp(name, config);
				warps.put(name, warp);
				Location destination = warp.getDestination();
				if (destination != null)
					qwListener.addWarp(warp.getLocation(), destination);
				warp.start();
			}
		}
	}

	public QuickWarp loadWarp(String name, FileConfiguration config) {
		String path = "Warps." + name + ".";
		Location loc = ConfigUtil.loadLocation(sm.getServer(), config, path + "Location."), destination = ConfigUtil
				.loadLocation(sm.getServer(), config, path + "Destination.");
		Material material = Material.getMaterial(config.getString(path + "Material"));
		int data = config.getInt(path + "Data");
		return new QuickWarp(name, loc, destination, material, data, sm);
	}

	public void create(Player player, String warp) {
		ItemStack item = player.getItemInHand();
		if (item != null) {
			Material material = item.getType();
			if (material != Material.AIR) {
				@SuppressWarnings("deprecation")
				Byte bdata = item.getData().getData();
				int data = bdata.intValue();
				Location loc = player.getLocation().getBlock().getLocation();
				Block block = loc.getBlock().getRelative(BlockFace.DOWN);
				block.setType(Material.SEA_LANTERN);
				QuickWarp quickWarp = new QuickWarp(warp, loc, material, data, sm);
				warps.put(warp, quickWarp);
				quickWarp.start();
				saveWarp(quickWarp);
				player.sendMessage(ChatColor.GREEN + "Warp created.");
			} else {
				player.sendMessage(ChatColor.RED + "You must have a block in your hand.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You must have a block in your hand.");
		}
	}

	public void set(Player player, String warp) {
		if (warps.containsKey(warp)) {
			QuickWarp qwarp = warps.get(warp);
			Location loc = player.getLocation();
			qwarp.setDestination(loc);
			qwListener.addWarp(qwarp.getLocation(), loc);
			saveWarp(qwarp);
			player.sendMessage(ChatColor.GREEN + "Destination set.");
		} else {
			player.sendMessage(ChatColor.RED + "That warp does not exist.");
		}
	}

	public void remove(Player player, String warp) {
		if (warps.containsKey(warp)) {
			QuickWarp qwarp = warps.get(warp);
			qwarp.stop();
			qwListener.removeWarp(qwarp.getLocation());
			warps.remove(warp);
			getQuickWarpConfig().set("Warps." + warp, null);
			saveQuickWarpConfig();
			player.sendMessage(ChatColor.GREEN + "Warp removed.");
		}
	}

	public void saveWarp(QuickWarp warp) {
		FileConfiguration config = getQuickWarpConfig();
		String path = "Warps." + warp.getName() + ".";
		ConfigUtil.saveLocation(config, path + "Location.", warp.getLocation());
		Location destination = warp.getDestination();
		if (destination != null)
			ConfigUtil.saveLocation(config, path + "Destination.", destination);
		config.set(path + "Material", warp.getMaterial().name());
		config.set(path + "Data", warp.getData());
		saveQuickWarpConfig();
	}

	public void printList(Player player) {
		String names = "", current;
		Iterator<String> iter = warps.keySet().iterator();
		boolean first = true;
		while (iter.hasNext()) {
			current = iter.next();
			if (first)
				first = false;
			else
				names = names + ", ";
			names = names + current;
		}
		player.sendMessage(names);
	}

	/*
	 * Reloads quickWarps.yml
	 * 
	 * @ensure quickWarpFile != null
	 */
	public void reloadQuickWarpConfig() {
		if (quickWarpFile == null) {
			quickWarpFile = new File(sm.getDataFolder(), "quickWarps.yml");
		}
		quickWarps = YamlConfiguration.loadConfiguration(quickWarpFile);

		// Look for defaults in the jar
		InputStream defConfigStream = sm.getResource("quickWarps.yml");
		if (defConfigStream != null) {
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			quickWarps.setDefaults(defConfig);
		}
	}

	/*
	 * Reads quickWarps.yml
	 * 
	 * @ensure returns quickWarps file config
	 */
	public FileConfiguration getQuickWarpConfig() {
		if (quickWarps == null) {
			reloadQuickWarpConfig();
		}
		return quickWarps;
	}

	/*
	 * Writes to quickWarps.yml
	 */
	public void saveQuickWarpConfig() {
		if (quickWarps == null || quickWarpFile == null) {
			return;
		}
		try {
			getQuickWarpConfig().save(quickWarpFile);
		} catch (IOException ex) {
			sm.getLogger().log(Level.SEVERE, "Could not save config to " + quickWarpFile, ex);
		}
	}
}
