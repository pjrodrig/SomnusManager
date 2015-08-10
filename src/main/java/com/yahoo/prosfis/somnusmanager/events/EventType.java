package com.yahoo.prosfis.somnusmanager.events;

import java.util.Map;

import com.google.common.collect.Maps;

public enum EventType {
	LOVE_LETTERS, TWERK_CONTEST, LOTTERY, QUIET_TIME, GO_TO_HELL, MINE_CONTEST;
	
	private final static Map<String, EventType> BY_NAME = Maps.newHashMap();
	
	static {
		for (EventType eType : values()) {
			BY_NAME.put(eType.name().toLowerCase(), eType);
		}
	}
	
	public static EventType getEvent(String event){
		return BY_NAME.get(event);
	}
}
