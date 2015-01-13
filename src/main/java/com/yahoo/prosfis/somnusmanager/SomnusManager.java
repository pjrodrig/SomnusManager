package com.yahoo.prosfis.somnusmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SomnusManager extends JavaPlugin {

	public void onEnable() {

	}

	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		String command = cmd.getName();
		if (command.equalsIgnoreCase("invalidMob")) {
		}
		return false;
	}

}
