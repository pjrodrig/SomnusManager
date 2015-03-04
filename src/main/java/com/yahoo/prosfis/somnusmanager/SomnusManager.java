package com.yahoo.prosfis.somnusmanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.yahoo.prosfis.somnusmanager.arena.ArenaCommandExecutor;
import com.yahoo.prosfis.somnusmanager.arena.ArenaManager;
import com.yahoo.prosfis.somnusmanager.arena.listeners.ArenaListener;
import com.yahoo.prosfis.somnusmanager.dungeons.listeners.DungeonListener;
import com.yahoo.prosfis.somnusmanager.fireprotect.FireProtectListener;

public class SomnusManager extends JavaPlugin {

	private ArenaManager am;
	private Connection connection;
	private String ip, port, dbName, username, password;

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
		openConnection();
		checkTables();
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

	public void openConnection() {
		if (ip == null || port == null || dbName == null || username == null
				|| password == null) {
			FileConfiguration config = getConfig();
			ip = config.getString("DataBase.IP");
			port = config.getString("DataBase.Port");
			dbName = config.getString("DataBase.Name");
			username = config.getString("DataBase.Username");
			password = config.getString("DataBase.Password");
		}
		try {
			connection = new DBConnection(ip, port, dbName, username, password)
					.getConnection();
		} catch (Exception e) {
			getServer().getLogger().info(e.getMessage());
		}
	}

	public void checkTables() {
		try {
			ResultSet rs = connection.createStatement().executeQuery(
					"SHOW TABLES LIKE 'block_changes'");
			if (!rs.next()) {
				getConnection().createStatement().execute(
						"CREATE TABLE block_changes (uuid VARCHAR(40), world "
								+ "VARCHAR(20), x INTEGER, y INTEGER, z "
								+ "INTEGER)");
			}
		} catch (SQLException e) {
			getLogger().warning(e.getMessage());
		}
	}

	/*
	 * Returns connection to the SomnusRealms database
	 */
	public Connection getConnection() {
		try {
			if (connection.isClosed())
				openConnection();
		} catch (Exception e) {
			getServer().getLogger().info(e.getMessage());
		}
		return connection;
	}

}
