package ru.ilezzov.showregion.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.database.data.player.PlayerData;
import ru.ilezzov.showregion.database.data.player.PlayerDataRepository;
import ru.ilezzov.showregion.file.config.Config;

import java.util.UUID;

public class PlayerLoginEvent implements Listener {
    private final PlayerDataRepository playerDataRepository = Main.getPlayerDataRepository();
    private final Config.ConfigShowingSection showingSection = Main.getConfig().showingSection();

    @EventHandler
    public void onPlayerLoginEvent(final org.bukkit.event.player.PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUniqueId = player.getUniqueId();
        playerDataRepository.get(playerUniqueId).thenAccept(data -> {
            if (data == null) {
                final PlayerData playerData = createPlayerData(player);
                playerDataRepository.insert(playerUniqueId, playerData);
            }
        });
    }

    private PlayerData createPlayerData(final Player player) {
        return new PlayerData(
                player.getUniqueId(),
                player.getDisplayName(),
                showingSection.defaultEnable(),
                true,
                true
        );
    }
}
