package com.yahoo.prosfis.somnusmanager.util;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtil {

	public static void saveLocation(FileConfiguration config, String path,
			Location loc) {
		config.set(path + "Set", true);
		config.set(path + "World", loc.getWorld().getName());
		config.set(path + "X", loc.getX());
		config.set(path + "Y", loc.getY());
		config.set(path + "Z", loc.getZ());
		config.set(path + "Yaw", loc.getYaw());
		config.set(path + "Pitch", loc.getPitch());
	}

}
