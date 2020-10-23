package ca.landonjw.gooeylibs.api.button;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

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

    public static class Builder {

        private ItemStack display;
        private String title;
        private Collection<String> lore = Lists.newArrayList();
        private Consumer<ButtonAction> onClick;

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

        private void validate() {
            if (display == null) throw new IllegalStateException("button display must be defined");
        }

        private ItemStack buildDisplay() {
            if (title != null) display.setStackDisplayName(title);

            if (!lore.isEmpty()) {
                NBTTagList nbtLore = new NBTTagList();
                for (String line : lore) {
                    //If a line in the lore is null, just ignore it.
                    if (line != null) {
                        nbtLore.appendTag(new NBTTagString(line));
                    }
                }
                display.getOrCreateSubCompound("display").setTag("Lore", nbtLore);
            }
            if (display.hasTagCompound()) {
                display.getTagCompound().setString("tooltip", "");
            }
            return display;
        }

    }

}