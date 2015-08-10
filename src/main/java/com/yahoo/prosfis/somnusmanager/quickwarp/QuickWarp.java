package com.yahoo.prosfis.somnusmanager.quickwarp;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.scheduler.BukkitTask;

import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.customentities.FakeFallingBlock;

public class QuickWarp {

	private final SomnusManager sm;

	private final String name;
	private IBlockData block;
	private Material material;
	private int data;
	private int ticks, rate;
	private final double x, y, z;
	private double magnitude;
	private final World world;
	private final WorldServer ws;
	private boolean repeat;
	private Location loc, destination;
	private BukkitTask task;

	public QuickWarp(String name, Location loc, Material material, int data, SomnusManager sm) {
		this(name, loc, null, material, data, sm);
	}
	
	@SuppressWarnings("deprecation")
	public QuickWarp(String name, Location loc, Location destination, Material material, int data, SomnusManager sm) {
		this.sm = sm;
		this.name = name;
		this.material = material;
		this.block = Block.getById(material.getId()).fromLegacyData(data);
		this.data = data;
		this.ticks = 20;
		this.rate = 7;
		this.magnitude = .5;
		this.loc = loc;
		this.destination = destination;
		this.world = loc.getWorld();
		ws = ((CraftWorld) world).getHandle();
		this.x = loc.getBlockX() + .5;
		this.y = loc.getBlockY() + .5;
		this.z = loc.getBlockZ() + .5;
	}

	private void spawnBlock() {
		FakeFallingBlock ffblock = new FakeFallingBlock(ws, x, y, z, block, ticks);
		ffblock.ticksLived = 1;
		ws.addEntity(ffblock, SpawnReason.CUSTOM);
		// Motion
		ffblock.motX = 0;
		ffblock.motY = magnitude;
		ffblock.motZ = 0;
		if (repeat) {
			task = sm.getServer().getScheduler().runTaskLater(sm, new Runnable() {
				public void run() {
					spawnBlock();
				}
			}, rate);
		}
	}

	public void start() {
		repeat = true;
		spawnBlock();
	}

	public void stop() {
		if (task != null)
			task.cancel();
		repeat = false;
	}

	public World getWorld() {
		return world;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Material getMaterial() {
		return material;
	}

	public int getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public int getTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public Location getDestination() {
		return destination;
	}

	public void setDestination(Location destination) {
		this.destination = destination;
	}
}
