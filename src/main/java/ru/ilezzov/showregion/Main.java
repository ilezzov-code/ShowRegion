package ru.ilezzov.showregion;

import com.sk89q.worldguard.WorldGuard;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.showregion.api.ImpShowRegionApi;
import ru.ilezzov.showregion.api.ShowRegionApi;
import ru.ilezzov.showregion.database.DatabaseType;
import ru.ilezzov.showregion.database.SQLDatabase;
import ru.ilezzov.showregion.database.adapter.MySQLDatabase;
import ru.ilezzov.showregion.database.adapter.PostgreSQLDatabase;
import ru.ilezzov.showregion.database.adapter.SQLiteDatabase;
import ru.ilezzov.showregion.database.data.player.PlayerData;
import ru.ilezzov.showregion.database.data.player.PlayerDataRepository;
import ru.ilezzov.showregion.events.EventManager;
import ru.ilezzov.showregion.file.PluginFile;
import ru.ilezzov.showregion.file.config.Config;
import ru.ilezzov.showregion.logging.Logger;
import ru.ilezzov.showregion.logging.PaperLogger;
import ru.ilezzov.showregion.managers.ShowingManager;
import ru.ilezzov.showregion.managers.VersionManager;
import ru.ilezzov.showregion.managers.regions.RegionManager;
import ru.ilezzov.showregion.messages.ConsoleMessages;
import ru.ilezzov.showregion.settings.PluginSettings;
import ru.ilezzov.showregion.stats.PluginStats;
import ru.ilezzov.showregion.utils.ListUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static ru.ilezzov.showregion.commands.CommandManager.loadCommands;
import static ru.ilezzov.showregion.messages.ConsoleMessages.*;

public final class Main extends JavaPlugin {
    // API
    @Getter
    private static Main instance;
    @Getter
    private static ShowRegionApi api;

    // WorldGuard
    @Getter
    private static WorldGuard worldGuard;

    // Logger
    @Getter
    private static Logger pluginLogger;

    // Settings
    @Getter
    private static PluginSettings pluginSettings;

    // Plugin info
    @Getter
    private static String prefix;
    @Getter
    private static String pluginVersion;
    @Getter
    private static String pluginContactLink;
    @Getter
    private static List<String> pluginDevelopers;
    @Getter
    private static boolean outdatedVersion;
    @Getter
    private static String messageLanguage;

    // Files
    private static PluginFile configYamlFile;
    @Getter
    private static PluginFile messagesYamlFile;
    @Getter
    private static PluginFile databaseFile;
    @Getter
    private static PluginFile regionSettings;

    // Config
    @Getter
    private static Config pluginConfig;

    // Managers
    @Getter
    private static VersionManager versionManager;
    @Getter
    private static RegionManager regionManager;
    @Getter
    private static ShowingManager showingManager;

    // Database
    @Getter
    private static SQLDatabase database;

    @Getter
    private static PlayerDataRepository playerDataRepository;

    // Events
    @Getter
    private static EventManager eventManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        pluginLogger = new PaperLogger(this);
        instance = this;

        // Load files
        loadSettings();
        loadFiles();

        checkWorldGuard();

        pluginConfig = new Config(configYamlFile);

        // Load plugin info
        loadPluginInfo();

        // Check plugin version
        checkPluginVersion();

        // Connect to the database
        createDatabase();

        try {
            database.connect();
            database.initialize();
            pluginLogger.info(successConnectToDatabase());
        } catch (SQLException | IOException e) {
            pluginLogger.info(errorOccurred(e.getMessage()));
            throw new RuntimeException(e);
        }

        // Load data repositories
        loadDataRepositories();
        insertAllPlayers();

        // Load API
        api = new ImpShowRegionApi();

        // Load managers
        loadManagers();

        // Load commands and events
        loadCommands();
        loadEvents();

        // Load Metrics
        loadMetrics();

        // Send enable message
        sendEnableMessage();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (database != null) {
            try {
                database.disconnect();
            } catch (SQLException e) {
                pluginLogger.info(ConsoleMessages.errorOccurred(e.getMessage()));
            }
        }
        if (playerDataRepository != null) {
            playerDataRepository.saveCache();
            playerDataRepository.flushQueue();
            playerDataRepository.stopAutoSave();
        }

