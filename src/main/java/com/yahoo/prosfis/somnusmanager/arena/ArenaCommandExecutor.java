package com.yahoo.prosfis.somnusmanager.arena;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommandExecutor implements CommandExecutor {

	private final ArenaManager am;

	public ArenaCommandExecutor(ArenaManager am) {
		this.am = am;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {
				am.warp((Player) sender);
			} else {
				help(sender);
			}
			return true;
		} else if (args[0].equalsIgnoreCase("help")
				|| args[0].equalsIgnoreCase("?")) {
			help(sender);
			return true;
		} else if (args[0].equalsIgnoreCase("set")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.hasPermission("SomnusManager.admin")) {
					if (args[1].equalsIgnoreCase("warp")) {
						if (args.length == 2) {
							am.setWarp(player.getLocation());
							player.sendMessage(ChatColor.GREEN
									+ "Arena warp set.");
							return true;
						}
					} else if (args[1].equalsIgnoreCase("spawn")) {
						if (args.length == 3) {
							if (args[2].equalsIgnoreCase("red")) {
								am.setRedSpawn(player.getLocation());
								player.sendMessage(ChatColor.GREEN
										+ "Red spawn set.");
								return true;
							} else if (args[2].equalsIgnoreCase("blue")) {
								am.setBlueSpawn(player.getLocation());
								player.sendMessage(ChatColor.GREEN
										+ "Blue spawn set.");
								return true;
							}
						}
					}
				} else {
					player.sendMessage(ChatColor.RED
							+ "You do not have permission to use this command.");
					return true;
				}
			} else {
				sender.sendMessage("Only players may issue this command.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("quit")) {
			if (sender instanceof Player) {
				am.quit((Player) sender, true);
			} else {
				sender.sendMessage("Only players may issue this command.");
			}
		} else if (args[0].equalsIgnoreCase("end")) {
			if (!(sender instanceof Player)
					|| ((Player) sender).hasPermission("SomnusManager.admin")) {
				am.forceEnd();
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to use this command.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("reset")) {
			if (!(sender instanceof Player)
					|| ((Player) sender).hasPermission("SomnusManager.admin")) {
				am.reset();
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to use this command.");
				return true;
			}
		}
		sender.sendMessage(ChatColor.RED + "/arena help");
		return true;
	}

	private void help(CommandSender sender) {
		sender.sendMessage(new String[] {
				ChatColor.GOLD + "Arena help menu:",
				ChatColor.AQUA + "Right click a player with a sword to "
						+ "challenge them, or accept a challenge.",
				ChatColor.AQUA + "/arena: " + ChatColor.GRAY + "warp to arena",
				ChatColor.AQUA + "/arena quit: " + ChatColor.GRAY
						+ "leave duel" });
		if (!(sender instanceof Player)
				|| ((Player) sender).hasPermission("SomnusManager.admin")) {
			sender.sendMessage(new String[] {
					ChatColor.GOLD + "Arena admin commands:",
					ChatColor.AQUA + "/arena set warp: " + ChatColor.GRAY
							+ "set location for warp",
					ChatColor.AQUA + "/arena set <redSpawn/blueSpawn>: "
							+ ChatColor.GRAY + "set "
							+ "location for player spawn",
					ChatColor.AQUA + "/arena reset: " + ChatColor.GRAY
							+ "end any matches in progress" });
		}
	}
}
