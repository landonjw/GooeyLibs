package ca.landonjw.gooeylibs.api.template.types;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.TemplateType;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DispenserTemplate extends Template {

    protected DispenserTemplate(@Nonnull TemplateSlot[] slots) {
        super(TemplateType.DISPENSER, slots);
    }

    public TemplateSlot getSlot(int row, int col) {
        return getSlot(row * 3 + col);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public DispenserTemplate clone() {
        TemplateSlot[] clonedSlots = new TemplateSlot[getSize()];
        for (int i = 0; i < getSize(); i++) {
            Button button = getSlot(i).getButton().orElse(null);
            clonedSlots[i] = new TemplateSlot(button, i, 0, 0);
        }
        return new DispenserTemplate(clonedSlots);
    }

    public static class Builder {

        private final Button[] buttons = new Button[9];

        public Builder set(int index, @Nullable Button button) {
            buttons[index] = button;
            return this;
        }

        public Builder set(int row, int col, @Nullable Button button) {
            return set(row * 3 + col, button);
        }

        public Builder fill(@Nullable Button button) {
            for (int i = 0; i < 9; i++) {
                buttons[i] = button;
            }
            return this;
        }

        public DispenserTemplate build() {
            return new DispenserTemplate(toSlots());
        }

        protected TemplateSlot[] toSlots() {
            TemplateSlot[] slots = new TemplateSlot[9];
            for (int i = 0; i < 9; i++) {
                slots[i] = new TemplateSlot(buttons[i], i, 0, 0);
            }
            return slots;
        }

    }

}
