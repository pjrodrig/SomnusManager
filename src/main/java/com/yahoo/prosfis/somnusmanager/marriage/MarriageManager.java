package com.yahoo.prosfis.somnusmanager.marriage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class MarriageManager {

	private FileConfiguration marriageConfig = null;
	private File marriageConfigFile = null;

	private final Map<UUID, UUID> proposals;
	private final Map<UUID, Couple> marriages;
	private final SomnusManager sm;

	public MarriageManager(SomnusManager sm) {
		this.sm = sm;
		this.marriages = Maps.newHashMap();
		this.proposals = Maps.newHashMap();
	}

	public void propose(Player proposer, Player proposee) {
		UUID proposerId = proposer.getUniqueId(), proposeeId = proposee
				.getUniqueId();
		if (marriages.containsKey(proposerId)) {
			proposer.sendMessage(ChatColor.RED
					+ "You are already married. Use '/divorce' to divorce.");
		} else if (marriages.containsKey(proposeeId)) {
			proposer.sendMessage(ChatColor.RED
					+ "That player is already married.");
		} else {
			if (proposals.containsKey(proposeeId)) {
				if (proposals.get(proposeeId).equals(proposerId)) {
					proposer.sendMessage(ChatColor.RED
							+ "You have already proposed to this player.");
					proposer.sendMessage(ChatColor.RED
							+ "You must wait to propose again.");
				}
			} else {
				addProposal(proposer, proposerId, proposee, proposeeId);
			}
		}
	}

	private void addProposal(Player proposer, final UUID proposerId, Player proposee,
			final UUID proposeeId) {
		proposer.sendMessage(ChatColor.GRAY + "You have proposed to "
				+ ChatColor.LIGHT_PURPLE + proposee.getName() + ChatColor.GRAY + ".");
		proposee.sendMessage(ChatColor.LIGHT_PURPLE + proposer.getName() + ChatColor.GRAY + " has proposed to you.");
		proposals.put(proposeeId, proposerId);
		sm.getServer().getScheduler().runTaskLater(sm, new Runnable(){
			public void run(){
				if(proposals.containsKey(proposeeId) && proposals.get(proposeeId).equals(proposerId))
					proposals.remove(proposeeId);
			}
		}, 20 * 10);
	}

	public void marry(Player p1, Player p2) {
		UUID p1Id = p1.getUniqueId(), p2Id = p2.getUniqueId();
		Couple couple = new Couple(p1Id, p2Id);
		marriages.put(p1Id, couple);
		marriages.put(p2Id, couple);
		sm.getServer().broadcastMessage(
				ChatColor.LIGHT_PURPLE + p1.getName() + ChatColor.GRAY
						+ " and " + ChatColor.LIGHT_PURPLE + p2.getName()
						+ ChatColor.GRAY + " are now married!");
	}

	public void setMarryHome(Player player) {
		UUID id = player.getUniqueId();
		if (marriages.containsKey(id))
			marriages.get(id).setHome(player.getLocation());
		else
			player.sendMessage(ChatColor.RED + "You are not married to anyone.");
	}

	public void marryHome(Player player) {
		UUID id = player.getUniqueId();
		if (marriages.containsKey(id)) {
			Location home = marriages.get(id).getHome();
			if (home != null) {
				player.teleport(home);
			} else {
				player.sendMessage(ChatColor.RED
						+ "You have not set a home yet. Use '/marry sethome'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You are not married to anyone.");
		}
	}

	public void marryHome(UUID id, Player player) {
		if (marriages.containsKey(id)) {
			Location home = marriages.get(id).getHome();
			if (home != null) {
				player.teleport(home);
			} else {
				player.sendMessage(ChatColor.RED
						+ "That player has not set a marry home yet.");
			}
		} else {
			player.sendMessage(ChatColor.RED
					+ "That player is not married to anyone.");
		}
	}

	public void divorce(Player player) {
		UUID id = player.getUniqueId(), partnerId;
		if (marriages.containsKey(id)) {
			Couple couple = marriages.get(id);
			marriages.remove(id);
			partnerId = couple.getPartner(id);
			marriages.remove(partnerId);
			Player partner = sm.getServer().getPlayer(partnerId);
			if (partner != null)
				partner.sendMessage(ChatColor.LIGHT_PURPLE + player.getName()
						+ " has divorced you.");
		} else {
			player.sendMessage(ChatColor.RED + "You are not married to anyone.");
		}
	}

	/*
	 * Reloads marriageConfig.yml
	 * 
	 * @ensure marriageConfigFile != null
	 */
	public void reloadMarriageConfig() {
		if (marriageConfigFile == null) {
			marriageConfigFile = new File(sm.getDataFolder(),
					"marriageConfig.yml");
		}
		marriageConfig = YamlConfiguration
				.loadConfiguration(marriageConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = sm.getResource("marriageConfig.yml");
		if (defConfigStream != null) {
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			marriageConfig.setDefaults(defConfig);
		}
	}

	/*
	 * Reads marriageConfig.yml
	 * 
	 * @ensure returns marriageConfig file config
	 */
	public FileConfiguration getMarriageConfig() {
		if (marriageConfig == null) {
			reloadMarriageConfig();
		}
		return marriageConfig;
	}

	/*
	 * Writes to marriageConfig.yml
	 */
	public void saveMarriageConfig() {
		if (marriageConfig == null || marriageConfigFile == null) {
			return;
		}
		try {
			getMarriageConfig().save(marriageConfigFile);
		} catch (IOException ex) {
			sm.getLogger().log(Level.SEVERE,
					"Could not save config to " + marriageConfigFile, ex);
		}
	}

	private class Couple {

		private UUID p1, p2;
		private Location home;

		public Couple(UUID p1, UUID p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		public UUID getPlayer1() {
			return p1;
		}

		public UUID getPlayer2() {
			return p2;
		}

		public void setHome(Location home) {
			this.home = home;
		}

		public Location getHome() {
			return home;
		}

		public UUID getPartner(UUID id) {
			UUID partner;
			if (id.equals(p1))
				partner = p2;
			else
				partner = p1;
			return partner;
		}

		public boolean contains(UUID id) {
			boolean contains = false;
			if (p1.equals(id) || p2.equals(id))
				contains = true;
			return contains;
		}
	}
}
