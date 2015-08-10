package com.yahoo.prosfis.somnusmanager.events;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.yahoo.prosfis.somnusmanager.SomnusManager;

public class EventCommandExecutor implements CommandExecutor {

	private final EventManager em;

	public EventCommandExecutor(EventManager em, SomnusManager sm) {
		this.em = em;
		sm.getCommand("event").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/event help");
		} else {
			if (args[0].equalsIgnoreCase("start")) {
				if (args.length == 2) {
					EventType event = EventType.getEvent(args[1]);
					if (event == null) {
						sender.sendMessage(ChatColor.RED + "That is not a valid event.");
					} else {
						em.startEvent(event);
					}
				}
			}
		}
		return true;
	}
}
