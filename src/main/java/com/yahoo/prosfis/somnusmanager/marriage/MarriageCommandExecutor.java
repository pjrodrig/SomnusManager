package com.yahoo.prosfis.somnusmanager.marriage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class MarriageCommandExecutor implements CommandExecutor {

	private final MarriageManager mm;
	private final SomnusManager sm;

	public MarriageCommandExecutor(MarriageManager mm, SomnusManager sm) {
		this.mm = mm;
		this.sm = sm;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage(ChatColor.RED + "Marriages are currently disabled.");
		// if (cmd.getName().equalsIgnoreCase("church")) {
		// if (args.length == 0) {
		// if (sender instanceof Player) {
		// mm.warp((Player) sender);
		// } else
		// sender.sendMessage("Only players may issue this command.");
		// return true;
		// } else if (args.length == 2) {
		// if (args[0].equalsIgnoreCase("set")) {
		// if (sender instanceof Player) {
		// Player player = ((Player) sender);
		// if (player.hasPermission("SomnusManager.admin")) {
		// if (args[1].equalsIgnoreCase("warp")) {
		// mm.setChurch(player);
		// } else if (args[1].equalsIgnoreCase("p1")) {
		// mm.setPlayer1(player);
		// } else if (args[1].equalsIgnoreCase("p2")) {
		// mm.setPlayer2(player);
		// } else if (args[1].equalsIgnoreCase("priest")) {
		// mm.setPriest(player);
		// }
		// return true;
		// } else
		// player.sendMessage(ChatColor.RED
		// + "You do not have permission to use this command.");
		// } else
		// sender.sendMessage("Only players may issue this command.");
		// return true;
		// }
		// }
		// } else if (cmd.getName().equalsIgnoreCase("propose")) {
		// if (sender instanceof Player) {
		// if (args.length == 1) {
		// Player player = sm.getServer().getPlayer(args[0]);
		// if (player == null) {
		// sender.sendMessage(ChatColor.RED + "That player is not online.");
		// } else {
		// mm.propose((Player) sender, player);
		// }
		// return true;
		// }
		// } else {
		// sender.sendMessage("Only players may issue this command.");
		// return true;
		// }
		// } else if (cmd.getName().equalsIgnoreCase("marry")) {
		// if (args.length >= 1) {
		// if (args[0].equalsIgnoreCase("help")) {
		// if (args.length == 1) {
		// help(sender);
		// return true;
		// }
		// } else if (args[0].equalsIgnoreCase("accept")) {
		// if (sender instanceof Player) {
		// if (args.length == 1) {
		// mm.acceptProposal((Player) sender);
		// return true;
		// }
		// } else {
		// sender.sendMessage("Only a player may issue this command.");
		// return true;
		// }
		// } else if (args[0].equalsIgnoreCase("sethome")) {
		// if (sender instanceof Player) {
		// if (args.length == 1) {
		// mm.setMarryHome((Player) sender);
		// return true;
		// }
		// } else {
		// sender.sendMessage("Only a player may issue this command.");
		// return true;
		// }
		// } else if (args[0].equalsIgnoreCase("home")) {
		// if (args.length == 1) {
		// if (sender instanceof Player) {
		// mm.marryHome((Player) sender);
		// } else
		// sender.sendMessage("Only players may issue this command.");
		// return true;
		// }
		// } else if (args[0].equalsIgnoreCase("chat")) {
		// if (args.length == 1) {
		// if (sender instanceof Player) {
		// mm.marryChat((Player) sender);
		// } else
		// sender.sendMessage("Only players may issue this command.");
		// return true;
		// }
		// } else if (args[0].equalsIgnoreCase("priest")) {
		// if (args.length == 1) {
		// if (sender instanceof Player) {
		// Player player = (Player) sender;
		// List<MetadataValue> list = player.getMetadata("RPGClass");
		// if (list != null && list.size() > 0) {
		// int pclass = list.get(0).asInt();
		// if (pclass == 1 || pclass == 101) {
		// mm.addPriest(player);
		// } else {
		// sender.sendMessage(ChatColor.RED
		// + "You are not a priest or an acolyte.");
		// }
		// }
		// } else
		// sender.sendMessage("Only players may issue this command.");
		// return true;
		// }
		// }
		// }
		// } else if (cmd.getName().equalsIgnoreCase("divorce")) {
		// if (args.length == 0) {
		// if (sender instanceof Player)
		// mm.divorce((Player) sender);
		// else
		// sender.sendMessage("Only players may issue this command.");
		// return true;
		// }
		// }
		// sender.sendMessage(ChatColor.RED + "/marry help");
		return true;
	}

	private void help(CommandSender sender) {
		sender.sendMessage(new String[] { ChatColor.GOLD + "Marriage help menu:",
				ChatColor.AQUA + "/church: " + ChatColor.GRAY + "warp to church",
				ChatColor.AQUA + "/propose <player>: " + ChatColor.GRAY + "propose to player",
				ChatColor.AQUA + "/marry accept: " + ChatColor.GRAY + "accept proposal",
				ChatColor.AQUA + "/marry sethome: " + ChatColor.GRAY + "set shared marriage home",
				ChatColor.AQUA + "/marry home: " + ChatColor.GRAY + "warp to your mariage home",
				ChatColor.AQUA + "/marry chat: " + ChatColor.GRAY + "chat with your spouse",
				ChatColor.AQUA + "/divorce: " + ChatColor.GRAY + "divorce your partner" });
		if (!(sender instanceof Player) || ((Player) sender).hasPermission("SomnusManager.admin")) {
			sender.sendMessage(new String[] { ChatColor.GOLD + "Arena marriage commands:",
					ChatColor.AQUA + "/church set warp: " + ChatColor.GRAY + "set church warp",
					ChatColor.AQUA + "/church set p1: " + ChatColor.GRAY
							+ "set first player's position",
					ChatColor.AQUA + "/church set p2: " + ChatColor.GRAY
							+ "set second player's position",
					ChatColor.AQUA + "/church set priest: " + ChatColor.GRAY
							+ "set priest's position" });
		}
	}
}
