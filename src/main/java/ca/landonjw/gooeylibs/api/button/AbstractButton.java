package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public abstract class AbstractButton implements IButton {

    private final ItemStack display;

    protected AbstractButton(@Nonnull ItemStack display) {
        this.display = requireNonNull(display);
    }

    @Override
    public ItemStack getDisplay() {
        return display;
    }

}