package ru.ilezzov.showregion.api;

import org.bukkit.entity.Player;
import ru.ilezzov.showregion.api.region.CurrentRegion;

import java.util.concurrent.CompletableFuture;

public interface ShowRegionApi {
    CurrentRegion getRegion(Player player);

    CompletableFuture<Integer> toggle(Player player);

    CompletableFuture<Integer> toggleBossBar(Player player);

    CompletableFuture<Integer> toggleActionBar(Player player);
}
