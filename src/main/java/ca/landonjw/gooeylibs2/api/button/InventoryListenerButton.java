package ca.landonjw.gooeylibs2.api.button;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class InventoryListenerButton extends ButtonBase {

    private final Consumer<ButtonAction> onClick;

    public InventoryListenerButton(@Nullable Consumer<ButtonAction> onClick) {
        super(ItemStack.EMPTY);
        this.onClick = onClick;
    }

    @Override
    public void onClick(@Nonnull ButtonAction action) {
        if (onClick != null) {
            onClick.accept(action);
        }
    }

}