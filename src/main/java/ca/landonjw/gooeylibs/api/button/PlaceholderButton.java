package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PlaceholderButton implements Button {

    private final Button button;

    public PlaceholderButton(@Nonnull Button button) {
        this.button = button;
    }

    public static PlaceholderButton of(@Nonnull Button button) {
        return new PlaceholderButton(button);
    }

    @Override
    public ItemStack getDisplay() {
        return button.getDisplay();
    }

    @Override
    public void onClick(@Nonnull ButtonAction action) {
        button.onClick(action);
    }

    @Override
    public void subscribe(@Nonnull Object observer, @Nonnull Consumer<Button> consumer) {
        button.subscribe(observer, consumer);
    }

    @Override
    public void unsubscribe(@Nonnull Object observer) {
        button.unsubscribe(observer);
    }

}