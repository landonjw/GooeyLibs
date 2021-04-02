package ca.landonjw.gooeylibs2.api.template.types;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.TemplateType;
import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CraftingTableTemplate extends Template {

    protected CraftingTableTemplate(@Nonnull TemplateSlot[] slots) {
        super(TemplateType.CRAFTING_TABLE, slots);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public CraftingTableTemplate clone() {
        TemplateSlot[] clonedSlots = new TemplateSlot[getSize()];
        for (int i = 0; i < getSize(); i++) {
            Button button = getSlot(i).getButton().orElse(null);
            clonedSlots[i] = new TemplateSlot(button, i, 0, 0);
        }
        return new CraftingTableTemplate(clonedSlots);
    }

    public static class Builder {

        private final Button[] buttons = new Button[10];

        public Builder set(int index, @Nullable Button button) {
            buttons[index] = button;
            return this;
        }

        public Builder setGrid(int row, int col, @Nullable Button button) {
            buttons[row * 3 + col + 1] = button;
            return this;
        }

        public Builder setResultItem(@Nullable Button button) {
            buttons[0] = button;
            return this;
        }

        public Builder fill(@Nullable Button button) {
            for (int i = 0; i < 10; i++) {
                buttons[i] = button;
            }
            return this;
        }

        public Builder fillGrid(@Nullable Button button) {
            for (int i = 1; i < 10; i++) {
                buttons[i] = button;
            }
            return this;
        }

        public CraftingTableTemplate build() {
            return new CraftingTableTemplate(toSlots());
        }

        protected TemplateSlot[] toSlots() {
            TemplateSlot[] slots = new TemplateSlot[10];
            for (int i = 0; i < 10; i++) {
                slots[i] = new TemplateSlot(buttons[i], i, 0, 0);
            }
            return slots;
        }

    }

}