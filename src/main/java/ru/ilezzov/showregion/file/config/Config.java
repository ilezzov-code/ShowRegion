package ru.ilezzov.showregion.file.config;

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
        setValues();
        showingSection.reload();
    }

    public String language() {
        return language;
    }

    public boolean checkUpdates() {
        return checkUpdates;
    }

    public ConfigShowingSection showingSection() {
        return showingSection;
    }

    public static final class ConfigShowingSection{
        private boolean defaultEnable;
        private boolean enableBossBar;
        private boolean enableActionBar;
        private int ownerCount;
        private long tickRate;

        private final PluginFile configFile;

        public ConfigShowingSection(final PluginFile configFile) {
            this.configFile = configFile;
            setValues();
        }

        public boolean defaultEnable() {
            return defaultEnable;
        }

        public boolean enableBossBar() {
            return enableBossBar;
        }

        public boolean enableActionBar() {
            return enableActionBar;
        }

        public int ownerCount() {
            return ownerCount;
        }

        public long tickRate() {
            return tickRate;
        }

        public void reload() {
            setValues();
        }

        private void setValues() {
            final ConfigurationSection configurationSection = configFile.getConfig().getConfigurationSection("showing_settings");
            assert configurationSection != null;

            defaultEnable = configurationSection.getBoolean("default_enable");
            enableBossBar = configurationSection.getBoolean("enable_boss_bar");
            enableActionBar = configurationSection.getBoolean("enable_action_bar");
            ownerCount = configurationSection.getInt("owner_count");
            tickRate = configurationSection.getLong("tick_rate");
        }
    }

    private void setValues() {
        this.language = configFile.getString("language");
        this.checkUpdates = configFile.getBoolean("check_updates");
        this.showingSection = new ConfigShowingSection(configFile);
    }
}
