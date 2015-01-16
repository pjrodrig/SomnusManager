package com.yahoo.prosfis.somnusmanager.util;

import java.util.concurrent.Callable;

import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class MetadataUtil {

	/*
	 * Converts value into LazyMetadataValue
	 * 
	 * @ensure return value as LazyMetaDataValue
	 */
	public static LazyMetadataValue makeSimpleCallable(final Object value, JavaPlugin plugin) {
		return new LazyMetadataValue(plugin, new Callable<Object>() {
			public Object call() throws Exception {
				return value;
			}
		});
	}
}