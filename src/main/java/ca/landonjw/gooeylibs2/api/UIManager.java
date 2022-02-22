package ca.landonjw.gooeylibs2.api;

import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.implementation.GooeyContainer;
import ca.landonjw.gooeylibs2.implementation.tasks.Task;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class UIManager {

    public static void openUIPassively(@Nonnull ServerPlayerEntity player, @Nonnull Page page, long timeout, TimeUnit timeoutUnit) {
        AtomicLong timeOutTicks = new AtomicLong(timeoutUnit.convert(timeout, TimeUnit.SECONDS) * 20);
        Task.builder()
                .execute((task) -> {
                    timeOutTicks.getAndDecrement();
                    if (player.openContainer == player.container || timeOutTicks.get() <= 0) {
                        openUIForcefully(player, page);
                        task.setExpired();
                    }
                })
                .infinite()
                .interval(1)
                .build();
    }

    public static void openUIForcefully(@Nonnull ServerPlayerEntity player, @Nonnull Page page) {
        // Delay the open to allow sponge's annoying mixins to process previous container and not have aneurysm
        Task.builder()
                .execute(() -> {
                    GooeyContainer container = new GooeyContainer(player, page);
                    container.open();
                })
                .build();
    }

    public static void closeUI(@Nonnull ServerPlayerEntity player) {
        Task.builder()
                .execute(() -> {
                    int windowId = player.openContainer == null ? 0 : player.openContainer.windowId;

                    CCloseWindowPacket pclient = new CCloseWindowPacket();
                    ObfuscationReflectionHelper.setPrivateValue(CCloseWindowPacket.class, pclient, windowId, "windowId");
                    SCloseWindowPacket pserver = new SCloseWindowPacket(windowId);

                    player.connection.processCloseWindow(pclient);
                    player.connection.sendPacket(pserver);
                })
                .build();
    }

}