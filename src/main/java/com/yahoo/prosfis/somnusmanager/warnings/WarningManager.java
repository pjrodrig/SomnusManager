package com.yahoo.prosfis.somnusmanager.warnings;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.BanList;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.util.DatabaseUtil;

public class WarningManager {

	private final SomnusManager sm;

	public WarningManager(SomnusManager sm) {
		this.sm = sm;
		init();
	}

	private void init() {
		new Thread(new Runnable() {
			public void run() {
				try {
					sm.getConnection()
							.createStatement()
							.execute(
									"UPDATE sm_warnings SET deleted=true WHERE DATE_SUB(CURRENT_TIMESTAMP,INTERVAL 60 DAY)>date");
				} catch (SQLException e) {
					sm.getServer().getLogger().info(e.getMessage());
				}
			}
		}).start();
	}

	public void warn(final CommandSender sender, final String warner, final String player,
			final UUID id, final int points, final String unpreparedWarning, final Location loc,
			final boolean silent) {
		new Thread(new Runnable() {
			public void run() {
				boolean banned = false;
				Server server = sm.getServer();
				String warning = DatabaseUtil.prepareString(unpreparedWarning);
				try {
					Connection connection = sm.getConnection();
					if (loc != null) {
						connection
								.createStatement()
								.execute(
										"INSERT INTO sm_warnings(uuid, warning, points, has_location, world, x, y, z, date, viewed, deleted) VALUES('"
												+ id
												+ "', '"
												+ warning
												+ "', "
												+ points
												+ ", true, '"
												+ loc.getWorld().getName()
												+ "', "
												+ (int) loc.getX()
												+ ", "
												+ (int) loc.getY()
												+ ", "
												+ (int) loc.getZ()
												+ ", CURRENT_TIMESTAMP, false, false)");
					} else {
						sm.getConnection()
								.createStatement()
								.execute(
										"INSERT INTO sm_warnings(uuid, warning, points, has_location, date, viewed, deleted) VALUES('"
												+ id + "', '" + warning + "', " + points
												+ ", false, CURRENT_TIMESTAMP, false, false)");
					}
					ResultSet rs = connection.createStatement().executeQuery(
							"SELECT * FROM sm_warnings WHERE uuid ='" + id.toString()
									+ "' AND deleted = false");
					int total = 0;
					while (rs.next()) {
						total += rs.getInt("points");
					}
					if (total >= 10) {
						server.getBanList(BanList.Type.NAME).addBan(player,
								"Warning limit reached! Appeal at SomnusRealms.com", null,
								"Warning limit reached");
						final Player onlinePlayer = server.getPlayer(id);
						if (onlinePlayer != null) {
							server.getScheduler().scheduleSyncDelayedTask(sm, new Runnable() {
								public void run() {
									onlinePlayer
											.kickPlayer("Warning limit reached! Appeal at SomnusRealms.com");
								}
							});
						}
						banned = true;
					}
				} catch (SQLException e) {
					sm.getServer().getLogger().info(e.getMessage());
				}
				if (!silent) {
					sm.getServer().broadcastMessage(
							ChatColor.GOLD + player + ChatColor.YELLOW
									+ " has recieved a warning for " + ChatColor.GOLD + warning
									+ ChatColor.RED + " worth " + points + " points.");
				}
				if (banned) {
					server.broadcastMessage(ChatColor.GOLD + player + ChatColor.YELLOW
							+ " has been banned for reaching 10 warning points.");
				}
			}
		}).start();
	}

