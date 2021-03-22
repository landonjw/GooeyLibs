package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.ButtonAction;
import ca.landonjw.gooeylibs.api.button.ButtonClick;
import ca.landonjw.gooeylibs.api.button.moveable.MoveableButton;
import ca.landonjw.gooeylibs.api.button.moveable.MoveableButtonAction;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.page.PageAction;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.TemplateType;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;
import ca.landonjw.gooeylibs.api.template.types.InventoryTemplate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

public class GooeyContainer extends Container {

	private static final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
	private final EntityPlayerMP player;

	private Page page;
	public InventoryTemplate inventoryTemplate;

	private long lastClickTick;
	private boolean closing;

	public GooeyContainer(@Nonnull EntityPlayerMP player, @Nonnull Page page) {
		this.player = player;
		this.windowId = 1;

		this.page = page;
		initializePage(page);
		subscribeToPage(page);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void subscribeToPage(Page page) {
		page.subscribe(this, (update) -> {
			initializePage(update);
			refreshContainer();
		});
	}

	private void initializePage(Page page) {
		this.inventorySlots.forEach((slot) -> {
			((TemplateSlot) slot).unsubscribe(this);
		});
		if (inventoryTemplate != null) {
			for (int i = 0; i < inventoryTemplate.getSize(); i++) {
				inventoryTemplate.getSlot(i).unsubscribe(this);
			}
		}
		this.inventorySlots = page.getTemplate().getSlots();
		this.inventoryItemStacks = page.getTemplate().getDisplayStacks();
		this.inventoryTemplate = page.getInventoryTemplate().orElse(null);
		this.inventorySlots.forEach((slot) -> {
			((TemplateSlot) slot).subscribe(this, () -> updateSlot(slot));
		});
		if (inventoryTemplate != null) {
			for (int i = 0; i < inventoryTemplate.getSize(); i++) {
				int index = i;
				int itemSlot = i + page.getTemplate().getSize();
				inventoryTemplate.getSlot(i).subscribe(this, () -> {
					updateSlotStack(index, getItemAtSlot(itemSlot), true);
				});
			}
		}
		this.page.getTemplate().subscribe(this, (template) -> {
			template.onUpdate(windowId, player);
		});
	}

	private void updateSlotStack(int index, ItemStack stack, boolean playerInventory) {
		if (playerInventory) {
			SPacketSetSlot setSlot = new SPacketSetSlot(windowId, this.inventorySlots.size() + index, stack);
			player.connection.sendPacket(setSlot);
		} else {
			SPacketSetSlot setSlot = new SPacketSetSlot(windowId, index, stack);
			player.connection.sendPacket(setSlot);
		}
	}

	private boolean isSlotInPlayerInventory(int slot) {
		int templateSize = page.getTemplate().getSize();
		return slot >= templateSize && slot - templateSize < inventoryTemplate.getSize();
	}

	private ItemStack getItemAtSlot(int slot) {
		if (slot == -999) {
			return ItemStack.EMPTY;
		} else if (isSlotInPlayerInventory(slot)) {
			int targetSlot = slot - page.getTemplate().getSize();
			if (inventoryTemplate != null) {
				return inventoryTemplate.getSlot(targetSlot).getStack();
			} else {
				//First 0-7 slots contain player's armor, hand slots, etc. So we offset for the inventory UI slots.
				int PLAYER_INVENTORY_SLOT_OFFSET = 9;
				return player.inventoryContainer.getSlot(targetSlot + PLAYER_INVENTORY_SLOT_OFFSET).getStack();
			}
		} else {
			return page.getTemplate().getSlot(slot).getStack();
		}
	}

	private Button getButton(int slot) {
		if (slot < 0) return null;

		//Check if it's player's inventory or UI slot
		if (slot >= page.getTemplate().getSize()) {

			int targetedPlayerSlotIndex = slot - page.getTemplate().getSize();

			if (inventoryTemplate != null) {
				return inventoryTemplate.getSlot(targetedPlayerSlotIndex).getButton().orElse(null);
			} else {
				return null;
			}
		} else {
			return page.getTemplate().getSlot(slot).getButton().orElse(null);
		}
	}

	private void updateSlot(@Nonnull Slot slot) {
		SPacketSetSlot setSlot = new SPacketSetSlot(windowId, slot.getSlotIndex(), slot.getStack());
		player.connection.sendPacket(setSlot);
	}

	public void open() {
		player.closeContainer();
		player.openContainer = this;
		player.currentWindowId = windowId;

		SPacketOpenWindow openWindow;
		if (page.getTemplate().getTemplateType() == TemplateType.CRAFTING_TABLE) {
			openWindow = new SPacketOpenWindow(
					player.currentWindowId,
					page.getTemplate().getTemplateType().getID(),
					new TextComponentString(page.getTitle())
			);
		} else {
			openWindow = new SPacketOpenWindow(
					player.currentWindowId,
					page.getTemplate().getTemplateType().getID(),
					new TextComponentString(page.getTitle()),
					page.getTemplate().getSize()
			);
		}
		player.connection.sendPacket(openWindow);

		updateAllContainerContents();
		page.onOpen(new PageAction(player, page));
	}

	private Button cursorButton;

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer playerSP) {
//		System.out.println(clickType + ":" + slot + ":" + dragType + ":" + (cursorButton == null));

		Button clickedButton = getButton(slot);
		if (clickedButton instanceof MoveableButton || cursorButton != null) {
			return handleMoveableButton(slot, dragType, clickType);
		}

		if (clickType == ClickType.PICKUP) {
			if (lastClickTick < server.getTickCounter() - 5) {
				SPacketSetSlot setClickedSlot = new SPacketSetSlot(windowId, slot, getItemAtSlot(slot));
				player.connection.sendPacket(setClickedSlot);
			} else {
				updateAllContainerContents();
			}
		} else if (clickType == ClickType.CLONE) {
			SPacketSetSlot setClickedSlot = new SPacketSetSlot(windowId, slot, getItemAtSlot(slot));
			player.connection.sendPacket(setClickedSlot);
		} else if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP_ALL) {
			if (lastClickTick == server.getTickCounter()) return ItemStack.EMPTY;
			updateAllContainerContents();
		} else if (clickType == ClickType.QUICK_CRAFT) {
			boolean playerInventory = slot >= inventorySlots.size();
			int targetSlot = (playerInventory) ? slot - inventorySlots.size() : slot;
			updateSlotStack(targetSlot, ItemStack.EMPTY, playerInventory);
			return ItemStack.EMPTY;
		}

