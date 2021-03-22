package ca.landonjw.gooeylibs.api.button.moveable;

import ca.landonjw.gooeylibs.api.button.ButtonAction;
import ca.landonjw.gooeylibs.api.button.GooeyButton;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

public class MoveableButton extends GooeyButton {

    private Consumer<MoveableButtonAction> onPickup;
    private Consumer<MoveableButtonAction> onDrop;

    protected MoveableButton(@Nonnull ItemStack display,
                             @Nullable Consumer<ButtonAction> onClick,
                             @Nullable Consumer<MoveableButtonAction> onPickup,
                             @Nullable Consumer<MoveableButtonAction> onDrop) {
        super(display, onClick);
        this.onPickup = onPickup;
        this.onDrop = onDrop;
    }

    public void onPickup(MoveableButtonAction action) {
        if (onPickup != null) {
            this.onPickup.accept(action);
        }
    }

    public void onDrop(MoveableButtonAction action) {
        if (onDrop != null) {
            this.onDrop.accept(action);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GooeyButton.Builder {

        protected Consumer<MoveableButtonAction> onPickup;
        protected Consumer<MoveableButtonAction> onDrop;

        public Builder display(@Nonnull ItemStack display) {
            super.display(display);
            return this;
        }

        public Builder title(@Nullable String title) {
            super.title(title);
            return this;
        }

        public Builder lore(@Nullable Collection<String> lore) {
            super.lore(lore);
            return this;
        }

        public Builder onClick(@Nullable Consumer<ButtonAction> behaviour) {
            super.onClick(behaviour);
            return this;
        }

        public Builder onClick(@Nullable Runnable behaviour) {
            super.onClick(behaviour);
            return this;
        }

        public Builder onPickup(@Nullable Consumer<MoveableButtonAction> behaviour) {
            this.onPickup = behaviour;
            return this;
        }

        public Builder onPickup(@Nullable Runnable behaviour) {
            if (behaviour != null) {
                this.onPickup = (action) -> behaviour.run();
            }
            return this;
        }

        public Builder onDrop(@Nullable Consumer<MoveableButtonAction> behaviour) {
            this.onDrop = behaviour;
            return this;
        }

        public Builder onDrop(@Nullable Runnable behaviour) {
            if (behaviour != null) {
                this.onDrop = (action) -> behaviour.run();
            }
            return this;
        }

        public MoveableButton build() {
            validate();
            return new MoveableButton(buildDisplay(), onClick, onPickup, onDrop);
        }

    }

}
