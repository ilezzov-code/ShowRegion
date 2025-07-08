package ru.ilezzov.showregion.api;

import org.bukkit.entity.Player;
import ru.ilezzov.showregion.api.region.CurrentRegion;

public interface ShowRegionApi {
    // Получить регион по локации игрока
    CurrentRegion getRegion(Player player);
}
