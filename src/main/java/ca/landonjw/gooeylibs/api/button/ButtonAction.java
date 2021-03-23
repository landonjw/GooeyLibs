package ca.landonjw.gooeylibs.api.button;

import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.template.Template;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ButtonAction {

    private final EntityPlayerMP player;
    private final ButtonClick clickType;
    private final Button button;
    private final Template template;
    private final Page page;
    private final int slot;

    public ButtonAction(@Nonnull EntityPlayerMP player,
                        @Nonnull ButtonClick clickType,
                        @Nonnull Button button,
                        @Nonnull Template template,
                        @Nonnull Page page,
                        int slot) {
        this.player = Objects.requireNonNull(player);
        this.clickType = Objects.requireNonNull(clickType);
        this.button = Objects.requireNonNull(button);
        this.template = Objects.requireNonNull(template);
        this.page = Objects.requireNonNull(page);
        this.slot = slot;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }

    public ButtonClick getClickType() {
        return clickType;
    }

    public Button getButton() {
        return button;
    }

    public Template getTemplate() {
        return template;
    }

    public Page getPage() {
        return page;
    }

    public int getSlot() {
        return slot;
    }

}