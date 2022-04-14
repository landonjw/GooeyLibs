package ca.landonjw.gooeylibs2.api.button;

import ca.landonjw.gooeylibs2.api.data.Subject;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface Button extends Subject<Button> {

    ItemStack getDisplay();

    default void onClick(@Nonnull ButtonAction action) {
    }

}
