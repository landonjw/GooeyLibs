package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.button.ButtonAction;
import ca.landonjw.gooeylibs.api.button.IButton;
import ca.landonjw.gooeylibs.api.page.IPage;
import ca.landonjw.gooeylibs.api.page.PageAction;
import ca.landonjw.gooeylibs.api.template.ITemplate;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;
import ca.landonjw.gooeylibs.implementation.tasks.Task;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class GooeyContainer extends Container {

	private static final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
	private final EntityPlayerMP player;

	private IPage page;
	private ITemplate template;

	private NonNullList<ItemStack> stacksToDisplay;

	private long lastClickTick;
	private boolean closing;

	public GooeyContainer(@Nonnull EntityPlayerMP player, @Nonnull IPage page) {
		this.player = player;
		this.windowId = 1;

		this.page = page;
		this.inventorySlots.addAll(this.page.getTemplate().getSlots());

		subscribeToPage(page);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void subscribeToPage(IPage page) {
		page.subscribe(this, (update) -> {
			this.fillStacksToDisplay(update.getTemplate());
			this.unsubscribeToTemplateSlots(this.template);
			this.template = update.getTemplate();
			this.subscribeToTemplateSlots(page.getTemplate());
			this.refreshContainer();
		});
	}

	private void unsubscribeToPage(IPage page) {
		page.unsubscribe(this);
		unsubscribeToTemplateSlots(page.getTemplate());
	}

	private void fillStacksToDisplay(ITemplate template) {
		this.stacksToDisplay = template.getSlots().stream()
				.map(TemplateSlot::getStack)
				.collect(Collectors.toCollection(NonNullList::create));
	}

	private void subscribeToTemplateSlots(@Nonnull ITemplate template) {
		template.getSlots().forEach((slot) -> {
			slot.subscribe(this, (update) -> {
				SPacketSetSlot setSlot = new SPacketSetSlot(windowId, update.getSlotIndex(), update.getStack());
				player.connection.sendPacket(setSlot);
			});
		});
	}

	private void unsubscribeToTemplateSlots(@Nonnull ITemplate template) {
		template.getSlots().forEach((slot) -> slot.unsubscribe(this));
	}

	public void open() {
		player.closeContainer();
		player.openContainer = this;
		player.currentWindowId = windowId;

		SPacketOpenWindow openWindow = new SPacketOpenWindow(
				player.currentWindowId,
				"minecraft:container",
				new TextComponentString(page.getTitle()),
				page.getTemplate().getSize()
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

		page.getTemplate().getSlot(slot).getButton().ifPresent((button) -> {
			button.onClick(new ButtonAction(player, clickType, button, page));
		});
		return ItemStack.EMPTY;
	}

	public IPage getPage() {
		return page;
	}

	public void setPage(IPage page) {
		if(page != this.page) {
			unsubscribeToPage(page);
			this.page = page;
			subscribeToPage(page);

			refreshContainer();

			page.onOpen(new PageAction(player, page));
		}
	}

	public void refreshContainer() {
		SPacketOpenWindow openWindow = new SPacketOpenWindow(
				player.currentWindowId,
				"minecraft:container",
				new TextComponentString(page.getTitle()),
				page.getTemplate().getSize()
		);
		player.connection.sendPacket(openWindow);
	}

	private void updateAllContainerContents() {
		player.sendAllContents(player.openContainer, stacksToDisplay);

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
		if(slot >= page.getTemplate().getSize()) {
			//First 0-7 slots contain player's armor, hand slots, etc. So we offset for the inventory UI slots.
			int PLAYER_INVENTORY_SLOT_OFFSET = 9;
			int targetedPlayerSlotIndex = slot - page.getTemplate().getSize() + PLAYER_INVENTORY_SLOT_OFFSET;
			return player.inventoryContainer.getSlot(targetedPlayerSlotIndex).getStack();
		}
		else {
			return page.getTemplate().getSlot(slot).getStack();
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
		unsubscribeToPage(this.page);
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPickup(EntityItemPickupEvent event) {
		if(!event.getEntity().equals(player)) return;

		if(event.getResult() != Event.Result.DENY && !event.isCanceled()) {
			Task.builder().execute(this::updateAllContainerContents)
					.delay(1)
					.build();
		}
	}

}
