package ca.landonjw.gooeylibs2.api.button.moveable;

import ca.landonjw.gooeylibs2.api.button.ButtonAction;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

public class MovableButton extends GooeyButton implements Movable {

    private Consumer<MovableButtonAction> onPickup;
    private Consumer<MovableButtonAction> onDrop;

    protected MovableButton(@Nonnull ItemStack display,
                            @Nullable Consumer<ButtonAction> onClick,
                            @Nullable Consumer<MovableButtonAction> onPickup,
                            @Nullable Consumer<MovableButtonAction> onDrop) {
        super(display, onClick);
        this.onPickup = onPickup;
        this.onDrop = onDrop;
    }

    public void onPickup(MovableButtonAction action) {
        if (onPickup != null) {
            this.onPickup.accept(action);
        }
    }

    public void onDrop(MovableButtonAction action) {
        if (onDrop != null) {
            this.onDrop.accept(action);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GooeyButton.Builder {

        protected Consumer<MovableButtonAction> onPickup;
        protected Consumer<MovableButtonAction> onDrop;

        public Builder display(@Nonnull ItemStack display) {
            super.display(display);
            return this;
        }

        public Builder title(@Nullable ITextComponent title) {
            super.title(title);
            return this;
        }

        public Builder lore(@Nullable Collection<String> lore) {
            super.lore(lore);
            return this;
        }

        public <T> Builder lore(Class<T> type, @Nullable Collection<T> lore) {
            super.lore(type, lore);
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

        public Builder onPickup(@Nullable Consumer<MovableButtonAction> behaviour) {
            this.onPickup = behaviour;
            return this;
        }

        public Builder onPickup(@Nullable Runnable behaviour) {
            if (behaviour != null) {
                this.onPickup = (action) -> behaviour.run();
            }
            return this;
        }

        public Builder onDrop(@Nullable Consumer<MovableButtonAction> behaviour) {
            this.onDrop = behaviour;
            return this;
        }

        public Builder onDrop(@Nullable Runnable behaviour) {
            if (behaviour != null) {
                this.onDrop = (action) -> behaviour.run();
            }
            return this;
        }

        public MovableButton build() {
            validate();
            return new MovableButton(buildDisplay(), onClick, onPickup, onDrop);
        }

    }

}
