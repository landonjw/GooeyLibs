package ca.landonjw.gooeylibs2.api.page;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;

public class PageAction {

    private final ServerPlayer player;
    private final Page page;

    public PageAction(@Nonnull ServerPlayer player, @Nonnull Page page) {
        this.player = player;
        this.page = page;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public Page getPage() {
        return page;
    }

}
