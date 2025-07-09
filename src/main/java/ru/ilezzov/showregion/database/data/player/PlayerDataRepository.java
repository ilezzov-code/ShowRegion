package ru.ilezzov.showregion.database.data.player;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.database.SQLDatabase;
import ru.ilezzov.showregion.database.data.DataRepository;
import ru.ilezzov.showregion.database.data.QueueManager;
import ru.ilezzov.showregion.database.data.Status;
import ru.ilezzov.showregion.logging.Logger;
import ru.ilezzov.showregion.messages.ConsoleMessages;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class PlayerDataRepository implements DataRepository<UUID, PlayerData>, QueueManager<PlayerData> {
    private final Logger logger = Main.getPluginLogger();
    private final SQLDatabase database;
    private final BukkitTask autoSaveTask;

    private final Queue<PlayerData> saveQueue = new ConcurrentLinkedQueue<>();
    private final Cache<UUID, PlayerData> cache = Caffeine.newBuilder().build();

    public PlayerDataRepository(final SQLDatabase database, final Plugin plugin) {
        this.database = database;
        this.autoSaveTask = startAutoSaveScheduler(plugin);
    }

    @Override
    public CompletableFuture<PlayerData> get(UUID key) {
        final PlayerData playerData = cache.getIfPresent(key);

        if (playerData != null) {
            return CompletableFuture.completedFuture(playerData);
        }

        return CompletableFuture.supplyAsync(() -> loadFromDatabase(key));
    }

    private PlayerData loadFromDatabase(final UUID key) {
        final String sql = "SELECT * FROM players WHERE uuid = ?";

        if (key == null) {
            return null;
        }

        try (final ResultSet resultSet = database.executePreparedQuery(sql, key)) {
            if (!resultSet.next()) {
                return null;
            }

            final String name = resultSet.getString("display_name");
            final boolean enableShowing = resultSet.getBoolean("enable_showing");
            final boolean enableActionbar = resultSet.getBoolean("enable_action_bar");
            final boolean enableBossBar = resultSet.getBoolean("enable_boss_bar");

            final PlayerData playerData = new PlayerData(
                    key,
                    name,
                    enableShowing,
                    enableActionbar,
                    enableBossBar
            );

            cache.put(key, playerData);
            return playerData;
        } catch (SQLException e) {
            logger.error(ConsoleMessages.databaseError(String.format("Failed to load player data for uuid %s \n Error: %s", key, e.getMessage())));
            return null;
        }
    }

    @Override
    public void insert(UUID key, PlayerData data) {
        CompletableFuture.runAsync(() -> {
            if (key == null) {
                return;
            }

            if (data == null) {
                return;
            }

            String sql = switch (database.getType()) {
                case SQLITE, POSTGRESQL -> "INSERT INTO players " +
                        "(uuid, display_name, enable_showing, enable_action_bar, enable_boss_bar) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "ON CONFLICT (uuid) DO NOTHING";
                case MYSQL -> "INSERT INTO players " +
                        "(uuid, display_name, enable_showing, enable_action_bar, enable_boss_bar) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE uuid = uuid";
            };

            final Object[] params = new Object[]{
                    data.getUuid(),
                    data.getDisplayName(),
                    data.isEnableShowing(),
                    data.isEnableActionBar(),
                    data.isEnableBossBar()
            };

            try {
                database.executePreparedUpdate(sql, params);
                cache.put(data.getUuid(), data);
            } catch (SQLException e) {
                logger.info(ConsoleMessages.errorOccurred("Failed to insert player data for uuid: " + data.getUuid() + "\n" + e.getMessage()));
            }
        });
    }

    @Override
    public void insertAll(Collection<? extends PlayerData> data) {
        CompletableFuture.runAsync(() -> {
            if (data == null || data.isEmpty()) {
                return;
            }

            String sql = switch (database.getType()) {
                case SQLITE, POSTGRESQL -> "INSERT INTO players " +
                        "(uuid, display_name, enable_showing, enable_action_bar, enable_boss_bar) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "ON CONFLICT (uuid) DO NOTHING";
                case MYSQL -> "INSERT INTO players " +
                        "(uuid, display_name, enable_showing, enable_action_bar, enable_boss_bar) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE uuid = uuid";
            };

            final List<Object[]> batchParams = new ArrayList<>(data.size());
            final List<PlayerData> playerDataList = new ArrayList<>(data);

            for (final PlayerData playerData : data) {
                batchParams.add(new Object[]{
                        playerData.getUuid(),
                        playerData.getDisplayName(),
                        playerData.isEnableShowing(),
                        playerData.isEnableActionBar(),
                        playerData.isEnableBossBar()
                });

                try {
                    final int[] affectedRows = database.executePreparedBatchUpdate(sql, batchParams);

                    for (int i = 0; i < affectedRows.length; i++) {
                        boolean wasInserted = affectedRows[i] == 1;

                        if (wasInserted) {
                            final PlayerData player = playerDataList.get(i);
                            cache.put(player.getUuid(), player);
                        }
                    }
                } catch (SQLException e) {
                    logger.info(ConsoleMessages.errorOccurred("Failed to insert player data: " + e.getMessage()));
                }
            }
        });
    }

    @Override
    public CompletableFuture<Status> delete(UUID key) {
        return CompletableFuture.supplyAsync(() -> {
            if (key == null) {
                return Status.ERROR;
            }

            if (get(key) == null) {
                return Status.NOT_FOUND;
            }

            final String sql = "DELETE FROM players WHERE uuid = ?";

            try {
                database.executePreparedUpdate(sql, key.toString());
                return Status.ACCESS;
            } catch (SQLException e) {
                logger.info(ConsoleMessages.errorOccurred("Failed to delete player data for uuid = " + key + "\n" + e.getMessage()));
                return Status.ERROR;
            }
        });
    };

    @Override
    public Map<UUID, PlayerData> asMap() {
        return cache.asMap();
    }

    @Override
    public void saveCache() {

        final List<PlayerData> batch = new ArrayList<>(cache.asMap().values());

        if (batch.isEmpty()) {
            return;
        }

        saveBatch(batch);
    }

    @Override
    public void queueSave(PlayerData value, boolean deleteFromCache) {
        if (value == null) {
            return;
        }

        if (deleteFromCache) {
            cache.invalidate(value.getUuid());
        }

        saveQueue.add(value);
    }

    @Override
    public void flushQueue() {
        final List<PlayerData> batch = new ArrayList<>();
        PlayerData data;

        while ((data = saveQueue.poll()) != null) {
            batch.add(data);
        }

        if (batch.isEmpty()) {
            return;
        }

        saveBatch(batch);
    }

    @Override
    public void stopAutoSave() {
        this.autoSaveTask.cancel();
    }

    private void saveBatch(final List<PlayerData> batch) {
        final String sql = "UPDATE players SET " +
                "display_name = ?, " +
                "enable_showing = ?, " +
                "enable_action_bar = ?, " +
                "enable_boss_bar = ?," +
                "WHERE uuid = ?";

        final List<Object[]> batchParams = new ArrayList<>(batch.size());

        for (final PlayerData playerData : batch) {
            batchParams.add(new Object[]{
                    playerData.getDisplayName(),
                    playerData.getDisplayName(),
                    playerData.isEnableShowing(),
                    playerData.isEnableActionBar(),
                    playerData.isEnableBossBar(),
                    playerData.getUuid()
            });
        }

        try {
            database.executePreparedBatchUpdate(sql, batchParams);
            logger.info(ConsoleMessages.saveQueue());
        } catch (SQLException e) {
            logger.info(ConsoleMessages.errorOccurred("Failed to update player data: " + e.getMessage()));
        }
    }

    private BukkitTask startAutoSaveScheduler(final Plugin plugin) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::flushQueue, 20L * 10, 20L * 10);
    }

}