        if (showingManager != null) {
            showingManager.stopShowingTask();
        }
    }

    public static void checkPluginVersion() {
        if (pluginConfig.checkUpdates()) {
            try {
                versionManager = new VersionManager(pluginVersion, pluginSettings.getUrlToFileVersion());

                if (versionManager.check()) {
                    pluginLogger.info(latestPluginVersion(pluginVersion));
                    outdatedVersion = false;
                } else {
                    pluginLogger.info(legacyPluginVersion(pluginVersion, versionManager.getCurrentPluginVersion(), pluginSettings.getUrlToDownloadLatestVersion()));
                    outdatedVersion = true;
                }
            } catch (URISyntaxException e) {
                pluginLogger.info(errorOccurred("Invalid link to the GitHub file. link = ".concat(versionManager.getUrlToFileVersion())));
            } catch (IOException | InterruptedException e ) {
                pluginLogger.info(errorOccurred("Couldn't send a request to get the plugin version"));
            }
        }
    }

    public static void createDatabase() {
        final ConfigurationSection section = databaseFile.getConfig().getConfigurationSection("Database");
        assert section != null;
        final String type = section.getString("type", "SQLITE");

        database = switch (type.toUpperCase()) {
            case "MYSQL" -> new MySQLDatabase(
                    section.getString("host"),
                    section.getInt("port"),
                    section.getString("database"),
                    section.getString("username"),
                    section.getString("password"),
                    DatabaseType.MYSQL
            );
            case "POSTGRESQL" -> new PostgreSQLDatabase(
                    section.getString("host"),
                    section.getInt("port"),
                    section.getString("database"),
                    section.getString("username"),
                    section.getString("password"),
                    DatabaseType.POSTGRESQL
            );
            default -> new SQLiteDatabase(new File(Main.getInstance().getDataFolder().getPath(), "data/database.db").getPath(), DatabaseType.SQLITE);
        };
    }

    private void loadSettings() {
        try {
            pluginSettings = new PluginSettings();
        } catch (IOException e) {
            pluginLogger.info(errorOccurred(e.getMessage()));
        }
    }

    private void loadFiles() {
        configYamlFile = new PluginFile(Main.getInstance(), "config.yml");
        messageLanguage = configYamlFile.getString("language");
        messagesYamlFile = new PluginFile(Main.getInstance(), "messages/".concat(messageLanguage).concat(".yml"));
        databaseFile = new PluginFile(Main.getInstance(), "data/database.yml");
        regionSettings = new PluginFile(Main.getInstance(), "region_settings.yml");
    }

    private void loadEvents() {
        eventManager = new EventManager(this);
        eventManager.loadEvents();
    }

    private void loadPluginInfo() {
        prefix = messagesYamlFile.getString("Plugin.plugin-prefix");
        pluginVersion = this.getDescription().getVersion();
        pluginDevelopers = this.getDescription().getAuthors();
        pluginContactLink = this.getDescription().getWebsite();
    }

    public static void reloadPluginInfo() {
        prefix = messagesYamlFile.getString("Plugin.plugin-prefix");
    }

    public static void reloadFiles() {
        configYamlFile.reload();
        pluginConfig.reload();

        final String messageLanguage = pluginConfig.language();

        if (!messageLanguage.equals(getMessageLanguage())) {
            messagesYamlFile = new PluginFile(Main.getInstance(), "messages/".concat(messageLanguage).concat(".yml"));
        } else {
            messagesYamlFile.reload();
        }

        databaseFile.reload();
        regionSettings.reload();
    }

    public static void insertAllPlayers() {
        final List<PlayerData> playerDataList = Bukkit.getOnlinePlayers().stream()
                .map(Main::getPlayerDataByPlayer)
                .toList();
        playerDataRepository.insertAll(playerDataList);
    }

    private static PlayerData getPlayerDataByPlayer(final Player player) {
        return null;
    }

    private void loadManagers() {
        regionManager = new RegionManager();
        showingManager = new ShowingManager(this);
    }

    private void loadMetrics() {
        new PluginStats(this);
    }

    private void sendEnableMessage() {
        pluginLogger.info(pluginEnable(ListUtils.listToString(getPluginDevelopers()), getPluginVersion(), getPluginContactLink()));
    }

    private void loadDataRepositories() {
        playerDataRepository = new PlayerDataRepository(database, this);
    }

    private void checkWorldGuard() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (plugin == null) {
            pluginLogger.info(ConsoleMessages.downloadWorldGuard());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        worldGuard = WorldGuard.getInstance();
        pluginLogger.info(ConsoleMessages.worldGuardConnected());
    }
}