		setPlayersCursor(ItemStack.EMPTY);
		lastClickTick = server.getTickCounter();

		ButtonClick buttonClickType = getButtonClickType(clickType, dragType);

		if (slot >= 0 && slot < inventorySlots.size()) {
			page.getTemplate().getSlot(slot).getButton().ifPresent((button) -> {
				button.onClick(new ButtonAction(player, buttonClickType, button, page.getTemplate(), page, slot));
			});
		} else {
			if (slot != -999) {
				InventoryTemplate invTemplate = page.getInventoryTemplate().orElse(null);
				if (invTemplate != null) {
					int targetSlot = slot - inventorySlots.size();
					invTemplate.getSlot(targetSlot).getButton().ifPresent(button -> {
						button.onClick(new ButtonAction(player, buttonClickType, button, invTemplate, page, targetSlot));
					});
				}
			}
		}
		return ItemStack.EMPTY;
	}

	private ButtonClick getButtonClickType(ClickType type, int dragType) {
		switch (type) {
			case PICKUP:
				return (dragType == 0) ? ButtonClick.LEFT_CLICK : ButtonClick.RIGHT_CLICK;
			case CLONE:
				return ButtonClick.MIDDLE_CLICK;
			case QUICK_MOVE:
				return (dragType == 0) ? ButtonClick.SHIFT_LEFT_CLICK : ButtonClick.SHIFT_RIGHT_CLICK;
			case THROW:
				return ButtonClick.THROW;
			default:
				return ButtonClick.OTHER;
		}
	}

	private ItemStack handleMoveableButton(int slot, int dragType, ClickType clickType) {
		if (clickType == ClickType.QUICK_CRAFT && slot == -999) {
			return ItemStack.EMPTY;
		}

		if (clickType == ClickType.PICKUP) {
			if (lastClickTick < server.getTickCounter() - 5) {
				SPacketSetSlot setClickedSlot = new SPacketSetSlot(windowId, slot, getItemAtSlot(slot));
				player.connection.sendPacket(setClickedSlot);
			} else {
				updateAllContainerContents();
			}
		} else if (clickType == ClickType.CLONE) {
			SPacketSetSlot setClickedSlot = new SPacketSetSlot(windowId, slot, getItemAtSlot(slot));
			player.connection.sendPacket(setClickedSlot);
		} else if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP_ALL) {
			if (lastClickTick == server.getTickCounter()) return ItemStack.EMPTY;
			updateAllContainerContents();
		}

		Template targetTemplate;
		int targetTemplateSlot;

		if (slot >= 0 && slot < inventorySlots.size()) {
			targetTemplate = page.getTemplate();
			targetTemplateSlot = slot;
		} else {
			InventoryTemplate invTemplate = page.getInventoryTemplate().orElse(null);
			if (invTemplate == null) return ItemStack.EMPTY;

			targetTemplate = invTemplate;
			targetTemplateSlot = slot - inventorySlots.size();
		}

		if (cursorButton == null) {
			if (slot == -999) return ItemStack.EMPTY;
			SPacketSetSlot setCursorSlot = new SPacketSetSlot(-1, 0, getItemAtSlot(slot));
			player.connection.sendPacket(setCursorSlot);

			Button clickedButton = getButton(slot);
			if (clickedButton == null) {
				return ItemStack.EMPTY;
			}

			ButtonClick click = getButtonClickType(clickType, dragType);
			MoveableButtonAction action = new MoveableButtonAction(player, click, clickedButton, targetTemplate, page, targetTemplateSlot);
			clickedButton.onClick(action);
			((MoveableButton) clickedButton).onPickup(action);

			if (!action.isCancelled()) {
				cursorButton = clickedButton;
				setButton(slot, null);

				// Clone needs to return empty ItemStack or it desyncs.
				if (clickType == ClickType.CLONE) {
					return ItemStack.EMPTY;
				}

				return cursorButton.getDisplay();
			} else {
				setPlayersCursor(ItemStack.EMPTY);
				updateSlotStack(targetTemplateSlot, clickedButton.getDisplay(), targetTemplate instanceof InventoryTemplate);
				return ItemStack.EMPTY;
			}
		} else {
			Button clickedButton = getButton(slot);

			// This prevents desync on double clicking when dropping
			if (clickType == ClickType.PICKUP_ALL || slot == -999) {
				setPlayersCursor(cursorButton.getDisplay());
				return ItemStack.EMPTY;
			}

			// Check for collision
			if (clickedButton != null) {
				setPlayersCursor(cursorButton.getDisplay()); // TODO: Test necessity
				return clickedButton != null ? clickedButton.getDisplay() : ItemStack.EMPTY;
			}

			ButtonClick click = getButtonClickType(clickType, dragType);
			MoveableButtonAction action = new MoveableButtonAction(player, click, cursorButton, targetTemplate, page, targetTemplateSlot);
			cursorButton.onClick(action);
			((MoveableButton) cursorButton).onDrop(action);

			if (!action.isCancelled()) {
				setButton(slot, cursorButton);
				cursorButton = null;
				setPlayersCursor(ItemStack.EMPTY); // TODO: Check necessity
				return ItemStack.EMPTY;
			} else {
				// Clone needs to return empty ItemStack or it desyncs.
				if (clickType == ClickType.CLONE) {
					return ItemStack.EMPTY;
				}

				setPlayersCursor(cursorButton.getDisplay());
				updateSlotStack(targetTemplateSlot, ItemStack.EMPTY, targetTemplate instanceof InventoryTemplate);
				return cursorButton.getDisplay();
			}
		}
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		if (page != this.page) {
			this.page.unsubscribe(this);
			this.page = page;

			initializePage(page);
			subscribeToPage(page);

			refreshContainer();

			page.onOpen(new PageAction(player, page));
		}
	}

	public void refreshContainer() {
		SPacketOpenWindow openWindow;
		if (page.getTemplate().getTemplateType() == TemplateType.CRAFTING_TABLE) {
			openWindow = new SPacketOpenWindow(
					player.currentWindowId,
					page.getTemplate().getTemplateType().getID(),
					new TextComponentString(page.getTitle())
			);
		} else {
			openWindow = new SPacketOpenWindow(
					player.currentWindowId,
					page.getTemplate().getTemplateType().getID(),
					new TextComponentString(page.getTitle()),
					page.getTemplate().getSize()
			);
		}
		player.connection.sendPacket(openWindow);
		updateAllContainerContents();
	}

	private void updateAllContainerContents() {
		player.sendAllContents(player.openContainer, inventoryItemStacks);

		/*
		 * Detects changes in the player's inventory and updates them. This is to prevent desyncs if a player
		 * gets items added to their inventory while in the user interface.
		 */
		player.inventoryContainer.detectAndSendChanges();
		if (inventoryTemplate != null) {
			player.sendAllContents(player.inventoryContainer, inventoryTemplate.getFullDisplay(player));
		} else {
			player.sendAllContents(player.inventoryContainer, player.inventoryContainer.inventoryItemStacks);
		}
	}

	private void setPlayersCursor(ItemStack stack) {
		SPacketSetSlot setCursorSlot = new SPacketSetSlot(-1, 0, stack);
		player.connection.sendPacket(setCursorSlot);
	}

	private void setButton(int slot, Button button) {
		if (slot < 0) return;

		//Check if it's player's inventory or UI slot
		if (slot >= page.getTemplate().getSize()) {
			if (inventoryTemplate != null) {
				int targetedPlayerSlotIndex = slot - page.getTemplate().getSize();

				inventoryTemplate.getSlot(targetedPlayerSlotIndex).setButton(button);
			}
		} else {
			page.getTemplate().getSlot(slot).setButton(button);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		if (closing) return;

		closing = true;
		page.onClose(new PageAction(player, page));
		page.unsubscribe(this);
		this.inventorySlots.forEach((slot) -> ((TemplateSlot) slot).unsubscribe(this));
		if (inventoryTemplate != null) {
			for (int i = 0; i < inventoryTemplate.getSize(); i++) {
				inventoryTemplate.getSlot(i).unsubscribe(this);
			}
		}
		MinecraftForge.EVENT_BUS.unregister(this);

		player.inventoryContainer.detectAndSendChanges();
		player.sendAllContents(player.inventoryContainer, player.inventoryContainer.inventoryItemStacks);
	}

}
