package com.yahoo.prosfis.somnusmanager.warnings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class WarningListener implements Listener {

	private final SomnusManager sm;

	public WarningListener(SomnusManager sm) {
		this.sm = sm;
	}

	@EventHandler
	public void login(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final UUID id = player.getUniqueId();
		new Thread(new Runnable() {
			public void run() {
				try {
					ResultSet rs = sm
							.getConnection()
							.createStatement()
							.executeQuery(
									"SELECT * FROM sm_warnings WHERE uuid ='" + id.toString()
											+ "' AND deleted = false ORDER BY id asc");
					int total = 0;
					boolean hasNew = false;
					while (rs.next()) {
						if (!hasNew) {
							hasNew = !rs.getBoolean("viewed");
						}
						total += rs.getInt("points");
					}
					if (hasNew) {
						sm.getServer().getScheduler()
								.runTaskLaterAsynchronously(sm, new Runnable() {
									public void run() {
										player.sendMessage(ChatColor.RED
												+ ""
												+ ChatColor.BOLD
												+ "You have new warnings. Type '/warnings' to view them.");
									}
								}, 20);
					}
					if (total > 0 && !player.hasPermission("SomnusManager.staff")) {
						for (Player p : sm.getServer().getOnlinePlayers()) {
							if (p.hasPermission("SomnusManager.staff")) {
								p.sendMessage(ChatColor.GRAY + player.getName() + " has "
										+ ChatColor.RED + total + ChatColor.GRAY
										+ " warning points.");
							}
						}
					}
				} catch (SQLException e) {
					sm.getLogger().warning(e.getMessage());
				}
			}
		}).start();
	}
}
