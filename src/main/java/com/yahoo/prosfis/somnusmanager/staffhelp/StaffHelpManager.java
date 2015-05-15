package com.yahoo.prosfis.somnusmanager.staffhelp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class StaffHelpManager {

	private final List<UUID> hiddenStaff;
	private final SomnusManager sm;

	private FileConfiguration staff = null;
	private File staffFile = null;

	public StaffHelpManager(SomnusManager sm) {
		this.sm = sm;
		hiddenStaff = new ArrayList<UUID>();
		init();
	}

	private void init() {
		FileConfiguration config = getStaffConfig();
		for (String id : config.getStringList("Hidden")) {
			hiddenStaff.add(UUID.fromString(id));
		}
	}

	public void showStaff(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Online Staff:");
		if (sender.hasPermission("SomnusManager.staff")) {
			for (Player player : sm.getServer().getOnlinePlayers()) {
				if (player.hasPermission("SomnusManager.staff")) {
					if (hiddenStaff.contains(player.getUniqueId())) {
						sender.sendMessage(ChatColor.GRAY + player.getName());
					} else {
						sender.sendMessage(ChatColor.AQUA + player.getName());
					}
				}
			}
		} else {
			for (Player player : sm.getServer().getOnlinePlayers()) {
				if (player.hasPermission("SomnusManager.staff")
						&& !hiddenStaff.contains(player.getUniqueId())) {
					sender.sendMessage(ChatColor.AQUA + player.getName());
				}
			}
		}
	}

	public void toggleHidden(Player player) {
		UUID id = player.getUniqueId();
		if (hiddenStaff.contains(id)) {
			hiddenStaff.remove(id);
		} else {
			hiddenStaff.add(id);
		}
		saveHiddenStaff();
	}

	public void sendStaffMessage(Player sender, String message) {
		sender.sendMessage(ChatColor.RED + "[you -> staff] " + ChatColor.GRAY + message);
		for (Player player : sm.getServer().getOnlinePlayers()) {
			if (player.hasPermission("SomnusManager.staff")) {
				BukkitScheduler scheduler = sm.getServer().getScheduler();
				player.playSound(player.getLocation(), Sound.CLICK, 4, 3);
				final Player fplayer = player;
				scheduler.runTaskLater(sm, new Runnable() {
					public void run() {
						fplayer.playSound(fplayer.getLocation(), Sound.CLICK, 4, 5);
					}
				}, 2);
				scheduler.runTaskLater(sm, new Runnable() {
					public void run() {
						fplayer.playSound(fplayer.getLocation(), Sound.CLICK, 4, 1);
					}
				}, 4);
				player.sendMessage(ChatColor.RED + "[STAFF HELP] " + ChatColor.GRAY + sender.getName()
						+ ": " + message);
			}
		}
	}

	private void saveHiddenStaff() {
		FileConfiguration config = getStaffConfig();
		config.set("Hidden", hiddenStaff);
	}

	/*
	 * Reloads staff.yml
	 * 
	 * @ensure staffFile != null
	 */
	public void reloadStaffConfig() {
		if (staffFile == null) {
			staffFile = new File(sm.getDataFolder(), "staff.yml");
		}
		staff = YamlConfiguration.loadConfiguration(staffFile);

		// Look for defaults in the jar
		InputStream defConfigStream = sm.getResource("staff.yml");
		if (defConfigStream != null) {
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			staff.setDefaults(defConfig);
		}
	}

	/*
	 * Reads staff.yml
	 * 
	 * @ensure returns staff file config
	 */
	public FileConfiguration getStaffConfig() {
		if (staff == null) {
			reloadStaffConfig();
		}
		return staff;
	}

	/*
	 * Writes to staff.yml
	 */
	public void saveStaffConfig() {
		if (staff == null || staffFile == null) {
			return;
		}
		try {
			getStaffConfig().save(staffFile);
		} catch (IOException ex) {
			sm.getLogger().log(Level.SEVERE, "Could not save config to " + staffFile, ex);
		}
	}
}
