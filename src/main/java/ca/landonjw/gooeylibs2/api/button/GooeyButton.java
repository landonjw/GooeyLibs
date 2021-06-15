package ca.landonjw.gooeylibs2.api.button;

import ca.landonjw.gooeylibs2.api.template.LineType;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
    public static GooeyButton of(ItemStack stack, String title) {
        return builder()
                .display(stack)
                .title(title)
                .build();
    }

    public static class Builder {

        protected ItemStack display;
        protected String title;
        protected Collection<String> lore = Lists.newArrayList();
        protected Consumer<ButtonAction> onClick;
        protected List<EnumFlag> hideFlags = new ArrayList<>();

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

        public Builder hideFlags(EnumFlag... flags) {
            this.hideFlags = Arrays.asList(flags);
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
            if (!this.hideFlags.isEmpty() && display.hasTagCompound()) {
                if (this.hideFlags.contains(EnumFlag.PIXELMON)) {
                    display.getTagCompound().setString("tooltip", "");
                }
                int value = 0;
                for (EnumFlag flag : this.hideFlags) {
                    value += flag.getValue();
                }
                display.getTagCompound().setInteger("HideFlags", value);
            }
            return display;
        }

    }

}