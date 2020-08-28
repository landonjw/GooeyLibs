package ca.landonjw.gooeylibs.internal.inventory;

import ca.landonjw.gooeylibs.api.button.ButtonAction;
import ca.landonjw.gooeylibs.api.button.IButton;
import ca.landonjw.gooeylibs.api.page.IPage;
import ca.landonjw.gooeylibs.api.page.PageAction;
import ca.landonjw.gooeylibs.internal.tasks.Task;
import ca.landonjw.gooeylibs.internal.updates.ContainerUpdater;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

public class GooeyContainer extends Container {

	private static MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

	//First 0-7 slots contain player's armor, hand slots, etc. So we offset for the inventory UI slots.
	private final int PLAYER_INVENTORY_SLOT_OFFSET = 9;

	private EntityPlayerMP player;

	private IPage page;

	private String title;
	private int slotsDisplayed;

	private long lastClickTick;
	private boolean closing;

	public GooeyContainer(@Nonnull EntityPlayerMP player, @Nonnull IPage page) {
		this.player = player;
		this.windowId = 1;

		this.page = page;
		ContainerUpdater.register(page, this);

		this.title = page.getTitle();
		this.slotsDisplayed = page.getTemplate().getSlots();
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void open() {
		player.closeContainer();
		player.openContainer = this;
		player.currentWindowId = windowId;

		SPacketOpenWindow openWindow = new SPacketOpenWindow(
				player.currentWindowId,
				"minecraft:container",
				new TextComponentString(page.getTitle()),
				page.getTemplate().getSlots()
		);
		player.connection.sendPacket(openWindow);

		updateAllContainerContents();
		page.onOpen(new PageAction(player, page));
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer playerSP) {
		if(clickType == ClickType.PICKUP) {
			if(lastClickTick < server.getTickCounter() - 5) {
				SPacketSetSlot setClickedSlot = new SPacketSetSlot(windowId, slot, getItemToSend(slot));
				player.connection.sendPacket(setClickedSlot);
			}
			else {
				updateAllContainerContents();
			}
		}
		else if(clickType == ClickType.CLONE) {
			SPacketSetSlot setClickedSlot = new SPacketSetSlot(windowId, slot, getItemToSend(slot));
			player.connection.sendPacket(setClickedSlot);
		}
		else if(clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP_ALL) {
			if(lastClickTick == server.getTickCounter()) return ItemStack.EMPTY;
			updateAllContainerContents();
		}

		clearPlayersCursor();
		lastClickTick = server.getTickCounter();

		page.getTemplate().getButton(slot).ifPresent((button) -> {
			button.onClick(new ButtonAction(player, clickType, button, page));
		});
		return ItemStack.EMPTY;
	}

	public IPage getPage() {
		return page;
	}

	public void setPage(IPage page) {
		if(page == this.page) {
			render();
		}
		else {
			ContainerUpdater.unregister(this.page, this);
			this.page = page;
			render();
			page.onOpen(new PageAction(player, page));
			ContainerUpdater.register(page, this);
		}
	}

	public void render() {
		boolean updateContainer = false;
		if(!title.equals(page.getTitle())) updateContainer = true;
		if(slotsDisplayed != page.getTemplate().getSlots()) updateContainer = true;

		if(updateContainer) {
			SPacketOpenWindow openWindow = new SPacketOpenWindow(
					player.currentWindowId,
					"minecraft:container",
					new TextComponentString(page.getTitle()),
					page.getTemplate().getSlots()
			);
			player.connection.sendPacket(openWindow);
		}

		this.title = page.getTitle();
		this.slotsDisplayed = page.getTemplate().getSlots();

		updateAllContainerContents();
	}

	private void updateAllContainerContents() {
		player.sendAllContents(player.openContainer, page.getTemplate().toContainerDisplay());

		/*
		 * Detects changes in the player's inventory and updates them. This is to prevent desyncs if a player
		 * gets items added to their inventory while in the user interface.
		 */
		player.inventoryContainer.detectAndSendChanges();
		player.sendAllContents(player.inventoryContainer, player.inventoryContainer.inventoryItemStacks);
	}

	private void clearPlayersCursor() {
		SPacketSetSlot setCursorSlot = new SPacketSetSlot(-1, 0, ItemStack.EMPTY);
		player.connection.sendPacket(setCursorSlot);
	}

	private ItemStack getItemToSend(int slot) {
		if(slot < 0) return ItemStack.EMPTY;

		//Check if it's player's inventory or UI slot
		if(slot >= page.getTemplate().getSlots()) {
			int targetedPlayerSlotIndex = slot - page.getTemplate().getSlots() + PLAYER_INVENTORY_SLOT_OFFSET;
			return player.inventoryContainer.getSlot(targetedPlayerSlotIndex).getStack();
		}
		else {
			IButton button = page.getTemplate().getButton(slot).orElse(null);
			return (button != null) ? button.getDisplay() : ItemStack.EMPTY;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		if(closing) return;

		closing = true;
		page.onClose(new PageAction(player, page));
		ContainerUpdater.unregister(page, this);
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPickup(EntityItemPickupEvent event) {
		if(!event.getEntity().equals(player)) return;

		if(event.getResult() != Event.Result.DENY && !event.isCanceled()) {
			Task.builder().execute(() -> updateAllContainerContents())
					.delay(1)
					.build();
		}
	}

}
