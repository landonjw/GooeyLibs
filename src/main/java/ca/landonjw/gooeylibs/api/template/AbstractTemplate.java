package ca.landonjw.gooeylibs.api.template;

import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.Arrays;

public abstract class AbstractTemplate implements ITemplate {

    protected final TemplateType templateType;
    private final NonNullList<TemplateSlot> slots;

    protected AbstractTemplate(@Nonnull TemplateType templateType, @Nonnull TemplateSlot[] slots) {
        this.templateType = templateType;
        this.slots = NonNullList.create();
        this.slots.addAll(Arrays.asList(slots));
    }

    @Override
    public TemplateType getTemplateType() {
        return templateType;
    }

    @Override
    public int getSize() {
        return slots.size();
    }

    @Override
    public TemplateSlot getSlot(int index) {
        return slots.get(index);
    }

    @Override
    public NonNullList<TemplateSlot> getSlots() {
        NonNullList<TemplateSlot> copy = NonNullList.create();
        copy.addAll(this.slots);
        return copy;
    }

}
