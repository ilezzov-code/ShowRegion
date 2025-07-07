package ru.ilezzov.showregion.file.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import ru.ilezzov.showregion.file.PluginFile;

public class Config {
    private String language;
    private boolean checkUpdates;
    private ConfigShowingSection showingSection;

    private final PluginFile configFile;

    public Config(final PluginFile configFile) {
        this.configFile = configFile;
        setValues();
    }

    public void reload() {
        this.configFile.reload();
        setValues();
    }

    public String language() {
        return language;
    }

    public boolean checkUpdates() {
        return checkUpdates;
    }

    public PluginFile configFile() {
        return configFile;
    }

    public ConfigShowingSection showingSection() {
        return showingSection;
    }

    private static final class ConfigShowingSection{
        private boolean defaultEnable;
        private boolean enableBossBar;
        private boolean enableActionBar;
        private long tickRate;

        public boolean defaultEnable() {
            return defaultEnable;
        }

        public boolean enableBossBar() {
            return enableBossBar;
        }

        public boolean enableActionBar() {
            return enableActionBar;
        }

        public long tickRate() {
            return tickRate;
        }
    }

    private void setValues() {
        this.language = configFile.getString("language");
        this.checkUpdates = configFile.getBoolean("check_updates");

        final ConfigurationSection configurationSection = configFile.getConfig().getConfigurationSection("showing_settings");
        assert configurationSection != null;

        this.showingSection = new ConfigShowingSection();
        this.showingSection.defaultEnable = configurationSection.getBoolean("default_enable");
        this.showingSection.enableBossBar = configurationSection.getBoolean("enable_boss_bar");
        this.showingSection.enableActionBar = configurationSection.getBoolean("enable_action_bar");
        this.showingSection.tickRate = configurationSection.getLong("tick_rate");
    }
}
