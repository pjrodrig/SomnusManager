package com.yahoo.prosfis.somnusmanager.joinprotect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class BlockChangeListener implements Listener {

	private final SomnusManager sm;
	private final List<UUID> newPlayers;

	public BlockChangeListener(SomnusManager sm) {
		this.sm = sm;
		this.newPlayers = new ArrayList<UUID>();
	}

	@EventHandler
	public void destroy(BlockBreakEvent event) {
		Player player = event.getPlayer();
		UUID id = player.getUniqueId();
		if (newPlayers.contains(id)) {
			Location loc = event.getBlock().getLocation();
			try {
				ResultSet rs = sm
						.getConnection()
						.createStatement()
						.executeQuery(
								"SELECT uuid FROM block_changes WHERE world ='"
										+ loc.getWorld().getName()
										+ "' AND x = (" + loc.getX()
										+ ") AND y = (" + loc.getY()
										+ ") AND z = (" + loc.getZ()
										+ ") AND z > (" + loc.getZ()
										+ ") ORDER BY date desc;");
				if (rs.next()) {
					if (id.equals(UUID.fromString(rs.getString(0)))) {
						player.sendMessage(UUID.fromString(rs.getString(0))
								+ "");
					}
				}
			} catch (SQLException e) {
				sm.getLogger().warning(e.getMessage());
			}
		}
	}
	
	@EventHandler
	public void place(BlockPlaceEvent event){
		UUID id = event.getPlayer().getUniqueId();
		Location loc = event.getBlock().getLocation();
		try {
		ResultSet rs = sm
				.getConnection()
				.createStatement()
				.executeQuery(
						"SELECT uuid FROM block_changes WHERE world ='"
								+ loc.getWorld().getName()
								+ "' AND x = (" + loc.getX()
								+ ") AND y = (" + loc.getY()
								+ ") AND z = (" + loc.getZ()
								+ ") AND z > (" + loc.getZ()
								+ ") ORDER BY date desc;");
		if(rs.next()){
			sm
			.getConnection()
			.createStatement().executeUpdate(
			"UPDATE mytable"+
		    "SET column1 = value1,"+
		        "column2 = value2" +
		    "WHERE key_value = some_value;");
		}
		} catch (SQLException e) {
			sm.getLogger().warning(e.getMessage());
		}
	}

}
