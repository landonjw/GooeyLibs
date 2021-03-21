package ca.landonjw.gooeylibs.api.template.types;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.TemplateType;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FurnaceTemplate extends Template {

    public FurnaceTemplate(@Nonnull TemplateSlot[] slots) {
        super(TemplateType.FURNACE, slots);
    }

    public TemplateSlot getInputMaterial() {
        return getSlot(0);
    }

    public TemplateSlot getFuel() {
        return getSlot(1);
    }

    public TemplateSlot getOutputMaterial() {
        return getSlot(2);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public FurnaceTemplate clone() {
        TemplateSlot[] clonedSlots = new TemplateSlot[getSize()];
        for (int i = 0; i < getSize(); i++) {
            Button button = getSlot(i).getButton().orElse(null);
            clonedSlots[i] = new TemplateSlot(button, i, 0, 0);
        }
        return new FurnaceTemplate(clonedSlots);
    }

    public static class Builder {

        private final Button[] buttons = new Button[3];

        public Builder inputMaterial(@Nullable Button button) {
            buttons[0] = button;
            return this;
        }

        public Builder fuel(@Nullable Button button) {
            buttons[1] = button;
            return this;
        }

        public Builder outputMaterial(@Nullable Button button) {
            buttons[2] = button;
            return this;
        }

        public FurnaceTemplate build() {
            return new FurnaceTemplate(toSlots());
        }

        protected TemplateSlot[] toSlots() {
            TemplateSlot[] slots = new TemplateSlot[3];
            for (int i = 0; i < 3; i++) {
                slots[i] = new TemplateSlot(buttons[i], i, 0, 0);
            }
            return slots;
        }

    }

}
