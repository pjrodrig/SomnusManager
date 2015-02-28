package com.yahoo.prosfis.somnusmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.yahoo.prosfis.somnusmanager.arena.ArenaCommandExecutor;
import com.yahoo.prosfis.somnusmanager.arena.ArenaManager;
import com.yahoo.prosfis.somnusmanager.arena.listeners.ArenaListener;
import com.yahoo.prosfis.somnusmanager.dungeons.listeners.DungeonListener;
import com.yahoo.prosfis.somnusmanager.fireprotect.FireProtectListener;

public class SomnusManager extends JavaPlugin {

	private ArenaManager am;

	public void onEnable() {
		getLogger().info("SomnusManager is enabled.");
		init();
	}

	public void onDisable() {
		getLogger().info("SomnusManager is disabled.");
	}

	public void init() {
		this.am = new ArenaManager(this);
		assignCommands();
		registerListeners();
	}

	public void assignCommands() {
		ArenaCommandExecutor ace = new ArenaCommandExecutor(am);
		getCommand("arena").setExecutor(ace);
	}

	public void registerListeners() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ArenaListener(am), this);
		pm.registerEvents(new DungeonListener(), this);
		pm.registerEvents(new FireProtectListener(this), this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		String command = cmd.getName();
		if (command.equalsIgnoreCase("----")) {
		}
		return false;
	}

}
