package ca.landonjw.gooeylibs.api;

import ca.landonjw.gooeylibs.api.page.IPage;
import ca.landonjw.gooeylibs.implementation.GooeyContainer;
import ca.landonjw.gooeylibs.implementation.tasks.Task;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;

public class UIManager {

	public static void openUIPassively(@Nonnull EntityPlayerMP player, @Nonnull IPage page) {
		Task.builder()
				.execute(() -> openUIForcefully(player, page))
				.delay(1)
				.build();
	}

	public static void openUIForcefully(@Nonnull EntityPlayerMP player, @Nonnull IPage page) {
		if(player.openContainer instanceof GooeyContainer) {
			((GooeyContainer) player.openContainer).setPage(page);
			return;
		}

		GooeyContainer container = new GooeyContainer(player, page);
		container.open();
	}

	public static void closeUI(@Nonnull EntityPlayerMP player) {
		Task.builder()
				.execute(() -> {
					int windowId = player.openContainer == null ? 0 : player.openContainer.windowId;

					CPacketCloseWindow pclient = new CPacketCloseWindow();
					ObfuscationReflectionHelper.setPrivateValue(CPacketCloseWindow.class, pclient, windowId, 0);
					SPacketCloseWindow pserver = new SPacketCloseWindow(windowId);

					player.connection.processCloseWindow(pclient);
					player.connection.sendPacket(pserver);
				})
				.build();
	}

}
