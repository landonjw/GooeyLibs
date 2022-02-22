package ca.landonjw.gooeylibs2.api.button;

import ca.landonjw.gooeylibs2.api.adventure.ForgeTranslator;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        protected ITextComponent title;
        protected Collection<ITextComponent> lore = Lists.newArrayList();
        protected Consumer<ButtonAction> onClick;
        protected Set<FlagType> hideFlags = new LinkedHashSet<>();

        public Builder display(@Nonnull ItemStack display) {
            this.display = display;
            return this;
        }

        public Builder title(@Nullable ITextComponent title) {
            this.title = title;
            return this;
        }

        public Builder title(@Nullable Component title) {
            return this.title(ForgeTranslator.asMinecraft(title));
        }

        public Builder lore(@Nullable Collection<ITextComponent> lore) {
            this.lore = (lore != null) ? lore : Lists.newArrayList();
            return this;
        }

        public Builder lore(@Nullable List<Component> lore) {
            if(lore == null) {
                this.lore = Lists.newArrayList();
                return this;
            }

            return this.lore(lore.stream().map(ForgeTranslator::asMinecraft).collect(Collectors.toList()));
        }

        public Builder hideFlags(FlagType... flags) {
            this.hideFlags.addAll(Arrays.asList(flags));
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
            if (title != null) {
                IFormattableTextComponent result = new StringTextComponent("")
                        .setStyle(Style.EMPTY.setItalic(false))
                        .appendSibling(this.title);
                display.setDisplayName(result);
            }

            if (!lore.isEmpty()) {
                ListNBT nbtLore = new ListNBT();
                for (ITextComponent line : lore) {
                    IFormattableTextComponent result = new StringTextComponent("")
                            .setStyle(Style.EMPTY.setItalic(false))
                            .appendSibling(line);
                    nbtLore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(result)));
                }
                display.getOrCreateChildTag("display").put("Lore", nbtLore);
            }

            if (!this.hideFlags.isEmpty() && display.hasTag())
            {
                if (this.hideFlags.contains(FlagType.Reforged) || this.hideFlags.contains(FlagType.All))
                {
                    display.getOrCreateTag().putString("tooltip", "");
                }
                if (this.hideFlags.contains(FlagType.Generations) || this.hideFlags.contains(FlagType.All))
                {
                    display.getOrCreateTag().putBoolean("HideTooltip", true);
                }
                int value = 0;
                for (FlagType flag : this.hideFlags)
                {
                    value += flag.getValue();
                }
                display.getOrCreateTag().putInt("HideFlags", value);
            }
            return display;
        }

    }

}