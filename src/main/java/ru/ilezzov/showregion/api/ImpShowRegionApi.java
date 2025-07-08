package ru.ilezzov.showregion.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.ilezzov.showregion.Main;
import ru.ilezzov.showregion.api.region.CurrentRegion;
import ru.ilezzov.showregion.api.region.RegionType;

import java.util.Comparator;

public class ImpShowRegionApi implements ShowRegionApi{
    private final WorldGuard worldGuard = Main.getWorldGuard();

    @Override
    public CurrentRegion getRegion(final Player player) {
        final Location location = player.getLocation();

        final RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        final RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

        if (regionManager == null) {
            return new CurrentRegion("freeRegion", RegionType.FREE, null);
        }

        final ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));

        if (regions.size() == 0) {
            return new CurrentRegion("freeRegion", RegionType.FREE, null);
        }

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final ProtectedRegion protectedRegion = regions.getRegions().stream().max(Comparator.comparing(ProtectedRegion::getPriority))
                .orElse(null);

        final DefaultDomain owners = protectedRegion.getOwners();
        if (protectedRegion.isMember(localPlayer) || protectedRegion.isOwner(localPlayer)) {
            return new CurrentRegion(protectedRegion.getId(), RegionType.YOUR, owners.getPlayers());
        }

        return new CurrentRegion(protectedRegion.getId(), RegionType.FOREIGN, owners.getPlayers());
    }
}
