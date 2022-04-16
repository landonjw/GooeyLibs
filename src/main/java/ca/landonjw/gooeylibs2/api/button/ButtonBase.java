package ca.landonjw.gooeylibs2.api.button;

import ca.landonjw.gooeylibs2.api.data.UpdateEmitter;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public abstract class ButtonBase extends UpdateEmitter<Button> implements Button {

    private ItemStack display;

    protected ButtonBase(@Nonnull ItemStack display) {
        this.display = requireNonNull(display);
    }

    public final ItemStack getDisplay() {
        return display;
    }

    public void setDisplay(@Nonnull ItemStack display) {
        this.display = display;
        update();
    }

}