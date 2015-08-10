package com.yahoo.prosfis.somnusmanager.events.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class LoveLettersEvent implements CommandExecutor, Event {

	private final SomnusManager sm;
	private HashMap<String, Integer> letterCount;
	private List<UUID> players;
	private boolean active;

	public LoveLettersEvent(SomnusManager sm) {
		this.sm = sm;
		active = false;
		sm.getCommand("love").setExecutor(this);
	}

	public void start() {
		letterCount = Maps.newHashMap();
		players = new ArrayList<UUID>();
		active = true;
		Server server = sm.getServer();
		server.broadcastMessage(ChatColor.GOLD + "Love Letters: " + ChatColor.DARK_GREEN
				+ "Send a nice message to someone you love" + "\nwith /love <player> <message>");
		server.getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				stop();
			}
		}, 20 * 60);
	}

	public void stop() {
		active = false;
		Server server = sm.getServer();
		server.broadcastMessage(ChatColor.DARK_GREEN + "The Love Letters event is over!");
		if (letterCount.isEmpty()) {
			server.broadcastMessage(ChatColor.DARK_GREEN + "No love letters were sent.");
		} else {
			server.broadcastMessage(ChatColor.DARK_GREEN + "Loved players: " + ChatColor.GOLD
					+ "Letters Received" + ChatColor.DARK_GREEN + "/Player");
			for (Entry<String, Integer> entry : letterCount.entrySet()) {
				server.broadcastMessage(ChatColor.GOLD + "" + entry.getValue() + " "
						+ ChatColor.DARK_GREEN + entry.getKey());
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (!active) {
				sender.sendMessage(ChatColor.RED + "That event is not active at this time.");
			} else {
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "\n/love <player> <message>");
				} else {
					UUID id = ((Player) sender).getUniqueId();
					if (players.contains(id)) {
						sender.sendMessage(ChatColor.RED + "You may only send one love letter.");
					} else {
						Player player = sm.getServer().getPlayer(args[0]);
						if (player == null) {
							sender.sendMessage(ChatColor.RED + "Player not found."
									+ "\n/love <player> <message>");
						} else {
							if (player.getUniqueId().equals(id)) {
								sender.sendMessage(ChatColor.RED
										+ "You can't send a love letter to yourself!");
							} else {
								players.add(id);
								String name = player.getName();
								if (!letterCount.containsKey(name)) {
									letterCount.put(name, 1);
								} else {
									letterCount.put(name, letterCount.get(name) + 1);
								}
								player.sendMessage(ChatColor.DARK_GREEN + sender.getName() + ": "
										+ ChatColor.GRAY + getMessage(args));
								sender.sendMessage(ChatColor.DARK_GREEN + "Message sent.");
							}
						}
					}
				}
			}
		} else {
			sender.sendMessage("Only players may issue this command.");
		}
		return true;
	}

	private String getMessage(String[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			sb.append(args[i] + " ");
		}
		return sb.toString();
	}

}
