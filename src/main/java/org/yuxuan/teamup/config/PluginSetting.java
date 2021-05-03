package org.yuxuan.teamup.config;

import org.bukkit.configuration.file.FileConfiguration;

public abstract class PluginSetting {
	public FileConfiguration config;

	public PluginSetting(FileConfiguration config) {
		this.config = config;
	}

	public abstract void save();
}
