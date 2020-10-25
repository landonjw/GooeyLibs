package ca.landonjw.gooeylibs.api.template;

import javax.annotation.Nonnull;

public enum TemplateType {
    CHEST("minecraft:container"),
    FURNACE("minecraft:furnace"),
    BREWING_STAND("minecraft:brewing_stand"),
    HOPPER("minecraft:hopper"),
    DISPENSER("minecraft:dispenser");

    private final String id;

    TemplateType(@Nonnull String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }
}
