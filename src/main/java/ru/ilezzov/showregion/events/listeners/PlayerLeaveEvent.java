package ru.ilezzov.showregion.events.listeners;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.database.data.player.PlayerDataRepository;

import java.util.UUID;

public class PlayerLeaveEvent implements Listener {
    private final PlayerDataRepository playerDataRepository = Main.getPlayerDataRepository();

    @EventHandler
    public void onPlayerLeaveEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUniqueId = player.getUniqueId();

        playerDataRepository.get(playerUniqueId).thenAccept(data -> {
            if (data == null) {
                return;
            }

            final BossBar bossBar = data.getBossBar();
            if (bossBar != null) {
                player.hideBossBar(bossBar);
            }

            playerDataRepository.queueSave(data, true);
        });
    }
}
