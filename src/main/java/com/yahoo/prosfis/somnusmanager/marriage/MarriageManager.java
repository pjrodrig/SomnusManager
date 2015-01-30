package com.yahoo.prosfis.somnusmanager.marriage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.marriage.listeners.IDoListener;
import com.yahoo.prosfis.somnusmanager.marriage.listeners.LogoutListener;
import com.yahoo.prosfis.somnusmanager.marriage.listeners.MarriageChatListener;
import com.yahoo.prosfis.somnusmanager.util.ConfigUtil;

public class MarriageManager {

	private FileConfiguration marriageConfig = null;
	private File marriageConfigFile = null;

	private final Map<UUID, UUID> proposals;
	private final Map<UUID, Couple> marriages;
	private final Map<UUID, MarriageChatListener> chats;
	private final SomnusManager sm;
	private Status status;
	private Location warp, p1Loc, p2Loc, priestLoc;
	private Player p1, p2, priest;
	private LogoutListener logoutListener;
	private IDoListener iDoListener;

	public MarriageManager(SomnusManager sm) {
		this.sm = sm;
		this.marriages = Maps.newHashMap();
		this.proposals = Maps.newHashMap();
		this.chats = Maps.newHashMap();
		status = Status.OPEN;
		init();
	}

	public void init() {
		FileConfiguration config = getMarriageConfig();
		warp = ConfigUtil.loadLocation(sm.getServer(), config, "Church.Warp.");
		priestLoc = ConfigUtil.loadLocation(sm.getServer(), config,
				"Church.Priest.");
		p1Loc = ConfigUtil.loadLocation(sm.getServer(), config, "Church.P1.");
		p2Loc = ConfigUtil.loadLocation(sm.getServer(), config, "Church.P2.");
		ConfigurationSection section = config.createSection("Marriages");
		Iterator<String> iter = section.getValues(false).keySet().iterator();
		String path;
		Location loc;
		UUID p1, p2;
		Couple couple;
		while (iter.hasNext()) {
			path = "Marriages." + iter.next() + ".";
			p1 = UUID.fromString(config.getString(path + "P1"));
			p2 = UUID.fromString(config.getString(path + "P2"));
			loc = ConfigUtil.loadLocation(sm.getServer(), config, path
					+ "Home.");
			couple = new Couple(p1, p2);
			if (loc != null)
				couple.setHome(loc);
			marriages.put(p1, couple);
			marriages.put(p2, couple);
			saveMarriageConfig();
		}
	}

	public void saveCouple(Couple couple) {
		FileConfiguration config = getMarriageConfig();
		UUID p1 = couple.getPlayer1(), p2 = couple.getPlayer2();
		String path = "Marriages." + p1 + "|" + p2 + ".";
		config.set(path + "P1", p1);
		config.set(path + "P2", p2);
		if (couple.hasHome()) {
			ConfigUtil.saveLocation(config, path + "Home.", couple.getHome());
		}
		saveMarriageConfig();
	}

	public void setChurch(Player player) {
		warp = player.getLocation();
		ConfigUtil.saveLocation(getMarriageConfig(), "Church.Warp.", warp);
		saveMarriageConfig();
		player.sendMessage(ChatColor.GREEN + "Warp set");
	}

	public void setPriest(Player player) {
		priestLoc = player.getLocation();
		ConfigUtil.saveLocation(getMarriageConfig(), "Church.Priest.",
				priestLoc);
		saveMarriageConfig();
		player.sendMessage(ChatColor.GREEN + "Location set");
	}

	public void setPlayer1(Player player) {
		p1Loc = player.getLocation();
		ConfigUtil.saveLocation(getMarriageConfig(), "Church.P1.", p1Loc);
		saveMarriageConfig();
	}

	public void setPlayer2(Player player) {
		p1Loc = player.getLocation();
		ConfigUtil.saveLocation(getMarriageConfig(), "Church.P2.", p2Loc);
		saveMarriageConfig();
	}

