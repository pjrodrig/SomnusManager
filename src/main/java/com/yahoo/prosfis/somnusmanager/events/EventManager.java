package com.yahoo.prosfis.somnusmanager.events;

import java.util.HashMap;

import org.bukkit.Server;

import com.google.common.collect.Maps;
import com.yahoo.prosfis.somnusmanager.SomnusManager;
import com.yahoo.prosfis.somnusmanager.events.events.Event;
import com.yahoo.prosfis.somnusmanager.events.events.GoToHellEvent;
import com.yahoo.prosfis.somnusmanager.events.events.LotteryEvent;
import com.yahoo.prosfis.somnusmanager.events.events.LoveLettersEvent;
import com.yahoo.prosfis.somnusmanager.events.events.MathsEvent;
import com.yahoo.prosfis.somnusmanager.events.events.MineContestEvent;
import com.yahoo.prosfis.somnusmanager.events.events.QuietTimeEvent;
import com.yahoo.prosfis.somnusmanager.events.events.SecretsEvent;
import com.yahoo.prosfis.somnusmanager.events.events.TwerkContestEvent;

import net.md_5.bungee.api.ChatColor;

public class EventManager {

	private final SomnusManager sm;
	private final HashMap<EventType, Event> events;

	public EventManager(SomnusManager sm) {
		this.sm = sm;
		this.events = Maps.newHashMap();
		init();
		sm.getServer().getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				pickRandom();
			}
		}, 20 * 60 * 15);
	}

	private void init() {
		events.put(EventType.LOVE_LETTERS, new LoveLettersEvent(sm));
		events.put(EventType.TWERK_CONTEST, new TwerkContestEvent(sm));
		events.put(EventType.LOTTERY, new LotteryEvent(sm));
		events.put(EventType.QUIET_TIME, new QuietTimeEvent(sm));
		events.put(EventType.GO_TO_HELL, new GoToHellEvent(sm));
		events.put(EventType.MINE_CONTEST, new MineContestEvent(sm));
		events.put(EventType.MATHS, new MathsEvent(sm));
		events.put(EventType.SECRETS, new SecretsEvent(sm));
		new EventCommandExecutor(this, sm);
	}

	public void startEvent(EventType eventType) {
		events.get(eventType).start();
	}

	public void pickRandom() {
		Server server = sm.getServer();
		server.broadcastMessage(ChatColor.GOLD + "An event has been randomly chosen!");
		Event[] eventArray = events.values().toArray(new Event[events.size()]);
		eventArray[(int) (eventArray.length * Math.random())].start();
		server.getScheduler().runTaskLater(sm, new Runnable() {
			public void run() {
				pickRandom();
			}
		}, 20 * 60 * 30);
	}
}
