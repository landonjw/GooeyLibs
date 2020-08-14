package ca.landonjw.gooeylibs.api.template;

import ca.landonjw.gooeylibs.api.button.IButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Optional;

public interface ITemplate {

	int getSlots();

	Optional<IButton> getButton(int row, int col);

	Optional<IButton> getButton(int index);

	NonNullList<ItemStack> toContainerDisplay();

}