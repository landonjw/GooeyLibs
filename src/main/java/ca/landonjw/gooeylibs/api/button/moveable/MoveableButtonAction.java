package ca.landonjw.gooeylibs.api.button.moveable;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.ButtonAction;
import ca.landonjw.gooeylibs.api.button.ButtonClick;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.template.Template;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;

public class MoveableButtonAction extends ButtonAction {

    private boolean cancelled;

    public MoveableButtonAction(@Nonnull EntityPlayerMP player,
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
