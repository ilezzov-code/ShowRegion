package ru.ilezzov.showregion.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.api.region.CurrentRegion;
import ru.ilezzov.showregion.api.region.RegionType;
import ru.ilezzov.showregion.database.data.player.PlayerDataRepository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ImpShowRegionApi implements ShowRegionApi{
    private final WorldGuard worldGuard = Main.getWorldGuard();
    private final PlayerDataRepository playerDataRepository = Main.getPlayerDataRepository();

    @Override
    public CurrentRegion getRegion(final Player player) {
        final Location location = player.getLocation();

        final RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        final RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

        if (regionManager == null) {
            return new CurrentRegion("freeRegion", RegionType.FREE, null);
        }

        final ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));

        if (regions.size() == 0) {
            return new CurrentRegion("freeRegion", RegionType.FREE, null);
        }

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final ProtectedRegion protectedRegion = regions.getRegions().stream().max(Comparator.comparing(ProtectedRegion::getPriority))
                .orElse(null);

        final DefaultDomain owners = protectedRegion.getOwners();
        if (protectedRegion.isMember(localPlayer) || protectedRegion.isOwner(localPlayer)) {
            return new CurrentRegion(protectedRegion.getId(), RegionType.YOUR, getPlayersNames(owners));
        }

        return new CurrentRegion(protectedRegion.getId(), RegionType.FOREIGN,getPlayersNames(owners));
    }

    @Override
    public CompletableFuture<Integer> toggle(final Player player) {
        return playerDataRepository.get(player.getUniqueId()).thenApplyAsync(playerData -> {
            if (playerData.isEnableShowing()) {
                playerData.setEnableShowing(false);
                player.hideBossBar(playerData.getBossBar());
                return 0;
            } else {
                playerData.setEnableShowing(true);
                return 1;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> toggleBossBar(final Player player) {
        return playerDataRepository.get(player.getUniqueId()).thenApplyAsync(playerData -> {
            if (playerData.isEnableShowing()) {
                if (playerData.isEnableBossBar()) {
                    playerData.setEnableBossBar(false);
                    player.hideBossBar(playerData.getBossBar());
                    return 0;
                } else {
                    playerData.setEnableBossBar(true);
                    return 1;
                }
            } else {
                playerData.setEnableShowing(true);
                playerData.setEnableBossBar(true);
                return 1;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> toggleActionBar(final Player player) {
        return playerDataRepository.get(player.getUniqueId()).thenApplyAsync(playerData -> {
            if (playerData.isEnableShowing()) {
                if (playerData.isEnableActionBar()) {
                    playerData.setEnableActionBar(false);
                    return 0;
                } else {
                    playerData.setEnableActionBar(true);
                    return 1;
                }
            } else {
                playerData.setEnableShowing(true);
                playerData.setEnableActionBar(true);
                return 1;
            }
        });
    }

    private List<String> getPlayersNames(final DefaultDomain owners) {
        return owners.getPlayerDomain().getUniqueIds().stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .toList();
    }
}
