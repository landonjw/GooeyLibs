package ca.landonjw.gooeylibs.api.button;

import ca.landonjw.gooeylibs.api.data.EventEmitter;
import ca.landonjw.gooeylibs.api.data.Subject;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public abstract class Button implements Subject<Button> {

    private final EventEmitter<Button> eventEmitter = new EventEmitter<>();
    private ItemStack display;

    protected Button(@Nonnull ItemStack display) {
        this.display = requireNonNull(display);
    }

    public ItemStack getDisplay() {
        return display;
    }

    public void setDisplay(@Nonnull ItemStack display) {
        this.display = display;
        update();
    }

    public void onClick(@Nonnull ButtonAction action) {
    }

    public void subscribe(@Nonnull Object observer, @Nonnull Consumer<Button> consumer) {
        this.eventEmitter.subscribe(observer, consumer);
    }

    public void unsubscribe(@Nonnull Object observer) {
        this.eventEmitter.unsubscribe(observer);
    }

    public void update() {
        this.eventEmitter.emit(this);
    }

}