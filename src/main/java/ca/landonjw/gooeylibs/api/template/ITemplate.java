package ca.landonjw.gooeylibs.api.template;

import ca.landonjw.gooeylibs.api.button.IButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.Optional;

public interface ITemplate {

	int getSlots();

	Optional<IButton> getButton(int row, int col);

	Optional<IButton> getButton(int index);

	void setButton(int row, int col, @Nullable IButton button);

	void setButton(int index, @Nullable IButton button);

	NonNullList<ItemStack> toContainerDisplay();

}