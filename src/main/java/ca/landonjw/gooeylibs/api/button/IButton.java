package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

public interface IButton {

	ItemStack getDisplay();

	void onClick(ButtonAction action);

	IButton clone();

}
