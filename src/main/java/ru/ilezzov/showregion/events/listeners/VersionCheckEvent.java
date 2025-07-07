package ru.ilezzov.showregion.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.managers.VersionManager;
import ru.ilezzov.showregion.permission.PermissionsChecker;
import ru.ilezzov.showregion.placeholder.PluginPlaceholder;

import static ru.ilezzov.showregion.Main.*;
import static ru.ilezzov.showregion.messages.PluginMessages.pluginHasErrorCheckVersionMessage;
import static ru.ilezzov.showregion.messages.PluginMessages.pluginUseOutdatedVersionMessage;

public class VersionCheckEvent implements Listener {
    private final PluginPlaceholder eventPlaceholders = new PluginPlaceholder();

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        if (!Main.getConfig().checkUpdates()) {
            return;
        }

        if (!isOutdatedVersion()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!PermissionsChecker.hasPermission(player)) {
            return;
        }

        final VersionManager versionManager = getVersionManager();

        if (versionManager == null) {
            player.sendMessage(pluginHasErrorCheckVersionMessage(eventPlaceholders));
            return;
        }

        eventPlaceholders.addPlaceholder("{OUTDATED_VERS}", getPluginVersion());
        eventPlaceholders.addPlaceholder("{LATEST_VERS}", versionManager.getCurrentPluginVersion());
        eventPlaceholders.addPlaceholder("{DOWNLOAD_LINK}", getPluginSettings().getUrlToDownloadLatestVersion());

        player.sendMessage(pluginUseOutdatedVersionMessage(eventPlaceholders));
    }
}
