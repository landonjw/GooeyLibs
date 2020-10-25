package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class InventoryListenerButton extends PlaceholderButton {

    public InventoryListenerButton(@Nonnull Button button) {
        super(button);
    }

    public InventoryListenerButton() {
        super(GooeyButton.builder()
                .display(ItemStack.EMPTY)
                .build()
        );
    }

}