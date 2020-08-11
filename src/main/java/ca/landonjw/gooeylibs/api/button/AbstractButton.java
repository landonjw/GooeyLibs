package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public abstract class AbstractButton implements Button {

	private ItemStack display;

	public AbstractButton(ItemStack display) {
		this.display = Objects.requireNonNull(display);
	}

	public abstract Button clone();

	@Override
	public ItemStack getDisplay() {
		return display;
	}

}
