package ru.ilezzov.showregion.managers.regions;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.file.PluginFile;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RegionManager {
    private final PluginFile regionSetting = Main.getRegionSettings();

    private final HashMap<String, Region> customRegions = new HashMap<>();

    @Getter
    private Region freeRegion;
    @Getter
    private Region foreignRegion;
    @Getter
    private Region yourRegion;

    public RegionManager() {
        loadRegions();
    }

    public void reload() {
        customRegions.clear();
        loadRegions();
    }

    public Region getCustomRegion(final String regionName) {
        if (regionName == null || regionName.isBlank()) {
            return null;
        }

        return customRegions.get(regionName);
    }

    private void loadRegions() {
        final Configuration regionConfiguration = regionSetting.getConfig();
        final ConfigurationSection defaultRegions = regionConfiguration.getConfigurationSection("default_regions");
        final ConfigurationSection customRegions = regionConfiguration.getConfigurationSection("custom_regions");

        // Free region
        final ConfigurationSection freeRegionSection = defaultRegions.getConfigurationSection("free_region");
        freeRegion = new Region(
                "freeRegion",
                freeRegionSection.getString("action_bar"),
                freeRegionSection.getString("boss_bar.text"),
                (float) freeRegionSection.getDouble("boss_bar.progress"),
                BossBar.Color.valueOf(freeRegionSection.getString("boss_bar.color")),
                BossBar.Overlay.valueOf(freeRegionSection.getString("boss_bar.overlay"))
        );

        // Foreign region
        final ConfigurationSection foreignRegionSection = defaultRegions.getConfigurationSection("foreign_region");
        foreignRegion = new Region(
                "foreignRegion",
                foreignRegionSection.getString("action_bar"),
                foreignRegionSection.getString("boss_bar.text"),
                (float) foreignRegionSection.getDouble("boss_bar.progress"),
                BossBar.Color.valueOf(foreignRegionSection.getString("boss_bar.color")),
                BossBar.Overlay.valueOf(foreignRegionSection.getString("boss_bar.overlay"))
        );

        // Your region
        final ConfigurationSection yourRegionSection = defaultRegions.getConfigurationSection("your_region");
        yourRegion = new Region(
                "yourRegion",
                yourRegionSection.getString("action_bar"),
                yourRegionSection.getString("boss_bar.text"),
                (float) yourRegionSection.getDouble("boss_bar.progress"),
                BossBar.Color.valueOf(yourRegionSection.getString("boss_bar.color")),
                BossBar.Overlay.valueOf(yourRegionSection.getString("boss_bar.overlay"))
        );

        // Custom regions
        final Set<String> customRegionNames = customRegions.getKeys(false);
        for (final String regionName : customRegionNames) {
            final ConfigurationSection customRegionSection = customRegions.getConfigurationSection(regionName);
            final Region region = new Region(
                    regionName,
                    customRegionSection.getString("action_bar"),
                    customRegionSection.getString("boss_bar.text"),
                    (float) customRegionSection.getDouble("boss_bar.progress"),
                    BossBar.Color.valueOf(customRegionSection.getString("boss_bar.color")),
                    BossBar.Overlay.valueOf(customRegionSection.getString("boss_bar.overlay"))
            );
            this.customRegions.put(regionName, region);
        }
    }
}
