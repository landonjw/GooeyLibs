package ca.landonjw.gooeylibs.api.template;

import javax.annotation.Nonnull;

public enum TemplateType {
    CHEST("minecraft:container");

    private final String id;

    TemplateType(@Nonnull String id){
        this.id = id;
    }

    public String getID() {
        return id;
    }
}
