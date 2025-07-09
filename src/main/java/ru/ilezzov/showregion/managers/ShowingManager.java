package ru.ilezzov.showregion.managers;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.api.ShowRegionApi;
import ru.ilezzov.showregion.api.region.CurrentRegion;
import ru.ilezzov.showregion.database.data.player.PlayerData;
import ru.ilezzov.showregion.database.data.player.PlayerDataRepository;
import ru.ilezzov.showregion.file.config.Config;
import ru.ilezzov.showregion.managers.regions.Region;
import ru.ilezzov.showregion.managers.regions.RegionManager;
import ru.ilezzov.showregion.permission.Permission;
import ru.ilezzov.showregion.permission.PermissionsChecker;
import ru.ilezzov.showregion.placeholder.PluginPlaceholder;
import ru.ilezzov.showregion.utils.LegacySerialize;
import ru.ilezzov.showregion.utils.PlaceholderReplacer;

import java.util.*;

public class ShowingManager {
    private final Plugin plugin;
    private final ShowRegionApi api = Main.getApi();

    private final PlayerDataRepository playerDataRepository = Main.getPlayerDataRepository();
    private final Config.ConfigShowingSection showingSection = Main.getPluginConfig().showingSection();

    private final RegionManager regionManager = Main.getRegionManager();
    private final BukkitTask showingTask;

    public ShowingManager(final Plugin plugin) {
        this.plugin = plugin;
        this.showingTask = startShowingTask();
    }

    private BukkitTask startShowingTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                final Collection<? extends Player> playerList = Bukkit.getOnlinePlayers();

                for (final Player player : playerList) {
                    final UUID uuid = player.getUniqueId();

                    playerDataRepository.get(uuid).thenAccept(playerData -> {
                       if (playerData == null) {
                           return;
                       }

                       if (!PermissionsChecker.hasPermission(player, Permission.ACCESS_SHOWING)) {
                           return;
                       }

                       if (!playerData.isEnableShowing()) {
                           return;
                       }

                       if (!playerData.isEnableBossBar() && !playerData.isEnableActionBar()) {
                           return;
                       }

                       final CurrentRegion currentRegion = api.getRegion(player);
                       final String currentRegionName = currentRegion.regionName();
                       Region region = regionManager.getCustomRegion(currentRegionName);

                       if (region == null) {
                            region = switch (currentRegion.regionType()) {
                                case FREE -> regionManager.getFreeRegion();
                                case FOREIGN -> regionManager.getForeignRegion().setName(currentRegionName);
                                case YOUR -> regionManager.getYourRegion().setName(currentRegionName);
                            };
                       }

                       if (playerData.isEnableActionBar()) {
                           showActionBar(player, region, currentRegion.regionOwners());
                       }

                       if (playerData.isEnableBossBar()) {
                           showBossBar(player, playerData, region, currentRegion.regionOwners());
                       }
                   });
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, showingSection.tickRate());
    }

    public void stopShowingTask() {
        final Map<UUID, PlayerData> playerDataMap = playerDataRepository.asMap();

        for (final PlayerData playerData : playerDataMap.values()) {
            final Player player = Bukkit.getPlayer(playerData.getUuid());

            if (player != null) {
                player.hideBossBar(playerData.getBossBar());
            }
        }

        this.showingTask.cancel();
    }

    private void showActionBar(final Player player, final Region region, final List<String> owners) {
        if (!showingSection.enableActionBar()) {
            return;
        }

        final PluginPlaceholder placeholder = new PluginPlaceholder();
        placeholder.addPlaceholder("{REGION_NAME}", region.name());
        placeholder.addPlaceholder("{REGION_OWNER}", getOwnersString(owners));

        final String message = PlaceholderReplacer.replacePlaceholder(region.actionBarText(), placeholder);
        Bukkit.getScheduler().runTask(plugin, () -> player.sendActionBar(LegacySerialize.serialize(message)));
    }

    private void showBossBar(final Player player, final PlayerData playerData, final Region region, final List<String> owners) {
        final BossBar playerDataBossBar = playerData.getBossBar();

        if (!showingSection.enableBossBar()) {
            if (playerDataBossBar != null) {
                player.hideBossBar(playerDataBossBar);
                playerData.setBossBar(null);
            }
            return;
        }

        final PluginPlaceholder placeholder = new PluginPlaceholder();
        placeholder.addPlaceholder("{REGION_NAME}", region.name());
        placeholder.addPlaceholder("{REGION_OWNER}", getOwnersString(owners));

        final String message = PlaceholderReplacer.replacePlaceholder(region.bossBarText(), placeholder);
        final Component component = LegacySerialize.serialize(message);

        if (playerDataBossBar == null) {
            final BossBar bossBar = BossBar.bossBar(component, region.bossBarProgress(), region.bossBarColor(), region.bossBarOverlay());
            playerData.setBossBar(bossBar);
            player.showBossBar(bossBar);
            return;
        }

        playerDataBossBar.name(component);
        playerDataBossBar.progress(region.bossBarProgress());
        playerDataBossBar.color(region.bossBarColor());
        playerDataBossBar.overlay(region.bossBarOverlay());
    }

    private String getOwnersString(final List<String> owners) {
        if (owners == null || owners.isEmpty()) {
            return "";
        }

        final int countOwners = Main.getPluginConfig().showingSection().ownerCount();
        final int totalOwners = owners.size();

        if (totalOwners > countOwners) {
            final List<String> firstOwners = owners.subList(0, countOwners);
            return String.join(", ", firstOwners);
        } else {
            return String.join(", ", owners);
        }
    }

}
