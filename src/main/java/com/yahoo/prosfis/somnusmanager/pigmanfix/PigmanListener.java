package com.yahoo.prosfis.somnusmanager.pigmanfix;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.inventory.ItemStack;

public class PigmanListener implements Listener {

	public PigmanListener() {
	}

	@EventHandler
	public void death(EntityDeathEvent event) {
		if (event.getEntity() instanceof PigZombie) {
			List<ItemStack> items = event.getDrops();
			Iterator<ItemStack> iter = items.iterator();
			Material type;
			while (iter.hasNext()) {
				type = iter.next().getType();
				if (type.toString().toLowerCase().contains("gold")) {
					iter.remove();
				}
			}
		}
	}

	@EventHandler
	public void portal(EntityPortalExitEvent event) {
		if (event.getEntity() instanceof PigZombie) {
			event.setCancelled(true);
		}
	}
}
