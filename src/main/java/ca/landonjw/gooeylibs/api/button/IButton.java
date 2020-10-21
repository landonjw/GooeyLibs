package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IButton {

    ItemStack getDisplay();

    default void onClick(@Nonnull ButtonAction action) {
    }

}