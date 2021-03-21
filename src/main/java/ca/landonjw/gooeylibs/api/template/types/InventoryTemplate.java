package ca.landonjw.gooeylibs.api.template.types;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.InventoryListenerButton;
import ca.landonjw.gooeylibs.api.template.LineType;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    public static class Builder extends ChestTemplate.Builder {

        public Builder() {
            super(4);
        }

        @Override
        public Builder set(int index, @Nullable Button button) {
            super.set(index, button);
            return this;
        }

        @Override
        public Builder set(int row, int col, @Nullable Button button) {
            super.set(row, col, button);
            return this;
        }

        @Override
        public Builder row(int row, @Nullable Button button) {
            super.row(row, button);
            return this;
        }

        @Override
        public Builder column(int col, @Nullable Button button) {
            super.column(col, button);
            return this;
        }

        @Override
        public Builder line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nullable Button button) {
            super.line(lineType, startRow, startCol, length, button);
            return this;
        }

        @Override
        public Builder square(int startRow, int startCol, int size, @Nullable Button button) {
            super.rectangle(startRow, startCol, size, size, button);
            return this;
        }

        @Override
        public Builder rectangle(int startRow, int startCol, int length, int width, @Nullable Button button) {
            super.rectangle(startRow, startCol, length, width, button);
            return this;
        }

        @Override
        public Builder border(int startRow, int startCol, int length, int width, @Nullable Button button) {
            super.border(startRow, startCol, length, width, button);
            return this;
        }

        @Override
        public Builder checker(int startRow, int startCol, int length, int width, @Nullable Button button, @Nullable Button button2) {
            super.checker(startRow, startCol, length, width, button, button2);
            return this;
        }

        @Override
        public Builder fill(@Nullable Button button) {
            super.fill(button);
            return this;
        }

        @Override
        public InventoryTemplate build() {
            return new InventoryTemplate(toSlots());
        }

    }

}
