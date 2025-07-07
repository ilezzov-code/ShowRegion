package ru.ilezzov.showregion.database.data.player;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;

import java.util.UUID;

@Getter
public class PlayerData {
    private final UUID uuid;
    private final String displayName;

    private boolean enableShowing;
    private boolean enableActionBar;
    private boolean enableBossBar;

    @Setter
    private BossBar bossBar;

    private boolean dirty = false;

    public PlayerData(UUID uuid, String displayName,  boolean enableShowing, boolean enableActionBar, boolean enableBossBar) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.enableShowing = enableShowing;
        this.enableActionBar = enableActionBar;
        this.enableBossBar = enableBossBar;
    }

    public void setEnableShowing(final boolean enableShowing) {
        this.enableShowing = enableShowing;
        this.dirty = true;
    }

    public void setEnableActionBar(final boolean enableActionBar) {
        this.enableActionBar = enableActionBar;
        this.dirty = true;
    }

    public void setEnableBossBar(final boolean enableBossBar) {
        this.enableBossBar = enableBossBar;
        this.dirty = true;
    }
}
