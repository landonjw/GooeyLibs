package ca.landonjw.gooeylibs.internal.inventory;

import ca.landonjw.gooeylibs.api.button.ButtonAction;
import ca.landonjw.gooeylibs.api.page.IPage;
import ca.landonjw.gooeylibs.api.page.PageAction;
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
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

public class GooeyContainer extends Container {

	private static MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

	private EntityPlayerMP player;

	private IPage page;

	private String title;
	private int slotsDisplayed;

	private long lastQuickMoveClickTick;
	private boolean closing;

	public GooeyContainer(@Nonnull EntityPlayerMP player, @Nonnull IPage page) {
		this.player = player;
		this.windowId = 1;

		this.page = page;
		ContainerUpdater.register(page, this);

		this.title = page.getTitle();
		this.slotsDisplayed = page.getTemplate().getSlots();
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

		player.sendAllContents(player.openContainer, page.getTemplate().toContainerDisplay());
		player.sendAllContents(player.inventoryContainer, player.inventoryContainer.inventoryItemStacks);
		page.onOpen(new PageAction(player, page));
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer playerSP) {
		if(clickType == ClickType.PICKUP_ALL) return ItemStack.EMPTY;

		// Updates both containers to prevent inventory desyncs
		render();

		if(clickType == ClickType.QUICK_MOVE) {
			player.connection.sendPacket(new SPacketSetSlot(-1, slot, ItemStack.EMPTY));
			// Used to prevent quick moves from propagating and invoking button calls.
			// Allows for one quick move to be invoked each tick on the container.
			if(lastQuickMoveClickTick == server.getTickCounter()) return ItemStack.EMPTY;
			lastQuickMoveClickTick = server.getTickCounter();
		}
		else if(clickType == ClickType.CLONE) {
			player.connection.sendPacket(new SPacketSetSlot(-1, slot, ItemStack.EMPTY));
		}

		//Invokes the button's behaviour if there is a valid button in the slot clicked.
		if(slot >= page.getTemplate().getSlots() || slot < 0) return ItemStack.EMPTY;

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

		player.sendAllContents(player.openContainer, page.getTemplate().toContainerDisplay());
		player.sendAllContents(player.inventoryContainer, player.inventoryContainer.inventoryItemStacks);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerSP) {
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		if(closing) return;

		closing = true;
		page.onClose(new PageAction(player, page));
		ContainerUpdater.unregister(page, this);
	}

}
