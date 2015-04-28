package com.yahoo.prosfis.somnusmanager.quickwarp.listeners;

import java.util.Map;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.google.common.collect.Maps;

public class QuickWarpListener implements Listener{
	
	private final Map<Location, Location> warps;

	public QuickWarpListener(){
		warps = Maps.newHashMap();
	}
	
	@EventHandler
	public void warp(PlayerMoveEvent event){
		Location loc = event.getTo().getBlock().getLocation();
		if(warps.containsKey(loc)){
			Player player = event.getPlayer();
			player.teleport(warps.get(loc));
			player.sendMessage(ChatColor.AQUA + "Quick warp successful");
		}
	}
	
	public void addWarp(Location loc1, Location loc2){
		warps.put(loc1, loc2);
	}
	
	public void removeWarp(Location loc){
		warps.remove(loc);
	}
}
