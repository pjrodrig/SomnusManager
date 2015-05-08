package com.yahoo.prosfis.somnusmanager.random;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class RandomCommandExecutor implements CommandExecutor {

	private final RandomManager rm;
	private final SomnusManager sm;

	public RandomCommandExecutor(RandomManager rm, SomnusManager sm) {
		this.rm = rm;
		this.sm = sm;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					rm.teleport((Player) sender);
				} else {
					sender.sendMessage("Only players may issue this command. Use /random help for help");
				}
			} else
				switch (args[0]) {
				case "help":
					if (args.length == 1) {
						help(sender);
					}
					break;
				case "set":
					if (args.length == 4) {
						try {
							int value = Integer.parseInt(args[3]);
							World world = sm.getServer().getWorld(args[2]);
							if (world != null) {
								switch (args[1]) {
								case "xMin":
									rm.setXMin(sender, world, value);
									break;
								case "xMax":
									rm.setXMax(sender, world, value);
									break;
								case "zMin":
									rm.setZMin(sender, world, value);
									break;
								case "zMax":
									rm.setZMax(sender, world, value);
									break;
								default:
									sender.sendMessage(ChatColor.RED + "/random help");
									break;
								}
							} else {
								sender.sendMessage(ChatColor.RED + args[2]
										+ " is not a valid world.");
							}
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "/random help");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "/random help");
					}
					break;
				case "remove":
					if (args.length == 2) {
						World world = sm.getServer().getWorld(args[1]);
						if (world != null) {
							rm.removeWorld(sender, world);
						} else {
							sender.sendMessage(ChatColor.RED + args[1] + " is not a valid world.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "/random help");
					}
					break;
				default:
					sender.sendMessage(ChatColor.RED + "/random help");
					break;
				}
		} else {
			sender.sendMessage("Only players may issue this command.");
		}
		return true;
	}

	private void help(CommandSender sender) {
		sender.sendMessage(new String[] { ChatColor.GOLD + "Random help menu:" });
		sender.sendMessage(ChatColor.AQUA + "/random" + ChatColor.GRAY
				+ "Warps you to a random location in your current world");
		if (!(sender instanceof Player) || ((Player) sender).hasPermission("SomnusManager.admin")) {
			sender.sendMessage(new String[] {
					ChatColor.GOLD + "Random admin commands:",
					ChatColor.AQUA + "/random set <xMin/xMax/zMin/zMax> <world>: " + ChatColor.GRAY
							+ "set a world x/z min/max values",
					ChatColor.AQUA + "/random remove <world>: " + ChatColor.GRAY
							+ "remove a world from /random" });
		}
	}
}
