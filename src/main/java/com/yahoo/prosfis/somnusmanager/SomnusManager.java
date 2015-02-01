package com.yahoo.prosfis.somnusmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.yahoo.prosfis.somnusmanager.arena.ArenaCommandExecutor;
import com.yahoo.prosfis.somnusmanager.arena.ArenaManager;
import com.yahoo.prosfis.somnusmanager.arena.listeners.ArenaListener;
import com.yahoo.prosfis.somnusmanager.marriage.MarriageCommandExecutor;
import com.yahoo.prosfis.somnusmanager.marriage.MarriageManager;

public class SomnusManager extends JavaPlugin {

	private ArenaManager am;
	private MarriageManager mm;

	public void onEnable() {
		getLogger().info("SomnusManager is enabled.");
		init();
	}

	public void onDisable() {
		getLogger().info("SomnusManager is disabled.");
	}

	public void init() {
		this.am = new ArenaManager(this);
		this.mm = new MarriageManager(this);
		assignCommands();
		registerListeners();
	}

	public void assignCommands() {
		ArenaCommandExecutor ace = new ArenaCommandExecutor(am);
		getCommand("arena").setExecutor(ace);
		MarriageCommandExecutor mce = new MarriageCommandExecutor(mm, this);
		getCommand("marry").setExecutor(mce);
		getCommand("church").setExecutor(mce);
		getCommand("propose").setExecutor(mce);
		getCommand("divorce").setExecutor(mce);
		getCommand("addpriest").setExecutor(mce);
	}

	public void registerListeners() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ArenaListener(am), this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		String command = cmd.getName();
		if (command.equalsIgnoreCase("----")) {
		}
		return false;
	}

}
