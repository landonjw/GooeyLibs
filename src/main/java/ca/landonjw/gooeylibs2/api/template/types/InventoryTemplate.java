package ca.landonjw.gooeylibs2.api.template.types;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.InventoryListenerButton;
import ca.landonjw.gooeylibs2.api.helpers.TemplateHelper;
import ca.landonjw.gooeylibs2.api.template.LineType;
import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlot;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class InventoryTemplate extends ChestTemplate {

    protected InventoryTemplate(@Nonnull TemplateSlot[] slots) {
        super(slots);
    }

    public ItemStack getDisplayForSlot(@Nonnull EntityPlayerMP player, int index) {
        TemplateSlot slot = getSlot(index);
        if (slot.getButton().isPresent() && slot.getButton().get() instanceof InventoryListenerButton) {
            return player.inventoryContainer.inventoryItemStacks.get(index + 9);
        } else {
            return slot.getStack();
        }
    }

    @Override
    public InventoryTemplate clone() {
        TemplateSlot[] clonedSlots = new TemplateSlot[getSize()];
        for (int i = 0; i < getSize(); i++) {
            int row = i / 9;
            int col = i % 9;

            Button button = getSlot(i).getButton().orElse(null);
            clonedSlots[i] = new TemplateSlot(button, col + row * 9, 8 + col * 18, 18 + row * 18);
        }
        return new InventoryTemplate(clonedSlots);
    }

    @Deprecated
    public NonNullList<ItemStack> getFullDisplay(@Nonnull EntityPlayerMP player) {
        NonNullList<ItemStack> displays = NonNullList.create();

        int PLAYER_INVENTORY_OFFSET = 8;
        for (int i = 0; i <= PLAYER_INVENTORY_OFFSET; i++) displays.add(ItemStack.EMPTY);
        for (int i = 0; i < getSize(); i++) {
            displays.add(getDisplayForSlot(player, i));
        }
        return displays;
    }

    public static Builder builder() {
        return new Builder();
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
        private InventoryTemplate templateInstance;

        public Builder() {
            this.templateInstance = new InventoryTemplate(TemplateHelper.slotsOf(4 * COLUMNS));
        }

        public Builder set(int index, @Nullable Button button) {
            templateInstance.set(index, button);
            return this;
        }

        public Builder set(int row, int col, @Nullable Button button) {
            templateInstance.set(row, col, button);
            return this;
        }

        public Builder row(int row, @Nullable Button button) {
            templateInstance.row(row, button);
            return this;
        }

        public Builder rowFromList(int row, @Nonnull List<Button> buttons) {
            templateInstance.rowFromList(row, buttons);
            return this;
        }

        public Builder column(int col, @Nullable Button button) {
            templateInstance.column(col, button);
            return this;
        }

        public Builder columnFromList(int col, @Nonnull List<Button> buttons) {
            templateInstance.columnFromList(col, buttons);
            return this;
        }

        public Builder line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nullable Button button) {
            templateInstance.line(lineType, startRow, startCol, length, button);
            return this;
        }

        public Builder lineFromList(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nonnull List<Button> buttons) {
            templateInstance.lineFromList(lineType, startRow, startCol, length, buttons);
            return this;
        }

        public Builder square(int startRow, int startCol, int size, @Nullable Button button) {
            templateInstance.square(startRow, startCol, size, button);
            return this;
        }

        public Builder squareFromList(int startRow, int startCol, int size, @Nonnull List<Button> buttons) {
            templateInstance.squareFromList(startRow, startCol, size, buttons);
            return this;
        }

        public Builder rectangle(int startRow, int startCol, int length, int width, @Nullable Button button) {
            templateInstance.rectangle(startRow, startCol, length, width, button);
            return this;
        }

        public Builder rectangleFromList(int startRow, int startCol, int length, int width, @Nonnull List<Button> buttons) {
            templateInstance.rectangleFromList(startRow, startCol, length, width, buttons);
            return this;
        }

        public Builder border(int startRow, int startCol, int length, int width, @Nullable Button button) {
            templateInstance.border(startRow, startCol, length, width, button);
            return this;
        }

        public Builder borderFromList(int startRow, int startCol, int length, int width, @Nonnull List<Button> buttons) {
            templateInstance.borderFromList(startRow, startCol, length, width, buttons);
            return this;
        }

        public Builder checker(int startRow, int startCol, int length, int width, @Nullable Button button, @Nullable Button button2) {
            templateInstance.checker(startRow, startCol, length, width, button, button2);
            return this;
        }

        public Builder checkerFromList(int startRow, int startCol, int length, int width, @Nonnull List<Button> buttons, @Nonnull List<Button> buttons2) {
            templateInstance.checkerFromList(startRow, startCol, length, width, buttons, buttons2);
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

        public InventoryTemplate build() {
            InventoryTemplate templateToReturn = templateInstance;
            templateInstance = new InventoryTemplate(TemplateHelper.slotsOf(4 * COLUMNS));
            return templateToReturn;
        }

    }

}