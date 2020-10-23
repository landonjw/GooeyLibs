package ca.landonjw.gooeylibs.api.template.types;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.TemplateType;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BrewingStandTemplate extends Template {

    protected BrewingStandTemplate(@Nonnull TemplateSlot[] slots) {
        super(TemplateType.BREWING_STAND, slots);
    }

    public TemplateSlot getFuel() {
        return getSlot(0);
    }

    public TemplateSlot getBottle(int index) {
        if (index < 0 || index > 3) return null;
        return getSlot(index + 1);
    }

    public TemplateSlot getIngredient() {
        return getSlot(4);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Button[] buttons = new Button[5];

        public Builder fuel(@Nullable Button button) {
            this.buttons[0] = button;
            return this;
        }

        public Builder ingredient(@Nullable Button button) {
            this.buttons[1] = button;
            return this;
        }

        public Builder bottle(int index, @Nullable Button button) {
            if (index >= 0 && index < 3) this.buttons[index + 2] = button;
            return this;
        }

        public Builder bottles(@Nullable Button button) {
            for (int i = 0; i < 3; i++) bottle(i, button);
            return this;
        }

        public BrewingStandTemplate build() {
            return new BrewingStandTemplate(toSlots());
        }

        private TemplateSlot[] toSlots() {
            TemplateSlot[] slots = new TemplateSlot[5];
            for (int i = 0; i < 5; i++) {
                slots[i] = new TemplateSlot(buttons[i], i, 0, 0);
            }
            return slots;
        }

    }

}