	public void deleteWarning(final CommandSender sender, final UUID player, final int id,
			boolean full) {
		if (full) {
			new Thread(new Runnable() {
				public void run() {
					try {
						Connection connection = sm.getConnection();
						ResultSet rs = connection.createStatement().executeQuery(
								"SELECT * FROM sm_warnings WHERE uuid='" + player.toString()
										+ "' AND id=" + id);
						if (rs.next()) {
							connection.createStatement().execute(
									"INSERT INTO sm_warnings_history(uuid, deleted_by, warning, points, has_location, date) VALUES('"
											+ rs.getString("uuid") + "', '"
											+ ((Player) sender).getName() + "', '"
											+ rs.getString("warning") + "', " + rs.getInt("points")
											+ ", " + rs.getBoolean("has_location")
											+ ", CURRENT_TIMESTAMP)");
							connection.createStatement().execute(
									"DELETE from sm_warnings WHERE uuid='" + player.toString()
											+ "' AND id=" + id);
							rs = connection.createStatement().executeQuery(
									"SELECT * FROM sm_warnings WHERE uuid ='" + player.toString()
											+ "' ORDER BY id asc");
							int index = 0, old;
							while (rs.next()) {
								index++;
								old = rs.getInt("id");
								if (old != index) {
									connection.createStatement().execute(
											"UPDATE sm_warnings SET id=" + index + " WHERE uuid='"
													+ player.toString() + "' AND id=" + old);
								}
							}
							sender.sendMessage(ChatColor.GREEN + "Warning fully deleted.");
						} else {
							sender.sendMessage(ChatColor.RED + "Warning not found.");
						}
					} catch (SQLException e) {
						sm.getServer().getLogger().info(e.getMessage());
					}
				}
			}).start();
		} else {
			new Thread(new Runnable() {
				public void run() {
					try {
						sm.getConnection()
								.createStatement()
								.execute(
										"UPDATE sm_warnings SET deleted=true WHERE uuid='"
												+ player.toString() + "' AND id=" + id);
						sender.sendMessage(ChatColor.GREEN + "Warning deleted.");
					} catch (SQLException e) {
						sm.getServer().getLogger().info(e.getMessage());
					}
				}
			}).start();
		}
	}

	public void showWarnings(final String playerName, final UUID player,
			final CommandSender sender, final boolean owner, final boolean all) {
		new Thread(new Runnable() {
			public void run() {
				try {
					Connection connection = sm.getConnection();
					if (connection != null) {
						final ResultSet rs;
						if (all) {
							rs = connection.createStatement().executeQuery(
									"SELECT * FROM sm_warnings WHERE uuid ='" + player.toString()
											+ "' ORDER BY id asc");
						} else {
							rs = connection.createStatement().executeQuery(
									"SELECT * FROM sm_warnings WHERE uuid ='" + player.toString()
											+ "' AND deleted = false ORDER BY id asc");
						}
						sender.sendMessage(ChatColor.YELLOW + "Warnings for " + ChatColor.GOLD
								+ playerName + ChatColor.YELLOW + ":");
						int total = 0, current, index = 0;
						String location;
						boolean viewedAll = true;
						while (rs.next()) {
							current = rs.getInt("points");
							if (owner) {
								index++;
							} else {
								index = rs.getInt("id");
							}
							if (owner && viewedAll) {
								viewedAll = rs.getBoolean("viewed");
							}
							total += current;
							if (rs.getBoolean("has_location")) {
								location = ChatColor.GREEN + " Location: " + rs.getString("world")
										+ " " + rs.getInt("x") + ":" + rs.getInt("y") + ":"
										+ rs.getInt("z") + " " + ChatColor.GRAY;
							} else {
								location = "";
							}
							sender.sendMessage(ChatColor.GOLD + "" + index + ": " + ChatColor.GRAY
									+ DatabaseUtil.reversePrepareString(rs.getString("warning"))
									+ ChatColor.RED + " " + current + " points. " + ChatColor.GRAY
									+ location + rs.getTimestamp("date"));
						}
						if (owner && !viewedAll) {
							setAllViewed(player);
						}
						sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Total Points: "
								+ total);
					}
				} catch (SQLException e) {
					sm.getLogger().warning(e.getMessage());
				}
			}
		}).start();
	}

	public void warnTeleport(final Player sender, final UUID player, final int id) {
		try {
			final ResultSet rs = sm
					.getConnection()
					.createStatement()
					.executeQuery(
							"SELECT * FROM sm_warnings WHERE uuid ='" + player.toString()
									+ "' AND id=" + id);
			if (rs.next()) {
				if (rs.getBoolean("has_location")) {
					Location loc = new Location(sm.getServer().getWorld(rs.getString("world")),
							rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
					sender.teleport(loc);
					sender.sendMessage(ChatColor.AQUA + "Teleport successful.");
				} else {
					sender.sendMessage(ChatColor.RED + "Warning does not have a location.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Warning not found.");
			}
		} catch (SQLException e) {
			sm.getServer().getLogger().info(e.getMessage());
		}
	}

	public void setAllViewed(final UUID player) {
		new Thread(new Runnable() {
			public void run() {
				try {
					sm.getConnection()
							.createStatement()
							.execute(
									"UPDATE sm_warnings SET viewed=true WHERE uuid='"
											+ player.toString()
											+ "' AND viewed=false AND deleted=false");
				} catch (SQLException e) {
					sm.getServer().getLogger().info(e.getMessage());
				}
			}
		}).start();
	}

}
