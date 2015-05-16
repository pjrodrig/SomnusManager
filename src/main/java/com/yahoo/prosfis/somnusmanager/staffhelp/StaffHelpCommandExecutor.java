package com.yahoo.prosfis.somnusmanager.staffhelp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffHelpCommandExecutor implements CommandExecutor {

	private final StaffHelpManager shm;

	public StaffHelpCommandExecutor(StaffHelpManager shm) {
		this.shm = shm;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String com = cmd.getName();
		if (com.equalsIgnoreCase("staff")) {
			if (args.length == 0) {
				shm.showStaff(sender);
			} else if (args[0].equalsIgnoreCase("help")) {
				if (args.length == 1) {
					help(sender);
				} else {
					sender.sendMessage(ChatColor.RED + "/staff help");
				}
			} else if (args[0].equalsIgnoreCase("hide")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("SomnusManager.staff")) {
						shm.toggleHidden((Player) sender);
					} else {
						sender.sendMessage(ChatColor.RED + "/staff help");
					}
				} else {
					sender.sendMessage("Only players may issue that command.");
				}
			} else {
				if (sender instanceof Player) {
					shm.sendStaffMessage((Player) sender, getString(args));
				} else {
					sender.sendMessage("Only players may issue that command.");
				}
			}
		}
		return true;
	}

	private String getString(String[] args) {
		StringBuilder sb = new StringBuilder();
		if (args.length > 0) {
			sb.append(args[0]);
			int i = 1;
			while (i < args.length) {
				sb.append(" ");
				sb.append(args[i]);
				i++;
			}
		}
		return sb.toString();
	}

	private void help(CommandSender sender) {
		sender.sendMessage(new String[] {
				ChatColor.GOLD + "Staff Help Menu:",
				ChatColor.AQUA + "/staff: " + ChatColor.GRAY + "displays online staff",
				ChatColor.AQUA + "/staff <message>: " + ChatColor.GRAY
						+ "request help from staff with a message" });
		if (!(sender instanceof Player) || ((Player) sender).hasPermission("SomnusManager.admin")) {
			sender.sendMessage(new String[] {
					ChatColor.GOLD + "Staff Staff Commands:",
					ChatColor.AQUA + "/staff: " + ChatColor.GRAY
							+ "show all online staff. Hidden staff are grey",
					ChatColor.AQUA + "/staff hide: " + ChatColor.GRAY
							+ "toggles hidden state on the staff list" });
		}
	}
}
