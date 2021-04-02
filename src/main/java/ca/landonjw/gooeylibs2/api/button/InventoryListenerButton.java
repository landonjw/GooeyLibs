package ca.landonjw.gooeylibs2.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class InventoryListenerButton extends ButtonBase {

    private final Consumer<ButtonAction> onClick;

    public InventoryListenerButton(Consumer<ButtonAction> onClick) {
        super(ItemStack.EMPTY);
        this.onClick = onClick;
    }

    @Override
    public void onClick(@Nonnull ButtonAction action) {
        onClick.accept(action);
    }

}