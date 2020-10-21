package ca.landonjw.gooeylibs.api.template.slot;

import ca.landonjw.gooeylibs.api.button.IButton;
import ca.landonjw.gooeylibs.api.data.EventEmitter;
import com.google.common.collect.Maps;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class TemplateSlot extends Slot implements EventEmitter<TemplateSlot> {

    private final Map<Object, Consumer<TemplateSlot>> observers = Maps.newHashMap();
    private IButton button;

    public TemplateSlot(@Nullable IButton button, int index, int xPosition, int yPosition) {
        super(null, index, xPosition, yPosition);
        this.button = button;
    }

    public Optional<IButton> getButton() {
        return Optional.ofNullable(button);
    }

    public void setButton(@Nullable IButton button) {
        this.button = button;
        this.emit(this);
    }

    public void emit(@Nullable TemplateSlot value) {
        this.observers.values().forEach((observer) -> observer.accept(this));
    }

    public void subscribe(@Nonnull Object subscriber, @Nonnull Consumer<TemplateSlot> consumer) {
        this.observers.put(subscriber, consumer);
    }

    public void unsubscribe(@Nonnull Object subscriber) {
        this.observers.remove(subscriber);
    }

    @Override
    public ItemStack getStack() {
        return (button != null) ? button.getDisplay() : ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

}