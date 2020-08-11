package ca.landonjw.gooeylibs.internal.inventory;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.ButtonAction;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.page.PageAction;
import ca.landonjw.gooeylibs.api.template.Template;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;

import javax.annotation.Nonnull;

public class GooeyContainer extends Container {

	private EntityPlayerMP player;

	private Page page;
	private Template template;

	private boolean closing;

	public GooeyContainer(@Nonnull EntityPlayerMP player, @Nonnull Page page, int windowId) {
		this.player = player;
		this.page = page;
		this.template = page.getTemplate();

		initContainerSlots();
		initPlayerContainerSlots();
		initPlayerInventorySlots();
		this.windowId = windowId;
	}

	private void initContainerSlots() {
		for(int row = 0; row < template.getRows(); row++) {
			for(int col = 0; col < 9; col++) {
				GooeySlot slot = new GooeySlot(row, col, template.getButtons()[row][col]);
				addSlotToContainer(slot);
			}
		}
	}

	private void initPlayerContainerSlots() {
		int yAxisOffset = (template.getRows() - 4) * 18 + 103;
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 9; col++) {

				int slotIndex = row * 9 + col + 9;
				int slotXPos = 8 + col * 18;
				int slotYPos = row * 18 + yAxisOffset;

				Slot slot = new Slot(player.inventory, slotIndex, slotXPos, slotYPos);
				addSlotToContainer(slot);
			}
		}
	}

	private void initPlayerInventorySlots() {
		int yAxisOffset = (template.getRows() - 4) * 18 + 161;
		for(int col = 0; col < 9; col++) {
			addSlotToContainer(new Slot(player.inventory, col, 8 + col * 18, yAxisOffset));
		}
	}

	@Override
	protected Slot addSlotToContainer(Slot slot) {
		slot.slotNumber = this.inventorySlots.size();
		this.inventorySlots.add(slot);
		this.inventoryItemStacks.add(slot.getStack());
		return slot;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer playerSP) {
		if(clickType == ClickType.QUICK_MOVE || clickType == ClickType.CLONE) {
			player.connection.sendPacket(new SPacketSetSlot(windowId, slot, ItemStack.EMPTY));
		}

		if(slot < template.getRows() * 9 && slot >= 0) {
			Button button = template.getButtons()[slot / 9][slot % 9];
			button.onClick(new ButtonAction(player, clickType, button, page));
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerSP) {
		if(closing) return;

		closing = true;
		PageAction action = new PageAction(player, page);
		page.onClose(action);
	}

}
