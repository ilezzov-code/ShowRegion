package ru.ilezzov.showregion.messages;

import net.kyori.adventure.text.Component;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.file.PluginFile;
import ru.ilezzov.showregion.placeholder.PluginPlaceholder;
import ru.ilezzov.showregion.utils.LegacySerialize;
import ru.ilezzov.showregion.utils.PlaceholderReplacer;

public class PluginMessages {
    private static PluginFile getMessages() {
        return Main.getMessagesYamlFile();
    }

    public static Component pluginUseLatestVersionMessage(final PluginPlaceholder placeholder) {
        return getComponent("Plugin.plugin-use-latest-version", placeholder);
    }

    public static Component pluginUseOutdatedVersionMessage(final PluginPlaceholder placeholder) {
        return getComponent("Plugin.plugin-use-outdated-version", placeholder);
    }

    public static Component pluginHasErrorMessage(final PluginPlaceholder placeholder) {
        return getComponent("Plugin.plugin-has-error", placeholder);
    }

    public static Component pluginHasErrorCheckVersionMessage(final PluginPlaceholder placeholder) {
        return getComponent("Plugin.plugin-has-error-check-version", placeholder);
    }

    public static Component pluginReloadMessage(final PluginPlaceholder placeholder) {
        return getComponent("Plugin.plugin-reload", placeholder);
    }

    public static Component pluginNoPermsMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.command-permission-error", placeholder);
    }

    public static Component pluginNoConsoleMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.command-no-console-error", placeholder);
    }

    public static Component pluginCommandCooldownMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.command-cooldown", placeholder);
    }

    public static Component commandMainCommandMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.command-main-message", placeholder);
    }

    public static Component commandDisableCommandMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.command-disable", placeholder);
    }

    public Component commandToggleMessage(final PluginPlaceholder placeholder, final boolean isEnable) {
        if (isEnable)
            return getComponent("Messages.command-toggle-enable", placeholder);
        return getComponent("Messages.command-toggle-disable", placeholder);
    }

    public Component commandToggleBossBarMessage(final PluginPlaceholder placeholder, final boolean isEnable) {
        if (isEnable)
            return getComponent("Messages.command-toggle-bossbar-enable", placeholder);
        return getComponent("Messages.command-toggle-bossbar-disable", placeholder);
    }

    public Component commandToggleActionBarMessage(final PluginPlaceholder placeholder, final boolean isEnable) {
        if (isEnable)
            return getComponent("Messages.command-toggle-actionbar-enable", placeholder);
        return getComponent("Messages.command-toggle-actionbar-disable", placeholder);
    }

    private static Component getComponent(final String key) {
        final String message = getMessages().getConfig().getString(key);

        return LegacySerialize.serialize(message);
    }

    private static Component getComponent(final String key, final PluginPlaceholder placeholder) {
        String message = getMessages().getString(key);
        message = replacePlaceholder(message, placeholder);

        return LegacySerialize.serialize(message);
    }

    private static String replacePlaceholder(final String message, final PluginPlaceholder placeholder) {
        return PlaceholderReplacer.replacePlaceholder(message, placeholder);
    }
}
