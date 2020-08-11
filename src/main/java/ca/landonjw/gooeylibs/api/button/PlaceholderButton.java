package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class PlaceholderButton extends AbstractButton {

	private static PlaceholderButton instance;

	private PlaceholderButton() {
		super(ItemStack.EMPTY);
	}

	public static PlaceholderButton getInstance() {
		if(instance == null) {
			instance = new PlaceholderButton();
		}
		return instance;
	}

	@Override
	public void onClick(@Nonnull ButtonAction action) {
	}

	@Override
	public Button clone() {
		return getInstance();
	}

}
