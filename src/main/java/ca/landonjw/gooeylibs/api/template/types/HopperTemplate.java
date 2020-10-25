package ca.landonjw.gooeylibs.api.template.types;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.TemplateType;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HopperTemplate extends Template {

    protected HopperTemplate(@Nonnull TemplateSlot[] slots) {
        super(TemplateType.HOPPER, slots);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Button[] buttons = new Button[5];

        public Builder set(int index, @Nullable Button button) {
            buttons[index] = button;
            return this;
        }

        public HopperTemplate build() {
            return new HopperTemplate(toSlots());
        }

        protected TemplateSlot[] toSlots() {
            TemplateSlot[] slots = new TemplateSlot[5];
            for (int i = 0; i < 5; i++) {
                slots[i] = new TemplateSlot(buttons[i], i, 0, 0);
            }
            return slots;
        }

    }

}
