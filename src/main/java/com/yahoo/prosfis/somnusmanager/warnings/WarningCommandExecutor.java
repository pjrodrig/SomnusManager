package com.yahoo.prosfis.somnusmanager.warnings;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class WarningCommandExecutor implements CommandExecutor {

	private final WarningManager wm;
	private final SomnusManager sm;

	public WarningCommandExecutor(WarningManager wm, SomnusManager sm) {
		this.wm = wm;
		this.sm = sm;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String com = cmd.getName(), warning;
		Server server;
		Player player;
		OfflinePlayer oplayer;
		Integer points;
		UUID id = null;
		String warner;
		if (com.equalsIgnoreCase("warnings")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					wm.showWarnings(((Player) sender).getName(), ((Player) sender).getUniqueId(),
							sender, true, false);
				} else {
					sender.sendMessage("Only players may issue that command.");
				}
			} else if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("all"))) {
				if (!(sender instanceof Player) || sender.hasPermission("SomnusManager.staff")) {
					server = sm.getServer();
					player = server.getPlayer(args[0]);
					if (player != null) {
						id = player.getUniqueId();
					} else {
						oplayer = server.getOfflinePlayer(args[0]);
						if (oplayer != null) {
							id = oplayer.getUniqueId();
						}
					}
					wm.showWarnings(args[0], id, sender, false, args.length == 2);
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You do not have permission to view other player's warnings.");
				}
			}
		} else if (com.equalsIgnoreCase("warn")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				help(sender);
			} else if (args.length > 2) {
				server = sm.getServer();
				player = server.getPlayer(args[0]);
				if (player != null) {
					id = player.getUniqueId();
				} else {
					oplayer = server.getOfflinePlayer(args[0]);
					if (oplayer != null) {
						id = oplayer.getUniqueId();
					}
				}
				if (id != null) {
					try {
						points = Integer.parseInt(args[1]);
						warning = getWarning(2, args);
						if (sender instanceof Player) {
							warner = ((Player) sender).getName();
						} else {
							warner = "Console";
						}
						wm.warn(sender, warner, args[0], id, points, warning + " by " + warner,
								null, false);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "/warn help");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "That player has never joined this server.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/warn help");
			}
		} else if (com.equalsIgnoreCase("lwarn")) {
			if (sender instanceof Player) {
				if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
					help(sender);
				} else if (args.length > 2) {
					server = sm.getServer();
					player = server.getPlayer(args[0]);
					if (player != null) {
						id = player.getUniqueId();
					} else {
						oplayer = server.getOfflinePlayer(args[0]);
						if (oplayer != null) {
							id = oplayer.getUniqueId();
						}
					}
					if (id != null) {
						try {
							points = Integer.parseInt(args[1]);
							warning = getWarning(2, args);
							warner = ((Player) sender).getName();
							wm.warn(sender, warner, args[0], id, points, warning + " by " + warner,
									((Player) sender).getLocation(), false);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "/warn help");
						}
					} else {
						sender.sendMessage(ChatColor.RED
								+ "That player has never joined this server.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "/warn help");
				}
			} else {
				sender.sendMessage("Only players may issue that command");
			}
		} else if (com.equalsIgnoreCase("swarn")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				help(sender);
			} else if (args.length > 2) {
				server = sm.getServer();
				player = server.getPlayer(args[0]);
				if (player != null) {
					id = player.getUniqueId();
				} else {
					oplayer = server.getOfflinePlayer(args[0]);
					if (oplayer != null) {
						id = oplayer.getUniqueId();
					}
				}
				if (id != null) {
					try {
						points = Integer.parseInt(args[1]);
						warning = getWarning(2, args);
						if (sender instanceof Player) {
							warner = ((Player) sender).getName();
						} else {
							warner = "Console";
						}
						wm.warn(sender, warner, args[0], id, points, warning + " by " + warner,
								null, true);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "/warn help");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "That player has never joined this server.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/warn help");
			}
		} else if (com.equalsIgnoreCase("slwarn")) {
			if (sender instanceof Player) {
				if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
					help(sender);
				} else if (args.length > 2) {
					server = sm.getServer();
					player = server.getPlayer(args[0]);
					if (player != null) {
						id = player.getUniqueId();
					} else {
						oplayer = server.getOfflinePlayer(args[0]);
						if (oplayer != null) {
							id = oplayer.getUniqueId();
						}
					}
					if (id != null) {
						try {
							points = Integer.parseInt(args[1]);
							warning = getWarning(2, args);
							warner = ((Player) sender).getName();
							wm.warn(sender, warner, args[0], id, points, warning + " by " + warner,
									((Player) sender).getLocation(), true);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "/warn help");
						}
					} else {
						sender.sendMessage(ChatColor.RED
								+ "That player has never joined this server.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "/warn help");
				}
			} else {
				sender.sendMessage("Only players may issue that command");
			}
		} else if (com.equalsIgnoreCase("warnas")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				help(sender);
			} else if (args.length > 3) {
				server = sm.getServer();
				player = server.getPlayer(args[1]);
				if (player != null) {
					id = player.getUniqueId();
				} else {
					oplayer = server.getOfflinePlayer(args[1]);
					if (oplayer != null) {
						id = oplayer.getUniqueId();
					}
				}
				if (id != null) {
					try {
						points = Integer.parseInt(args[2]);
						warning = getWarning(2, args);
						warner = args[0];
						wm.warn(sender, warner, args[1], id, points, warning + " by " + warner,
								null, false);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "/warn help");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "That player has never joined this server.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/warn help");
			}
		} else if (com.equalsIgnoreCase("lwarnas")) {
			if (sender instanceof Player) {
				if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
					help(sender);
				} else if (args.length > 3) {
					server = sm.getServer();
					player = server.getPlayer(args[1]);
					if (player != null) {
						id = player.getUniqueId();
					} else {
						oplayer = server.getOfflinePlayer(args[1]);
						if (oplayer != null) {
							id = oplayer.getUniqueId();
						}
					}
					if (id != null) {
						try {
							points = Integer.parseInt(args[2]);
							warning = getWarning(2, args);
							warner = args[0];
							wm.warn(sender, warner, args[1], id, points, warning + " by " + warner,
									((Player) sender).getLocation(), false);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "/warn help");
						}
					} else {
						sender.sendMessage(ChatColor.RED
								+ "That player has never joined this server.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "/warn help");
				}
			} else {
				sender.sendMessage("Only players may issue that command.");
			}
		} else if (com.equalsIgnoreCase("swarnas")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				help(sender);
			} else if (args.length > 3) {
				server = sm.getServer();
				player = server.getPlayer(args[1]);
				if (player != null) {
					id = player.getUniqueId();
				} else {
					oplayer = server.getOfflinePlayer(args[1]);
					if (oplayer != null) {
						id = oplayer.getUniqueId();
					}
				}
				if (id != null) {
					try {
						points = Integer.parseInt(args[2]);
						warning = getWarning(2, args);
						warner = args[0];
						wm.warn(sender, warner, args[1], id, points, warning + " by " + warner,
								null, true);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "/warn help");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "That player has never joined this server.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/warn help");
			}
		} else if (com.equalsIgnoreCase("slwarnas")) {
			if (sender instanceof Player) {
				if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
					help(sender);
				} else if (args.length > 3) {
					server = sm.getServer();
					player = server.getPlayer(args[1]);
					if (player != null) {
						id = player.getUniqueId();
					} else {
						oplayer = server.getOfflinePlayer(args[1]);
						if (oplayer != null) {
							id = oplayer.getUniqueId();
						}
					}
					if (id != null) {
						try {
							points = Integer.parseInt(args[2]);
							warning = getWarning(2, args);
							warner = args[0];
							wm.warn(sender, warner, args[1], id, points, warning + " by " + warner,
									((Player) sender).getLocation(), true);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "/warn help");
						}
					} else {
						sender.sendMessage(ChatColor.RED
								+ "That player has never joined this server.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "/warn help");
				}
			} else {
				sender.sendMessage("Only players may issue that command.");
			}
		} else if (com.equalsIgnoreCase("cwarn")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				help(sender);
			} else if (args.length == 2) {
				try {
					int num = Integer.parseInt(args[1]);
					server = sm.getServer();
					player = server.getPlayer(args[0]);
					if (player != null) {
						id = player.getUniqueId();
					} else {
						oplayer = server.getOfflinePlayer(args[0]);
						if (oplayer != null) {
							id = oplayer.getUniqueId();
						}
					}
					wm.deleteWarning(sender, id, num, false);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "/cwarn help");
				}
			} else if (args.length == 3 && args[2].equalsIgnoreCase("full")) {
				if (sender instanceof Player) {
					try {
						int num = Integer.parseInt(args[1]);
						server = sm.getServer();
						player = server.getPlayer(args[0]);
						if (player != null) {
							id = player.getUniqueId();
						} else {
							oplayer = server.getOfflinePlayer(args[0]);
							if (oplayer != null) {
								id = oplayer.getUniqueId();
							}
						}
						wm.deleteWarning(sender, id, num, true);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "/cwarn help");
					}
				} else {
					sender.sendMessage("Only players may issue that command.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/cwarn help");
			}
		} else if (com.equalsIgnoreCase("warntp")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				help(sender);
			} else if (args.length == 2) {
				if (sender instanceof Player) {
					try {
						int num = Integer.parseInt(args[1]);
						server = sm.getServer();
						player = server.getPlayer(args[0]);
						if (player != null) {
							id = player.getUniqueId();
						} else {
							oplayer = server.getOfflinePlayer(args[0]);
							if (oplayer != null) {
								id = oplayer.getUniqueId();
							}
						}
						wm.warnTeleport((Player) sender, id, num);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "/warntp help");
					}
				} else {
					sender.sendMessage("Only players may issue that command.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/warntp help");
			}
		}
		return true;
	}

	private String getWarning(int start, String[] args) {
		int current = start + 1;
		StringBuilder sb = new StringBuilder();
		sb.append(args[start]);
		while (current < args.length) {
			sb.append(" ");
			sb.append(args[current]);
			current++;
		}
		return sb.toString();
	}

	private void help(CommandSender sender) {
		sender.sendMessage(new String[] {
				ChatColor.GOLD + "Warning commands:",
				ChatColor.AQUA + "/warnings [player] [all]: " + ChatColor.GRAY
						+ "displays a players warnings 'all' includes deleted warnings",
				ChatColor.AQUA + "/warn <player> <points> [reason]: " + ChatColor.GRAY
						+ "basic warning",
				ChatColor.AQUA + "/lwarn <player> <points> [reason]: " + ChatColor.GRAY
						+ "warning with location attached",
				ChatColor.AQUA + "/swarn <player> <points> [reason]: " + ChatColor.GRAY
						+ "silent warning",
				ChatColor.AQUA + "/slwarn <player> <points> [reason]: " + ChatColor.GRAY
						+ "silent warning with location",
				ChatColor.AQUA + "/warnas <warner> <player> <points> [reason]: " + ChatColor.GRAY
						+ "warn as if specified warner",
				ChatColor.AQUA + "/lwarnas <warner> <player> <points> [reason]: " + ChatColor.GRAY
						+ "warn as if specified warner with location",
				ChatColor.AQUA + "/swarnas <warner> <player> <points> [reason]: " + ChatColor.GRAY
						+ "warn as if specified warner silently",
				ChatColor.AQUA + "/slwarnas <warner> <player> <points> [reason]: " + ChatColor.GRAY
						+ "warn as if specified warner silently with location",
				ChatColor.AQUA + "/cwarn <player> <warning#>: " + ChatColor.GRAY
						+ "remove specified warning or all warnings",
				ChatColor.AQUA + "/warntp <player> <warning#>: " + ChatColor.GRAY
						+ "teleport to the location of a warning" });
	}
}
