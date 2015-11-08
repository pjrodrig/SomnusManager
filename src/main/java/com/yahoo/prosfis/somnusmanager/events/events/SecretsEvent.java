package com.yahoo.prosfis.somnusmanager.events.events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class SecretsEvent implements CommandExecutor, Event {

	private final SomnusManager sm;
	private List<String> secrets;
	private List<UUID> players;
	private boolean active;

	public SecretsEvent(SomnusManager sm) {
		this.sm = sm;
		active = false;
		sm.getCommand("secret").setExecutor(this);
	}

	public void start() {
		secrets = new LinkedList<String>();
		players = new ArrayList<UUID>();
		active = true;
		Server server = sm.getServer();
		server.broadcastMessage(ChatColor.GOLD + "Secrets: " + ChatColor.DARK_GREEN + "Anonymously share a secret with "
				+ ChatColor.GOLD + "/secret <message>");
		server.getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				stop();
			}
		}, 20 * 60);
	}

	public void stop() {
		active = false;
		final Server server = sm.getServer();
		server.broadcastMessage(ChatColor.DARK_GREEN + "Secrets will now be broadcasted!");
		BukkitScheduler scheduler = server.getScheduler();
		int count = 1;
		for (String message : secrets) {
			final String finalMessage = message;
			scheduler.runTaskLater(sm, new Runnable() {
				public void run() {
					server.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Secret: " + finalMessage);
				}
			}, 20 * 10 * count);
			count++;
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (!active) {
				sender.sendMessage(ChatColor.RED + "That event is not active at this time.");
			} else {
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "\n/secret <message>");
				} else {
					UUID id = ((Player) sender).getUniqueId();
					if (players.contains(id)) {
						sender.sendMessage(ChatColor.RED + "You may only submit one secret.");
					} else {
						players.add(id);
						secrets.add(getMessage(args));
						sender.sendMessage(ChatColor.DARK_GREEN + "Secret added.");
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
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i] + " ");
		}
		return sb.toString();
	}

}
