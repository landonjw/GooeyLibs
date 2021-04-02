package ca.landonjw.gooeylibs2.api.template.slot;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.data.EventEmitter;
import ca.landonjw.gooeylibs2.api.data.Subject;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public final class TemplateSlot extends Slot implements Subject<TemplateSlot> {

    private final EventEmitter<TemplateSlot> eventEmitter = new EventEmitter<>();
    private Button button;

    public TemplateSlot(@Nullable Button button, int index, int xPosition, int yPosition) {
        super(null, index, xPosition, yPosition);
        setButton(button);
    }

    public Optional<Button> getButton() {
        return Optional.ofNullable(button);
    }

    public void setButton(@Nullable Button button) {
        if (this.button != null) this.button.unsubscribe(this);
        this.button = button;
        if (button != null) button.subscribe(this, this::update);

        update();
    }

    @Override
    public void subscribe(@Nonnull Object observer, @Nonnull Consumer<TemplateSlot> consumer) {
        this.eventEmitter.subscribe(observer, consumer);
    }

    @Override
    public void unsubscribe(@Nonnull Object observer) {
        this.eventEmitter.unsubscribe(observer);
    }

    public void update() {
        this.eventEmitter.emit(this);
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