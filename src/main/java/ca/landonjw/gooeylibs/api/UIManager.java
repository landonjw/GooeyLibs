package ca.landonjw.gooeylibs.api;

import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.internal.inventory.GooeyContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class UIManager {

	private static int windowId;

	public static void openUIPassively(@Nonnull EntityPlayerMP player, @Nonnull Page page) {

	}

	public static void openUIForcefully(@Nonnull EntityPlayerMP player, @Nonnull Page page) {
		player.closeContainer();

		windowId = windowId % 100 + 1;
		GooeyContainer container = new GooeyContainer(player, page, windowId);
		player.openContainer = container;
		player.currentWindowId = windowId;
		for(int i = 0; i < container.inventorySlots.size(); i++) {
			player.connection.sendPacket(new SPacketWindowProperty(container.windowId, i, 0));
		}

		SPacketOpenWindow openWindow = new SPacketOpenWindow(
				player.currentWindowId,
				"minecraft:container",
				new TextComponentString(page.getTitle()),
				page.getTemplate().getRows() * 9
		);
		player.connection.sendPacket(openWindow);
		container.detectAndSendChanges();
		player.sendAllContents(container, container.inventoryItemStacks);
	}

	public static void closeUI(@Nonnull EntityPlayerMP player) {

	}

}
