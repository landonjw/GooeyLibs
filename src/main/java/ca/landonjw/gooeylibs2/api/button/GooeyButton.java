package ca.landonjw.gooeylibs2.api.button;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

public class GooeyButton extends ButtonBase {

    private final Consumer<ButtonAction> onClick;

    protected GooeyButton(@Nonnull ItemStack display, @Nullable Consumer<ButtonAction> onClick) {
        super(display);
        this.onClick = onClick;
    }

    @Override
    public void onClick(@Nonnull ButtonAction action) {
        if (onClick != null) onClick.accept(action);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static GooeyButton of(ItemStack stack) {
        return builder()
                .display(stack)
                .build();
    }

    public static class Builder {

        protected ItemStack display;
        protected String title;
        protected Collection<String> lore = Lists.newArrayList();
        protected Consumer<ButtonAction> onClick;

        public Builder display(@Nonnull ItemStack display) {
            this.display = display;
            return this;
        }

        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        public Builder lore(@Nullable Collection<String> lore) {
            this.lore = (lore != null) ? lore : Lists.newArrayList();
            return this;
        }

        public Builder onClick(@Nullable Consumer<ButtonAction> behaviour) {
            this.onClick = behaviour;
            return this;
        }

        public Builder onClick(@Nullable Runnable behaviour) {
            this.onClick = (behaviour != null) ? (action) -> behaviour.run() : null;
            return this;
        }

        public GooeyButton build() {
            validate();
            return new GooeyButton(buildDisplay(), onClick);
        }

        protected void validate() {
            if (display == null) throw new IllegalStateException("button display must be defined");
        }

        protected ItemStack buildDisplay() {
            if (title != null) display.setDisplayName(new StringTextComponent(title));

            if (!lore.isEmpty()) {
                ListNBT nbtLore = new ListNBT();
                for (String line : lore) {
                    //If a line in the lore is null, just ignore it.
                    if (line != null) {
                        nbtLore.add(StringNBT.valueOf(line));
                    }
                }
                display.getOrCreateChildTag("display").put("Lore", nbtLore);
            }
            if (display.hasTag()) {
                display.getTag().putString("tooltip", "");
            }
            return display;
        }

    }

}