package ca.landonjw.gooeylibs.api.page;

import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;

public class PageAction {

    private final EntityPlayerMP player;
    private final Page page;

    public PageAction(@Nonnull EntityPlayerMP player, @Nonnull Page page) {
        this.player = player;
        this.page = page;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }

    public Page getPage() {
        return page;
    }

}
