package org.yuxuan.teamup.config;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.yuxuan.teamup.PluginMain;

public class TeamupSetting extends PluginSetting {
	private final ConfigurationSection setting;

	public TeamupSetting() {
		super(PluginMain.getInstance().getConfig());

		this.setting = config.getConfigurationSection("teamup");
	}

	public List<String> getWorlds() {
		return setting.getStringList("worlds");
	}

	@Override
	public void save() {
		PluginMain.getInstance().saveConfig();
	}
}
