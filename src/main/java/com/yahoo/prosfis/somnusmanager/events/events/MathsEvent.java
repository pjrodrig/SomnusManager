package com.yahoo.prosfis.somnusmanager.events.events;

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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.util.PlayerUtil;

public class MathsEvent implements Event, CommandExecutor, Listener {

	private final SomnusManager sm;
	private Scoreboard scoreboard;
	private Objective objective;
	private double answer;
	private int round;
	private String question;
	private Score wrongAnswers;
	private boolean active, roundActive;
	private BukkitTask task;

	public MathsEvent(SomnusManager sm) {
		this.sm = sm;
		active = false;
	}

	public void start() {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("Wrong Answers", "Wrong Answers");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.DARK_GREEN + "Wrong Answers");
		wrongAnswers = objective.getScore(ChatColor.GOLD + "Count:");
		sm.getCommand("math").setExecutor(this);
		active = true;
		round = 0;
		Server server = sm.getServer();
		for (Player player : server.getOnlinePlayers()) {
			player.setScoreboard(scoreboard);
		}
		server.getPluginManager().registerEvents(this, sm);
		server.broadcastMessage(ChatColor.GOLD + "Maths: " + ChatColor.DARK_GREEN + "The first player to answer the math problem correctly in under one minute wins.");
		startRound();
	}

	public void stop() {
		active = false;
		round = 0;
		PlayerJoinEvent.getHandlerList().unregister(this);
		Server server = sm.getServer();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		for (Player player : server.getOnlinePlayers()) {
			player.setScoreboard(scoreboard);
		}
	}

	private void startRound() {
		round++;
		roundActive = true;
		Server server = sm.getServer();
		wrongAnswers.setScore(0);
		generateQuestion();
		server.broadcastMessage(ChatColor.DARK_GREEN + "Round " + round + ":" + ChatColor.GOLD + "\n" + question + ChatColor.DARK_GREEN + "\nUse " + ChatColor.GOLD + "/math <answer>" + ChatColor.DARK_GREEN + " to answer.");
		task = server.getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				endRound(null);
			}
		}, 20 * 60);
	}
	
	private void generateQuestion(){
		switch(round){
		case 1:
			generateEasyQuestion();
			break;
		case 2:
			generateMediumQuestion();
			break;
		case 3:
			generateMediumQuestion();
			break;
		}
	}
	
	private void generateEasyQuestion(){
		int num1, num2;
		switch((int)(Math.random() * 4)){
		case 0:
			num1 = getLargeNumber();
			num2 = getLargeNumber();
			answer = num1 + num2;
			question = num1 + " + " + num2 + " = ?";
			break;
		case 1:
			num1 = getLargeNumber();
			num2 = getLargeNumber();
			answer = num1 - num2;
			question = num1 + " - " + num2 + " = ?";
			break;
		case 2:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			answer = num1 * num2;
			question = num1 + " x " + num2 + " = ?";
			break;
		case 3:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			answer = num2;
			question = (num1 * num2) + " / " + num1 + " = ?";
			break;
		}
	}
	
	private void generateMediumQuestion(){
		int num1, num2, num3;
		switch((int)(Math.random() * 16)){
		case 0:
			num1 = getLargeNumber();
			num2 = getSmallNumber();
			num3 = getSmallNumber();
			answer = num1 + (num2 * num3);
			question = num1 + " + " + num2 + " * " + num3 + " = ?";
			break;
		case 1:
			num1 = getLargeNumber();
			num2 = getSmallNumber();
			num3 = getSmallNumber();
			answer = num1 - (num2 * num3);
			question = num1 + " - " + num2 + " * " + num3 + " = ?";
			break;
		case 2:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getSmallNumber();
			answer = (num1 + num2) * num3;
			question = "(" + num1 + " + " + num2 + ") * " + num3 + " = ?";
			break;
		case 3:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getSmallNumber();
			answer = (num1 - num2) * num3;
			question = "(" + num1 + " - " + num2 + ") * " + num3 + " = ?";
			break;
		case 4:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getSmallNumber();
			answer = num1 + (num2 * num3) / num3;
			question = num1 + " + " + (num2 * num3) + " / " + num3 + " = ?";
		case 5:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getSmallNumber();
			answer = num1 - (num2 * num3) / num3;
			question = num1 + " - " + (num2 * num3) + " / " + num3 + " = ?";
			break;
		case 6:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getSmallNumber();
			answer = (num1 + (num2 * num3 - num1)) / num3;
			question = "(" + num1 + " + " + (num2 * num3 - num1) + ") / " + num3 + " = ?";
			break;
		case 7:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getSmallNumber();
			answer = (num1 - (num2 * num3 + num1)) / num3;
			question = "(" + num1 + " - " + (num2 * num3 - num1) + ") / " + num3 + " = ?";
			break;
		case 8:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getLargeNumber();
			answer = num1 * num2 + num3;
			question = num1 + " * " + num2 + " + " + num3 + " = ?";
			break;
		case 9:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getLargeNumber();
			answer = num1 * num2 - num3;
			question = num1 + " * " + num2 + " - " + num3 + " = ?";
			break;
		case 10:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getLargeNumber();
			answer = num1 + num3;
			question = (num1 * num2) + " / " + num2 + " + " + num3 + " = ?";
			break;
		case 11:
			num1 = getSmallNumber();
			num2 = getSmallNumber();
			num3 = getLargeNumber();
			answer = num1 - num3;
			question = (num1 * num2) + " / " + num2 + " - " + num3 + " = ?";
			break;
		}
	}
	
	private int getLargeNumber(){
		return (int)((Math.random() * 51) - 25);
	}
	
	private int getSmallNumber(){
		return (int)((Math.random() * 21) - 10);
	}

	private void endRound(Player player) {
		roundActive = false;
		Server server = sm.getServer();
		if (player == null) {
			server.broadcastMessage(ChatColor.DARK_GREEN + "Time is up! The answer was: " + ChatColor.GOLD + answer
					+ ChatColor.DARK_GREEN + "\nThere were no winners this round.");
		} else {
			task.cancel();
			server.broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.DARK_GREEN
					+ " has answered correctly!\nThe answer was: " + ChatColor.GOLD + answer);
			PlayerUtil.giveItem(player, new ItemStack(Material.GOLD_INGOT, round * 5));
		}
		if (round < 3) {
			server.getScheduler().runTaskLater(sm, new Runnable() {
				public void run() {
					startRound();
				}
			}, 20 * 10);
		} else {
			stop();
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (active) {
				if (roundActive) {
					if (args.length == 1) {
						try {
							if (Double.parseDouble(args[0]) == answer) {
								endRound((Player) sender);
							} else {
								sender.sendMessage(ChatColor.GOLD + "That answer is incorrect.");
								wrongAnswers.setScore(wrongAnswers.getScore() + 1);
							}
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Your answer must be a number.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "/math <answer>");
					}
				} else {
					sender.sendMessage(ChatColor.GOLD + "The round has ended.");
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
		player.sendMessage(ChatColor.DARK_GREEN + "You joined during a Maths Event.");
		if(roundActive){
		player.sendMessage(ChatColor.DARK_GREEN + "type " + ChatColor.GOLD + "/maths <answer>" + ChatColor.DARK_GREEN
				+ " to place a bid. Bids cost 5 gold ingots. You may bid up to three times for three entries.");
		} else {
			player.sendMessage(ChatColor.DARK_GREEN + "A round will be starting shortly.");
		}
	}

}
