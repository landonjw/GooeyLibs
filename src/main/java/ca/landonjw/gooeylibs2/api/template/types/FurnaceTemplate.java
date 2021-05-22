package ca.landonjw.gooeylibs2.api.template.types;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.helpers.TemplateHelper;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.TemplateType;
import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlot;

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

    public FurnaceTemplate inputMaterial(@Nullable Button button) {
        getSlot(0).setButton(button);
        return this;
    }

    public FurnaceTemplate fuel(@Nullable Button button) {
        getSlot(1).setButton(button);
        return this;
    }

    public FurnaceTemplate outputMaterial(@Nullable Button button) {
        getSlot(2).setButton(button);
        return this;
    }

    public FurnaceTemplate clear() {
        for (int slotIndex = 0; slotIndex < getSize(); slotIndex++) {
            getSlot(slotIndex).setButton(null);
        }
        return this;
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
        private FurnaceTemplate templateInstance;

        public Builder() {
            this.templateInstance = new FurnaceTemplate(TemplateHelper.slotsOf(3));
        }

        public Builder inputMaterial(@Nullable Button button) {
            templateInstance.inputMaterial(button);
            return this;
        }

        public Builder fuel(@Nullable Button button) {
            templateInstance.fuel(button);
            return this;
        }

        public Builder outputMaterial(@Nullable Button button) {
            templateInstance.outputMaterial(button);
            return this;
        }

        public FurnaceTemplate build() {
            FurnaceTemplate templateToReturn = templateInstance;
            templateInstance = new FurnaceTemplate(TemplateHelper.slotsOf(3));
            return templateToReturn;
        }

    }

}
