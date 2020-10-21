package ca.landonjw.gooeylibs.api.template;

import ca.landonjw.gooeylibs.api.data.EventEmitter;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;
import net.minecraft.util.NonNullList;

public interface ITemplate {

    TemplateType getTemplateType();

    int getSize();

    TemplateSlot getSlot(int index);

    NonNullList<TemplateSlot> getSlots();

}