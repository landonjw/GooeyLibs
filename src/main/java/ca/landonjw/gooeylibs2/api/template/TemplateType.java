package ca.landonjw.gooeylibs2.api.template;

import javax.annotation.Nonnull;

public enum TemplateType {
    CHEST("minecraft:container"),
    FURNACE("minecraft:furnace"),
    BREWING_STAND("minecraft:brewing_stand"),
    HOPPER("minecraft:hopper"),
    DISPENSER("minecraft:dispenser"),
    CRAFTING_TABLE("minecraft:crafting_table");

    private final String id;

    TemplateType(@Nonnull String id) {
        this.id = id;
    }

    @Deprecated
    public String getID() {
        return id;
    }

}