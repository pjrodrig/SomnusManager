package com.yahoo.prosfis.somnusmanager.random;

import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.google.common.collect.Maps;
import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class RandomManager {

	private final SomnusManager sm;
	private final Map<UUID, WorldData> worlds;

	public RandomManager(SomnusManager sm) {
		this.sm = sm;
		this.worlds = Maps.newHashMap();
		init();
	}

	private void init() {
		FileConfiguration config = sm.getConfig();
		ConfigurationSection cs = config.getConfigurationSection("Random");
		if (cs != null) {
			Server server = sm.getServer();
			World current;
			for (String key : cs.getKeys(false)) {
				current = server.getWorld(key);
				if (current != null) {
					worlds.put(
							current.getUID(),
							new WorldData(cs.getInt(key + ".XMin"), cs.getInt(key + ".XMax"), cs
									.getInt(key + ".ZMin"), cs.getInt(key + ".ZMax")));
				}
			}
		}
	}

	public void teleport(Player player) {
		World world = player.getWorld();
		UUID id = world.getUID();
		if (worlds.containsKey(id)) {
			WorldData data = worlds.get(id);
			int xMin = data.getXMin(), xMax = data.getXMax(), zMin = data.getZMin(), zMax = data
					.getZMax(), xDiff = xMax - xMin, zDiff = zMax - zMin, x, z;
			Location loc, landing = null;
			Biome biome;
			while (landing == null) {
				x = ((int) (Math.random() * xDiff)) + xMin;
				z = ((int) (Math.random() * zDiff)) + zMin;
				biome = world.getBiome(x, z);
				if (!(biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN)) {
					loc = new Location(world, x, 256, z, 0, 45);
					landing = getGround(loc);
				}
			}
			loc = player.getLocation();
			landing.setYaw(loc.getYaw());
			landing.setPitch(loc.getPitch());
			player.teleport(landing);
			player.sendMessage(ChatColor.AQUA + "Teleport successful");
		} else {
			player.sendMessage(ChatColor.RED + "/random is not allowed in your current world.");
		}
	}

	private Location getGround(Location loc) {
		BlockIterator iter = new BlockIterator(loc, 256);
		Block current;
		Location landing = null;
		while (landing == null && iter.hasNext()) {
			current = iter.next();
			if (current.getType().isSolid()) {
				landing = current.getLocation().add(.5, 1, .5);
			} else if (current.isLiquid()) {
				break;
			}
		}
		return landing;
	}

	public void setXMin(CommandSender sender, World world, int xMin) {
		UUID id = world.getUID();
		WorldData data;
		if (!worlds.containsKey(id)) {
			data = new WorldData(xMin, xMin, 0, 0);
			worlds.put(id, data);
		} else {
			data = worlds.get(id);
			data.setXMin(xMin);
		}
		saveWorldData(world, data);
		sender.sendMessage(ChatColor.GREEN + "XMin set.");
	}

	public void setXMax(CommandSender sender, World world, int xMax) {
		UUID id = world.getUID();
		WorldData data;
		if (!worlds.containsKey(id)) {
			data = new WorldData(xMax, xMax, 0, 0);
			worlds.put(id, data);
		} else {
			data = worlds.get(id);
			data.setXMax(xMax);
		}
		saveWorldData(world, data);
		sender.sendMessage(ChatColor.GREEN + "XMax set.");
	}

	public void setZMin(CommandSender sender, World world, int zMin) {
		UUID id = world.getUID();
		WorldData data;
		if (!worlds.containsKey(id)) {
			data = new WorldData(0, 0, zMin, zMin);
			worlds.put(id, data);
		} else {
			data = worlds.get(id);
			data.setZMin(zMin);
		}
		saveWorldData(world, data);
		sender.sendMessage(ChatColor.GREEN + "ZMin set.");
	}

	public void setZMax(CommandSender sender, World world, int zMax) {
		UUID id = world.getUID();
		WorldData data;
		if (!worlds.containsKey(id)) {
			data = new WorldData(0, 0, zMax, zMax);
			worlds.put(id, data);
		} else {
			data = worlds.get(id);
			data.setZMax(zMax);
		}
		saveWorldData(world, data);
		sender.sendMessage(ChatColor.GREEN + "ZMax set.");
	}

	public void removeWorld(CommandSender sender, World world) {
		UUID id = world.getUID();
		worlds.remove(id);
		sm.getConfig().set("Random." + world.getName(), null);
		sender.sendMessage(ChatColor.GREEN + "World removed.");
	}

	private void saveWorldData(World world, WorldData data) {
		FileConfiguration config = sm.getConfig();
		String path = "Random." + world.getName() + ".";
		config.set(path + "XMin", data.getXMin());
		config.set(path + "XMax", data.getXMax());
		config.set(path + "ZMin", data.getZMin());
		config.set(path + "ZMax", data.getZMax());
		sm.saveConfig();
	}

	private class WorldData {
		private int xMin, zMin, xMax, zMax;

		public WorldData(int xMin, int xMax, int zMin, int zMax) {
			this.xMin = xMin;
			this.xMax = xMax;
			this.zMin = zMin;
			this.zMax = zMax;
		}

		public int getXMin() {
			return xMin;
		}

		public void setXMin(int xMin) {
			this.xMin = xMin;
		}

		public int getZMin() {
			return zMin;
		}

		public void setZMin(int zMin) {
			this.zMin = zMin;
		}

		public int getXMax() {
			return xMax;
		}

		public void setXMax(int xMax) {
			this.xMax = xMax;
		}

		public int getZMax() {
			return zMax;
		}

		public void setZMax(int zMax) {
			this.zMax = zMax;
		}
	}
}
