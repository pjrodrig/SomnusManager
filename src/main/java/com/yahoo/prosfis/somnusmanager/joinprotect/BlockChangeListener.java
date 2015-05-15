package com.yahoo.prosfis.somnusmanager.joinprotect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.collect.Maps;
import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.util.MetadataUtil;

public class BlockChangeListener implements Listener {

	private final SomnusManager sm;
	private final Map<UUID, Long> newPlayers;
	private final BukkitScheduler scheduler;
	private Queue<Runnable> queue;
	private Thread thread;

	public BlockChangeListener(SomnusManager sm) {
		this.sm = sm;
		this.scheduler = sm.getServer().getScheduler();
		this.newPlayers = Maps.newHashMap();
		queue = new LinkedBlockingQueue<Runnable>();
		init();
	}

	private void init() {
		UUID id;
		for (Player player : sm.getServer().getOnlinePlayers()) {
			id = player.getUniqueId();
			FileConfiguration config = sm.getSomnusPlayers();
			boolean firstJoin = false;
			if (!config.contains(id.toString())) {
				firstJoin = true;
				config.set(id + ".New", true);
				sm.saveSomnusPlayers();
			}
			if (firstJoin || config.getBoolean(id + ".New")) {
				newPlayers.put(id, (new Date().getTime() / 60000));
			}
		}
	}

	@EventHandler
	public void login(PlayerJoinEvent event) {
		UUID id = event.getPlayer().getUniqueId();
		FileConfiguration config = sm.getSomnusPlayers();
		boolean firstJoin = false;
		if (!config.contains(id.toString())) {
			firstJoin = true;
			config.set(id + ".New", true);
			sm.saveSomnusPlayers();
		}
		if (firstJoin || config.getBoolean(id + ".New")) {
			newPlayers.put(id, (new Date().getTime() / 60000));
		}
	}

	@EventHandler
	public void logout(PlayerQuitEvent event) {
		UUID id = event.getPlayer().getUniqueId();
		if (newPlayers.containsKey(id)) {
			FileConfiguration config = sm.getSomnusPlayers();
			float time = config.getLong(id + ".PlayTime");
			time += (new Date().getTime() / 60000) - newPlayers.get(id);
			if (time > 600) {
				config.set(id + ".New", false);
				config.set(id + ".PlayTime", null);
				newPlayers.remove(id);
			} else {
				config.set(id + ".PlayTime", time);
			}
			sm.saveSomnusPlayers();
		}
	}

	@EventHandler
	public void chunkLoad(ChunkLoadEvent event) {
		final Chunk chunk = event.getChunk();
		final World world = chunk.getWorld();
		final String worldName = world.getName();
		final int chunkX = chunk.getX(), chunkZ = chunk.getZ();
		addRunnable(new Runnable() {
			public void run() {
				try {
					Connection connection = sm.getConnection();
					if (connection != null) {
						final ResultSet rs = connection.createStatement().executeQuery(
								"SELECT * FROM sm_block_changes WHERE world ='" + worldName
										+ "' AND chunk_x = " + chunkX + " AND chunk_z = " + chunkZ);
						while (rs.next()) {
							final int x = rs.getInt("block_x"), y = rs.getInt("block_y"), z = rs
									.getInt("block_z");
							final String id = rs.getString("uuid");
							scheduler.scheduleSyncDelayedTask(sm, new Runnable() {
								public void run() {
									world.getBlockAt(x, y, z).setMetadata("SomnusPlayer",
											MetadataUtil.makeSimpleCallable(id, sm));
								}
							});
						}
					}
				} catch (SQLException e) {
					sm.getLogger().warning(e.getMessage());
				}
			}
		});
	}

	@EventHandler
	public void destroy(BlockBreakEvent event) {
		final Block block = event.getBlock();
		if (block.hasMetadata("SomnusPlayer")) {
			Player player = event.getPlayer();
			UUID id = player.getUniqueId();
			if (!newPlayers.containsKey(id)
					|| id.toString().equals(block.getMetadata("SomnusPlayer").get(0).asString())) {
				block.removeMetadata("SomnusPlayer", sm);
				addRunnable(new Runnable() {
					public void run() {
						try {
							sm.getConnection()
									.createStatement()
									.executeUpdate(
											"DELETE FROM sm_block_changes WHERE world = '"
													+ block.getWorld().getName()
													+ "' AND block_x = " + block.getX()
													+ " AND block_y = " + block.getY()
													+ " AND block_z = " + block.getZ());
						} catch (SQLException e) {
							sm.getLogger().warning(e.getMessage());
						}
					}
				});
			} else {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED
						+ "You cannot break blocks placed by other players yet.");
			}
		}
	}

	@EventHandler
	public void place(BlockPlaceEvent event) {
		final UUID id = event.getPlayer().getUniqueId();
		Block block = event.getBlock();
		final Location loc = block.getLocation();
		final Chunk chunk = loc.getChunk();
		block.setMetadata("SomnusPlayer", MetadataUtil.makeSimpleCallable(id.toString(), sm));
		addRunnable(new Runnable() {
			public void run() {
				try {
					sm.getConnection()
							.createStatement()
							.execute(
									"REPLACE INTO sm_block_changes(uuid, world, block_x, block_y, block_z, chunk_x, chunk_z) VALUES('"
											+ id + "', '" + loc.getWorld().getName() + "', "
											+ loc.getBlockX() + ", " + loc.getBlockY() + ", "
											+ loc.getBlockZ() + ", " + chunk.getX() + ", "
											+ chunk.getZ() + ")");
				} catch (SQLException e) {
					sm.getLogger().warning(e.getMessage());
				}
			}
		});
	}

	private void addRunnable(Runnable runnable) {
		queue.add(runnable);
		if (thread == null || !thread.isAlive()) {
			thread = new Thread(new Runnable() {
				public void run() {
					while (!queue.isEmpty()) {
						queue.poll().run();
					}
				}
			});
		}
	}
}
