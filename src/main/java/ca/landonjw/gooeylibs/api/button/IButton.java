package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IButton {

	ItemStack getDisplay();

	void onClick(@Nonnull ButtonAction action);

}