	public void warp(Player player) {
		player.teleport(warp);
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

	private void addProposal(Player proposer, final UUID proposerId,
			Player proposee, final UUID proposeeId) {
		proposer.sendMessage(ChatColor.GRAY + "You have proposed to "
				+ ChatColor.LIGHT_PURPLE + proposee.getName() + ChatColor.GRAY
				+ ".");
		proposee.sendMessage(ChatColor.LIGHT_PURPLE + proposer.getName()
				+ ChatColor.GRAY + " has proposed to you.");
		proposals.put(proposeeId, proposerId);
		sm.getServer().getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				if (proposals.containsKey(proposeeId)
						&& proposals.get(proposeeId).equals(proposerId))
					proposals.remove(proposeeId);
			}
		}, 20 * 10);
	}

	public void acceptProposal(Player player) {
		UUID id = player.getUniqueId();
		if (proposals.containsKey(id)) {
			Player spouse = sm.getServer().getPlayer(proposals.get(id));
			if (spouse == null) {
				player.sendMessage(ChatColor.RED
						+ "That player is no longer online.");
				proposals.remove(id);
			} else {
				switch (status) {
				case OPEN:
					p1 = spouse;
					p2 = player;
					logoutListener = new LogoutListener(p1, p2, this);
					sm.getServer().getPluginManager()
							.registerEvents(logoutListener, sm);
					getPriest();
					break;
				case PLANNING:
					player.sendMessage(ChatColor.RED
							+ "Another wedding is currently being planned.");
					break;
				case ACTIVE:
					player.sendMessage(ChatColor.RED
							+ "Another wedding is taking place.");
					break;
				case COOLDOWN:
					player.sendMessage(ChatColor.RED
							+ "The church is being prepared. Please try again soon.");
					break;
				}
			}
		} else {
			player.sendMessage(ChatColor.RED
					+ "You have no active proposals at this time.");
		}
	}

	private void getPriest() {
		Server server = sm.getServer();
		server.broadcastMessage(ChatColor.LIGHT_PURPLE + p1.getName()
				+ ChatColor.GRAY + " and " + ChatColor.LIGHT_PURPLE
				+ p2.getName() + ChatColor.GRAY
				+ " have agreed to get married.");
		server.broadcastMessage(ChatColor.GRAY
				+ "Is there a priest who will marry them? Use "
				+ ChatColor.LIGHT_PURPLE + "/marry priest ");
	}

	public void addPriest(Player player) {
		if (status == Status.PLANNING) {
			logoutListener.unregister();
			startWedding();
		} else {
			player.sendMessage(ChatColor.RED
					+ "There are no pending marriages at this time.");
		}
	}

	private void startWedding() {
		status = Status.ACTIVE;
		Server server = sm.getServer();
		server.broadcastMessage(ChatColor.LIGHT_PURPLE + p1.getName()
				+ ChatColor.GRAY + " and " + ChatColor.LIGHT_PURPLE
				+ p2.getName() + ChatColor.GRAY + " are getting married. Use "
				+ ChatColor.LIGHT_PURPLE + "/church" + ChatColor.GRAY
				+ " to watch.");
		priest.teleport(priestLoc);
		p1.teleport(p1Loc);
		p2.teleport(p2Loc);
		iDoListener = new IDoListener(p1, p2, priest, priestLoc, this, sm);
		server.getPluginManager().registerEvents(iDoListener, sm);
	}

	public void cancelWedding(Player player) {
		sm.getServer().broadcastMessage(
				ChatColor.GRAY + "The wedding has been canceled because "
						+ ChatColor.LIGHT_PURPLE + player.getName()
						+ " has left the game.");
		p1 = null;
		p2 = null;
		status = Status.OPEN;
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
		saveCouple(couple);
	}

	public void setMarryHome(Player player) {
		UUID id = player.getUniqueId();
		if (marriages.containsKey(id)) {
			Couple couple = marriages.get(id);
			couple.setHome(player.getLocation());
			saveCouple(couple);
		} else
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
			if (chats.containsKey(id)) {
				chats.get(id).unregister();
				chats.remove(id);
			}
			if (chats.containsKey(partnerId)) {
				chats.get(partnerId).unregister();
				chats.remove(partnerId);
			}
			if (partner != null)
				partner.sendMessage(ChatColor.LIGHT_PURPLE + player.getName()
						+ " has divorced you.");
		} else {
			player.sendMessage(ChatColor.RED + "You are not married to anyone.");
		}
	}

	public void marryChat(Player player) {
		UUID id = player.getUniqueId();
		if (marriages.containsKey(id)) {
			if (chats.containsKey(id)) {
				chats.get(id).unregister();
				chats.remove(id);
				player.sendMessage(ChatColor.DARK_GREEN
						+ "You left Marriage Chat.");
			} else {
				Player spouse = sm.getServer().getPlayer(
						marriages.get(id).getPartner(id));
				if (spouse == null) {
					player.sendMessage(ChatColor.RED
							+ "Your partner is not online.");
				}
				chats.put(id, new MarriageChatListener(player, spouse));
				player.sendMessage(ChatColor.DARK_GREEN
						+ "You are now in Marriage Chat.");
			}
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

		public boolean hasHome() {
			boolean hasHome = false;
			if (home != null)
				hasHome = true;
			return hasHome;
		}

		public UUID getPartner(UUID id) {
			UUID partner;
			if (id.equals(p1))
				partner = p2;
			else
				partner = p1;
			return partner;
		}
	}

	private enum Status {
		OPEN, PLANNING, ACTIVE, COOLDOWN;
	}
}
