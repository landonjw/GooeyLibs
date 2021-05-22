package ca.landonjw.gooeylibs2.api.template.types;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.helpers.TemplateHelper;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.TemplateType;
import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class HopperTemplate extends Template {

    protected HopperTemplate(@Nonnull TemplateSlot[] slots) {
        super(TemplateType.HOPPER, slots);
    }

    public static Builder builder() {
        return new Builder();
    }

    public HopperTemplate set(int index, @Nullable Button button) {
        getSlot(index).setButton(button);
        return this;
    }

    public HopperTemplate fill(@Nullable Button button) {
        for (int i = 0; i < getSize(); i++) {
            if (!getSlot(i).getButton().isPresent()) {
                getSlot(i).setButton(button);
            }
        }
        return this;
    }

    public HopperTemplate fillFromList(@Nonnull List<Button> buttons) {
        Iterator<Button> iterator = buttons.iterator();
        for (int i = 0; i < getSize(); i++) {
            if (!getSlot(i).getButton().isPresent()) {
                getSlot(i).setButton((iterator.hasNext()) ? iterator.next() : null);
            }
        }
        return this;
    }

    public HopperTemplate clear() {
        for (int slotIndex = 0; slotIndex < getSize(); slotIndex++) {
            getSlot(slotIndex).setButton(null);
        }
        return this;
    }

    @Override
    public HopperTemplate clone() {
        TemplateSlot[] clonedSlots = new TemplateSlot[getSize()];
        for (int i = 0; i < getSize(); i++) {
            Button button = getSlot(i).getButton().orElse(null);
            clonedSlots[i] = new TemplateSlot(button, i, 0, 0);
        }
        return new HopperTemplate(clonedSlots);
    }

    public static class Builder {

        /**
         * Instance of the template being built.
         * <p>
         * In 2.1.0, all template builders were moved from storing data instances (ie. button arrays)
         * of templates and constructing them on build, to simply delegating to an instance of the template.
         * <p>
         * This was done in order to shift all convenience methods (ie. row, column, border, etc.) into the
         * corresponding Template classes themselves to allow for easier modification of button state after
         * the Template was built.
         * <p>
         * Since we assign a new template instance at the end of each {@link #build()},
         * this should not have any side effects and thus be backwards compatible.
         * <p>
         * Yay for abstraction!
         */
        private HopperTemplate templateInstance;

        public Builder() {
            this.templateInstance = new HopperTemplate(TemplateHelper.slotsOf(5));
        }

        public Builder set(int index, @Nullable Button button) {
            templateInstance.set(index, button);
            return this;
        }

        public Builder fill(@Nullable Button button) {
            templateInstance.fill(button);
            return this;
        }

        public Builder fillFromList(@Nonnull List<Button> buttons) {
            templateInstance.fillFromList(buttons);
            return this;
        }

        public HopperTemplate build() {
            HopperTemplate templateToReturn = templateInstance;
            templateInstance = new HopperTemplate(TemplateHelper.slotsOf(5));
            return templateToReturn;
        }

    }

}
