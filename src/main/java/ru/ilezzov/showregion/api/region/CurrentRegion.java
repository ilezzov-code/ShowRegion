package ru.ilezzov.showregion.api.region;

import java.util.List;
import java.util.Set;

public record CurrentRegion(String regionName, RegionType regionType, List<String> regionOwners) { }
