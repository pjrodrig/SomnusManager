package com.yahoo.prosfis.somnusmanager.events.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.util.PlayerUtil;

public class LotteryEvent implements Event, CommandExecutor, Listener {

	private final SomnusManager sm;
	private List<UUID> bidders;
	private Scoreboard scoreboard;
	private Objective objective;
	private Score pot;
	private boolean active;

	public LotteryEvent(SomnusManager sm) {
		this.sm = sm;
		active = false;
	}

	public void start() {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("Lottery Pot", "Lottery Pot");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.DARK_GREEN + "Lottery Pot");
		pot = objective.getScore(ChatColor.GOLD + "Gold Ingots:");
		bidders = new ArrayList<UUID>();
		sm.getCommand("bid").setExecutor(this);
		active = true;
		Server server = sm.getServer();
		for (Player player : server.getOnlinePlayers()) {
			player.setScoreboard(scoreboard);
		}
		server.getPluginManager().registerEvents(this, sm);
		server.broadcastMessage(ChatColor.GOLD + "Lottery: " + ChatColor.DARK_GREEN + "type "
				+ ChatColor.GOLD + "/bid" + ChatColor.DARK_GREEN
				+ " to place a bid. Bids cost 5 gold ingots. You may bid up to three times for three entries.");
		server.getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				stop();
			}
		}, 20 * 60);
	}

	public void stop() {
		active = false;
		PlayerJoinEvent.getHandlerList().unregister(this);
		Server server = sm.getServer();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		for (Player player : server.getOnlinePlayers()) {
			player.setScoreboard(scoreboard);
		}
		int winnings = bidders.size() * 5;
		UUID pick = bidders.get((int) (Math.random() * bidders.size()));
		Player winner = server.getPlayer(pick);
		while (winner == null) {
			while (bidders.contains(pick)) {
				bidders.remove(pick);
			}
			if (bidders.isEmpty()) {
				break;
			} else {
				pick = bidders.get((int) (Math.random() * bidders.size()));
				winner = server.getPlayer(pick);
			}
		}
		if (winner != null) {
			server.broadcastMessage(ChatColor.DARK_GREEN + "All bids have been taken!");
			server.broadcastMessage(ChatColor.DARK_GREEN + "The lucky winner of " + ChatColor.GOLD
					+ winnings + ChatColor.DARK_GREEN + " gold ingots is " + ChatColor.GOLD
					+ winner.getName() + ChatColor.DARK_GREEN + "!");
			while (winnings >= 64) {
				winnings -= 64;
				PlayerUtil.giveItem(winner, new ItemStack(Material.GOLD_INGOT, 64));
			}
			if (winnings != 0) {
				PlayerUtil.giveItem(winner, new ItemStack(Material.GOLD_INGOT, winnings));
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (active) {
				Player player = (Player) sender;
				UUID id = player.getUniqueId();
				PlayerInventory inv = player.getInventory();
				HashMap<Integer, ? extends ItemStack> gold = inv.all(Material.GOLD_INGOT);
				int count = 0;
				for (UUID current : bidders) {
					if (current.equals(id)) {
						count++;
					}
				}
				if (count >= 3) {
					player.sendMessage(ChatColor.RED + "You've already placed three bids.");
				} else {
					if (inv.contains(Material.GOLD_INGOT, 5)) {
						int toRemove = 5, amount;
						for (ItemStack item : gold.values()) {
							amount = item.getAmount();
							if (amount > toRemove) {
								item.setAmount(amount - toRemove);
								toRemove = 0;
							} else {
								item.setAmount(-1);
								toRemove -= amount;
							}
							for (ItemStack slot : inv) {
								if (slot != null && slot.getAmount() == -1) {
									inv.remove(slot);
								}
							}
							player.updateInventory();
							if (toRemove == 0) {
								break;
							}
						}
						bidders.add(id);
						pot.setScore(pot.getScore() + 5);
					} else {
						sender.sendMessage(
								ChatColor.RED + "You do not have enough gold ingots to bid.");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "That event is not active.");
			}
		} else {
			sender.sendMessage("Only players may issue that command.");
		}
		return true;
	}

	@EventHandler
	public void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.DARK_GREEN + "You joined during a Lottery Event.");
		player.sendMessage(ChatColor.DARK_GREEN + "type " + ChatColor.GOLD + "/bid"
				+ ChatColor.DARK_GREEN
				+ " to place a bid. Bids cost 5 gold ingots. You may bid up to three times for three entries.");
	}
}
