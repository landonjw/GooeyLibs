package ca.landonjw.gooeylibs2.api.button;

import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.Template;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public class ButtonAction {

    private final ServerPlayerEntity player;
    private final ButtonClick clickType;
    private final Button button;
    private final Template template;
    private final Page page;
    private final int slot;

    public ButtonAction(@Nonnull ServerPlayerEntity player,
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

    public ServerPlayerEntity getPlayer() {
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

    public boolean isSlotInInventory() {
        return page.getInventoryTemplate().isPresent() && slot >= template.getSize();
    }

    public Optional<Integer> getInventorySlot() {
        if (isSlotInInventory()) {
            return Optional.of(slot - template.getSize());
        } else {
            return Optional.empty();
        }
    }

}