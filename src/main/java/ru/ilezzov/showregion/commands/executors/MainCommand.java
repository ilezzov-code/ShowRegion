package ru.ilezzov.showregion.commands.executors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.api.ShowRegionApi;
import ru.ilezzov.showregion.commands.CommandManager;
import ru.ilezzov.showregion.database.data.player.PlayerDataRepository;
import ru.ilezzov.showregion.file.config.Config;
import ru.ilezzov.showregion.managers.VersionManager;
import ru.ilezzov.showregion.messages.ConsoleMessages;
import ru.ilezzov.showregion.messages.PluginMessages;
import ru.ilezzov.showregion.permission.Permission;
import ru.ilezzov.showregion.permission.PermissionsChecker;
import ru.ilezzov.showregion.placeholder.PluginPlaceholder;
import ru.ilezzov.showregion.utils.LegacySerialize;
import ru.ilezzov.showregion.utils.ListUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ru.ilezzov.showregion.Main.*;
import static ru.ilezzov.showregion.permission.PermissionsChecker.hasPermission;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final PluginPlaceholder commandPlaceholders = new PluginPlaceholder();
    private final ShowRegionApi api = Main.getApi();
    private final Plugin plugin = Main.getInstance();

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String @NotNull [] args) {
        commandPlaceholders.addPlaceholder("{DEVELOPER}", ListUtils.listToString(getPluginDevelopers()));
        commandPlaceholders.addPlaceholder("{CONTACT_LINK}", getPluginContactLink());

        if (args.length == 0) {
            return handleHelp(sender);
        }

        return switch (args[0]) {
            case "reload" -> handleReload(sender);
            case "version" -> handleVersion(sender);
            case "toggle" -> handleToggle(sender, args);
            case "sql" -> handleSQL(sender, args);
            case "sql-result" -> handleSQLResult(sender, args);
            default -> handleHelp(sender);
        };
    }

    private boolean handleSQL(final CommandSender sender, final String[] args) {
        if (hasPermission(sender)) {
            try {
                Main.getDatabase().executeUpdate(getSqlUrl(args));
                sender.sendMessage(LegacySerialize.serialize("&7The request was &asuccessfully &7completed"));
            } catch (SQLException e) {
                sender.sendMessage(ConsoleMessages.errorOccurred(e.getMessage()));
            }
        }
        return true;
    }

    private boolean handleSQLResult(final CommandSender sender, final String[] args) {
        if (hasPermission(sender)) {
            try (final ResultSet resultSet = Main.getDatabase().executeQuery(getSqlUrl(args))) {
                sender.sendMessage(getSqlResponse(resultSet));
            } catch (SQLException e) {
                sender.sendMessage(ConsoleMessages.errorOccurred(e.getMessage()));
            }
        }
        return true;
    }

    private boolean handleHelp(final CommandSender sender) {
        sender.sendMessage(PluginMessages.commandMainCommandMessage(commandPlaceholders));
        return true;
    }

    private boolean handleReload(final CommandSender sender) {
        if (!hasPermission(sender, Permission.RELOAD)) {
            sender.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
            return true;
        }

        reloadFiles();
        reloadPluginInfo();

        checkPluginVersion();

        Main.insertAllPlayers();
        Main.getRegionManager().reload();
        
        CommandManager.loadCommands();
        getEventManager().reloadEvents();

        commandPlaceholders.addPlaceholder("{P}", getPrefix());
        sender.sendMessage(PluginMessages.pluginReloadMessage(commandPlaceholders));

        return true;
    }

    private boolean handleVersion(final CommandSender sender) {
        final VersionManager versionManager = getVersionManager();

        if (versionManager == null) {
            sender.sendMessage(PluginMessages.pluginHasErrorCheckVersionMessage(commandPlaceholders));
            return true;
        }

        commandPlaceholders.addPlaceholder("{LATEST_VERS}", versionManager.getCurrentPluginVersion());

        if (isOutdatedVersion()) {
            commandPlaceholders.addPlaceholder("{DOWNLOAD_LINK}", getPluginSettings().getUrlToDownloadLatestVersion());
            commandPlaceholders.addPlaceholder("{OUTDATED_VERS}", getPluginVersion());

            sender.sendMessage(PluginMessages.pluginUseOutdatedVersionMessage(commandPlaceholders));
            return true;
        }

        sender.sendMessage(PluginMessages.pluginUseLatestVersionMessage(commandPlaceholders));
        return true;
    }

    private boolean handleToggle(final CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PluginMessages.pluginNoConsoleMessage(commandPlaceholders));
            return true;
        }

        if (args.length == 1) {
            if (!PermissionsChecker.hasPermission(sender, Permission.TOGGLE_COMMAND, Permission.TOGGLE_ALL_COMMAND)) {
                sender.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
                return true;
            }

            api.toggle(player).thenAccept((status) -> Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(PluginMessages.commandToggleMessage(commandPlaceholders, status == 1))));
            return true;
        } else {
            switch (args[1].toLowerCase()) {
                case "actionbar" -> {
                    if (!PermissionsChecker.hasPermission(sender, Permission.TOGGLE_ACTIONBAR_COMMAND, Permission.TOGGLE_ALL_COMMAND)) {
                        sender.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
                        return true;
                    }
                    api.toggleActionBar(player).thenAccept((status) -> Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(PluginMessages.commandToggleActionBarMessage(commandPlaceholders, status == 1))));
                }
                case "bossbar" -> {
                    if (!PermissionsChecker.hasPermission(sender, Permission.TOGGLE_BOSSBAR_COMMAND, Permission.TOGGLE_ALL_COMMAND)) {
                        sender.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
                        return true;
                    }
                    api.toggleBossBar(player).thenAccept((status) -> Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(PluginMessages.commandToggleBossBarMessage(commandPlaceholders, status == 1))));
                }
                default -> player.sendMessage(PluginMessages.commandToggleHelpMessage(commandPlaceholders));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String @NotNull [] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("version");
            if (hasPermission(sender, Permission.RELOAD)) {
                completions.add("reload");
            }
            if (hasPermission(sender, Permission.TOGGLE_COMMAND, Permission.TOGGLE_ALL_COMMAND)) {
                completions.add("toggle");
            }
        }

        if (args.length == 2) {
            if (hasPermission(sender, Permission.TOGGLE_ACTIONBAR_COMMAND, Permission.TOGGLE_ALL_COMMAND)) {
                completions.add("actionbar");
            }

            if (hasPermission(sender, Permission.TOGGLE_BOSSBAR_COMMAND, Permission.TOGGLE_ALL_COMMAND)) {
                completions.add("bossbar");
            }
        }

        return completions;
    }

    private String getSqlUrl(final @NotNull String @NotNull [] args) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }

        return stringBuilder.toString();
    }

    private @NotNull String getSqlResponse(final ResultSet resultSet) throws SQLException {
        final StringBuilder sb = new StringBuilder();

        final ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            sb.append(metaData.getColumnName(i));
            if (i < columnCount) {
                sb.append(" | ");
            }
        }
        sb.append("\n");

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                sb.append(resultSet.getString(i));
                if (i < columnCount) {
                    sb.append(" | ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
