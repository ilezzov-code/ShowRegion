package ru.ilezzov.showregion.placeholder;

import lombok.Getter;
import ru.ilezzov.showregion.Main;

import java.util.HashMap;

@Getter
public class PluginPlaceholder {
    private final HashMap<String, Object> placeholders;

    public PluginPlaceholder() {
        this.placeholders = new HashMap<>();
        this.placeholders.put("{P}", Main.getPrefix());
    }

    public void addPlaceholder(final String placeholder, final Object value) {
        this.placeholders.put(placeholder, value);
    }
}
