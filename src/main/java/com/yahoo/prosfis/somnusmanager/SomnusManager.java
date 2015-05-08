package com.yahoo.prosfis.somnusmanager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.yahoo.prosfis.somnusmanager.arena.ArenaCommandExecutor;
import com.yahoo.prosfis.somnusmanager.arena.ArenaManager;
import com.yahoo.prosfis.somnusmanager.arena.listeners.ArenaListener;
import com.yahoo.prosfis.somnusmanager.dungeons.listeners.DungeonListener;
import com.yahoo.prosfis.somnusmanager.fireprotect.FireProtectListener;
import com.yahoo.prosfis.somnusmanager.joinprotect.BlockChangeListener;
import com.yahoo.prosfis.somnusmanager.quickwarp.QuickWarpCommandExecutor;
import com.yahoo.prosfis.somnusmanager.quickwarp.QuickWarpManager;
import com.yahoo.prosfis.somnusmanager.random.RandomCommandExecutor;
import com.yahoo.prosfis.somnusmanager.random.RandomManager;

public class SomnusManager extends JavaPlugin {

	private ArenaManager am;
	private QuickWarpManager qwm;
	private RandomManager rm;
	private Connection connection;
	private String ip, port, dbName, username, password;
	private FileConfiguration somnusPlayers = null;
	private File somnusPlayersFile = null;

	public void onEnable() {
		getLogger().info("SomnusManager is enabled.");
		init();
	}

	public void onDisable() {
		getLogger().info("SomnusManager is disabled.");
	}

	public void init() {
		this.am = new ArenaManager(this);
		this.qwm = new QuickWarpManager(this);
		this.rm = new RandomManager(this);
		assignCommands();
		registerListeners();
		openConnection();
		checkTables();
	}

	public void assignCommands() {
		ArenaCommandExecutor ace = new ArenaCommandExecutor(am);
		getCommand("arena").setExecutor(ace);
		QuickWarpCommandExecutor qwce = new QuickWarpCommandExecutor(qwm);
		getCommand("qw").setExecutor(qwce);
		getCommand("random").setExecutor(new RandomCommandExecutor(rm, this));
	}

	public void registerListeners() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ArenaListener(am), this);
		pm.registerEvents(new DungeonListener(), this);
		pm.registerEvents(new FireProtectListener(this), this);
		pm.registerEvents(new BlockChangeListener(this), this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String command = cmd.getName();
		if (command.equalsIgnoreCase("----")) {
		}
		return false;
	}

	public void openConnection() {
		if (ip == null || port == null || dbName == null || username == null || password == null) {
			FileConfiguration config = getConfig();
			ip = config.getString("DataBase.IP");
			port = config.getString("DataBase.Port");
			dbName = config.getString("DataBase.Name");
			username = config.getString("DataBase.Username");
			password = config.getString("DataBase.Password");
		}
		try {
			connection = new DBConnection(ip, port, dbName, username, password).getConnection();
		} catch (Exception e) {
			getServer().getLogger().info(e.getMessage());
		}
	}

	public void checkTables() {
		try {
			ResultSet rs = connection.createStatement().executeQuery(
					"SHOW TABLES LIKE 'sm_block_changes'");
			if (!rs.next()) {
				getConnection()
						.createStatement()
						.execute(
								"CREATE TABLE sm_block_changes (uuid VARCHAR(40), world "
										+ "VARCHAR(20), block_x INTEGER, block_y INTEGER, block_z "
										+ "INTEGER, chunk_x INTEGER, chunk_z "
										+ "INTEGER,  UNIQUE KEY unique_block (world, block_x, block_y, block_z))");
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

	/*
	 * Reloads somnusPlayers.yml
	 * 
	 * @ensure playerClassFile != null
	 */
	public void reloadSomnusPlayers() {
		if (somnusPlayersFile == null) {
			somnusPlayersFile = new File(getDataFolder(), "somnusPlayers.yml");
		}
		somnusPlayers = YamlConfiguration.loadConfiguration(somnusPlayersFile);

		// Look for defaults in the jar
		InputStream defConfigStream = getResource("somnusPlayers.yml");
		if (defConfigStream != null) {
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			somnusPlayers.setDefaults(defConfig);
		}
	}

	/*
	 * Reads somnusPlayers.yml
	 * 
	 * @ensure returns somnusPlayers file config
	 */
	public FileConfiguration getSomnusPlayers() {
		if (somnusPlayers == null) {
			reloadSomnusPlayers();
		}
		return somnusPlayers;
	}

	/*
	 * Writes to somnusPlayers.yml
	 */
	public void saveSomnusPlayers() {
		if (somnusPlayers == null || somnusPlayersFile == null) {
			return;
		}
		try {
			getSomnusPlayers().save(somnusPlayersFile);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + somnusPlayersFile, ex);
		}
	}

}
