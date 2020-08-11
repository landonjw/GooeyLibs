package ca.landonjw.gooeylibs.internal.inventory;

import ca.landonjw.gooeylibs.api.button.Button;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GooeySlot extends Slot {

	private Button button;

	public GooeySlot(int row, int col, Button button) {
		super(null, row * 9 + col, 8 + col * 18, 18 + row * 18);
		this.button = button;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	@Override
	public ItemStack getStack() {
		return (button != null) ? button.getDisplay() : ItemStack.EMPTY;
	}

}
