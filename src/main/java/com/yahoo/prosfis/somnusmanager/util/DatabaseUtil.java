package com.yahoo.prosfis.somnusmanager.util;

public class DatabaseUtil {

	public static String prepareString(String string) {
		string = string.replace("\\", "\\\\");
		string = string.replace("'", "\\'");
		string = string.replace("\"", "\\\"");
		string = string.replace("	", "\\t");
		string = string.replace("%", "\\%");
		string = string.replace("_", "\\_");
		return string;
	}

	public static String reversePrepareString(String string) {
		string = string.replace("\\'", "'");
		string = string.replace("\\\"", "\"");
		string = string.replace("\\t", "	");
		string = string.replace("\\%", "%");
		string = string.replace("\\_", "_");
		string = string.replace("\\\\", "\\");
		return string;
	}
}
