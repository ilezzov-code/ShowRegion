package ru.ilezzov.showregion.permission;

import lombok.Getter;

@Getter
public enum Permission {
    MAIN("showregion.*"),
    NO_COOLDOWN("showregion.no_cooldown"),
    RELOAD("showregion.reload"),
    ACCESS_SHOWING("showregion.access.showing"),
    TOGGLE_COMMAND("showregion.toggle"),
    TOGGLE_ALL_COMMAND("showregion.toggle.*"),
    TOGGLE_BOSSBAR_COMMAND("showregion.toggle.bossbar"),
    TOGGLE_ACTIONBAR_COMMAND("showregion.toggle.actionbar");

    private final String permission;

    Permission(final String permission) {
        this.permission = permission;
    }
}
