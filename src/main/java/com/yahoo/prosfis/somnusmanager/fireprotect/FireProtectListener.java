package com.yahoo.prosfis.somnusmanager.fireprotect;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class FireProtectListener implements Listener{

	public final SomnusManager sm;
	
	public FireProtectListener(SomnusManager sm){
		this.sm = sm;
	}
	
	@EventHandler
	public void burn(BlockBurnEvent event){
		final Block block = event.getBlock();
		final Material type = block.getType();
		@SuppressWarnings("deprecation")
		final byte data = block.getData();
		sm.getServer().getScheduler().runTaskLater(sm, new Runnable(){
			@SuppressWarnings("deprecation")
			public void run(){
				block.setType(type);
				block.setData(data);
			}
		}, 0);
	}
}
