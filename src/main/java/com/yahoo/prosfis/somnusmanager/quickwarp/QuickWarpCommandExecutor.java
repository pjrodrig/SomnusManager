package com.yahoo.prosfis.somnusmanager.quickwarp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuickWarpCommandExecutor implements CommandExecutor {

	private final QuickWarpManager qwm;

	public QuickWarpCommandExecutor(QuickWarpManager qwm) {
		this.qwm = qwm;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 0 || (args.length == 1 && args[0].equals("help"))) {
				help(sender);
			} else
				switch (args[0]) {
				case "create":
					if (args.length == 2) {
						qwm.create((Player) sender, args[1]);
					} else {
						sender.sendMessage(ChatColor.RED + "/qw help");
					}
					break;
				case "set":
					if (args.length == 2) {
						qwm.set((Player) sender, args[1]);
					} else {
						sender.sendMessage(ChatColor.RED + "/qw help");
					}
					break;
				case "remove":
					if (args.length == 2) {
						qwm.remove((Player) sender, args[1]);
					} else {
						sender.sendMessage(ChatColor.RED + "/qw help");
					}
					break;
				case "list":
					if (args.length == 1) {
						qwm.printList((Player) sender);
					} else {
						sender.sendMessage(ChatColor.RED + "/qw help");
					}
					break;
				default:
					sender.sendMessage(ChatColor.RED + "/qw help");
					break;
				}
		} else {
			sender.sendMessage("Only players may issue this command.");
		}
		return true;
	}

	private void help(CommandSender sender) {
		sender.sendMessage(new String[] { ChatColor.GOLD + "Quick warp help menu:" });
		if (!(sender instanceof Player) || ((Player) sender).hasPermission("SomnusManager.admin")) {
			sender.sendMessage(new String[] {
					ChatColor.GOLD + "Warp admin commands:",
					ChatColor.AQUA + "/qw create <warp>: " + ChatColor.GRAY
							+ "create a warp portal",
					ChatColor.AQUA + "/qw set <warp>: " + ChatColor.GRAY + "set a warp destination",
					ChatColor.AQUA + "/qw remove <warp>: " + ChatColor.GRAY + "remove a warp",
					ChatColor.AQUA + "/qw list: " + ChatColor.GRAY + "list all warps" });
		}
	}
}
