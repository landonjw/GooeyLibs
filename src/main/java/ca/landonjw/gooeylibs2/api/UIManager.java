package ca.landonjw.gooeylibs2.api;

import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.implementation.tasks.Task;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class UIManager {

    public static void openUIPassively(@Nonnull ServerPlayer player, @Nonnull Page page, long timeout, TimeUnit timeoutUnit) {
        AtomicLong timeOutTicks = new AtomicLong(timeoutUnit.convert(timeout, TimeUnit.SECONDS) * 20);
        Task.builder()
                .execute((task) -> {
                    timeOutTicks.getAndDecrement();
                    if (player.containerMenu == player.inventoryMenu || timeOutTicks.get() <= 0) {
                        openUIForcefully(player, page);
                        task.setExpired();
                    }
                })
                .infinite()
                .interval(1)
                .build();
    }

    public static void openUIForcefully(@Nonnull ServerPlayer player, @Nonnull Page page) {
        // Delay the open to allow sponge's annoying mixins to process previous container and not have aneurysm
        Task.builder()
                .execute(() -> {
                    Page.open(player, page);
                })
                .build();
    }

    public static void closeUI(@Nonnull ServerPlayer player) {
        Task.builder()
                .execute(() -> {
                    player.closeContainer();
                    int windowId = player.containerMenu.containerId;

                    ServerboundContainerClosePacket pclient = new ServerboundContainerClosePacket(windowId);
                    ClientboundContainerClosePacket pserver = new ClientboundContainerClosePacket(windowId);

                    player.connection.handleContainerClose(pclient);
                    player.connection.send(pserver);
                })
                .build();
    }

}