package ru.ilezzov.showregion.managers.regions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;

@AllArgsConstructor
public class Region {
    private String name;
    private final String actionBarText;
    private final String bossBarText;
    private final float bossBarProgress;
    private final BossBar.Color bossBarColor;
    private final BossBar.Overlay bossBarOverlay;

    public String name() {
        return name;
    }

    public String actionBarText() {
        return actionBarText;
    }

    public String bossBarText() {
        return bossBarText;
    }

    public float bossBarProgress() {
        return bossBarProgress;
    }

    public BossBar.Color bossBarColor() {
        return bossBarColor;
    }

    public BossBar.Overlay bossBarOverlay() {
        return bossBarOverlay;
    }

    public Region setName(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "Region{" +
                "name='" + name + '\'' +
                ", actionBarText='" + actionBarText + '\'' +
                ", bossBarText='" + bossBarText + '\'' +
                ", bossBarProgress=" + bossBarProgress +
                ", bossBarColor=" + bossBarColor +
                ", bossBarOverlay=" + bossBarOverlay +
                '}';
    }
}
