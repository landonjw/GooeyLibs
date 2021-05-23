package ca.landonjw.gooeylibs2.api.page;

import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nonnull;

public class PageAction {

    private final ServerPlayerEntity player;
    private final Page page;

    public PageAction(@Nonnull ServerPlayerEntity player, @Nonnull Page page) {
        this.player = player;
        this.page = page;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public Page getPage() {
        return page;
    }

}
