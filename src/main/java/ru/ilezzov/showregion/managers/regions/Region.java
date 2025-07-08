package ru.ilezzov.showregion.managers.regions;

import net.kyori.adventure.bossbar.BossBar;

public record Region(String name, String actionBarText, String bossBarText, float bossBarProgress,
                     BossBar.Color bossBarColor, BossBar.Overlay bossBarOverlay) {
}
