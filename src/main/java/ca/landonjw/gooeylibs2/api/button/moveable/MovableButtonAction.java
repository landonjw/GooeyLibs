package ca.landonjw.gooeylibs2.api.button.moveable;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.ButtonAction;
import ca.landonjw.gooeylibs2.api.button.ButtonClick;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.Template;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;

public class MovableButtonAction extends ButtonAction {

    private boolean cancelled;

    public MovableButtonAction(@Nonnull ServerPlayer player,
                               @Nonnull ButtonClick clickType,
                               @Nonnull Button button,
                               @Nonnull Template template,
                               @Nonnull Page page, int slot) {
        super(player, clickType, button, template, page, slot);
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}