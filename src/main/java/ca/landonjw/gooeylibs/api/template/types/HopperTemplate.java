package ca.landonjw.gooeylibs.api.template.types;

import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.TemplateType;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;

import javax.annotation.Nonnull;

public class HopperTemplate extends Template {

    protected HopperTemplate(@Nonnull TemplateSlot[] slots) {
        super(TemplateType.HOPPER, slots);
    }

}
