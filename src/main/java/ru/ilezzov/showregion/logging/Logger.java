package ru.ilezzov.showregion.logging;

import net.kyori.adventure.text.Component;

import java.util.List;

public interface Logger {
    void info(final String message);

    void info(final Component component);

    void info(final List<Component> components);

    void error(final String message);

    void error(final Component component);

}

