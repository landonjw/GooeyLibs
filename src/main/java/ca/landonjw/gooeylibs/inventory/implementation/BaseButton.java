package ca.landonjw.gooeylibs.inventory.implementation;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.ButtonAction;
import ca.landonjw.gooeylibs.inventory.api.ButtonType;
import ca.landonjw.gooeylibs.inventory.api.Page;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base implementation of {@link Button}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BaseButton implements Button {

    /** The item to display as the button on {@link Page}s. */
    protected ItemStack item;
    /** The type of the button. */
    protected ButtonType type;
    /** The behavior the button will have when it is clicked. */
    private Consumer<ButtonAction> clickBehavior;

    /**
     * Constructor for a button.
     *
     * @param item        the item to display as the button on pages
     * @param displayName the display name of the button
     * @param lore        the lore of the button
     * @param type        the type of button
     * @param behavior    the behavior of the button on click
     */
    protected BaseButton(@Nonnull  ItemStack item,
                         @Nullable String displayName,
                         @Nullable List<String> lore,
                         @Nonnull  ButtonType type,
                         @Nullable Consumer<ButtonAction> behavior){

        this.item = Objects.requireNonNull(item, "item must not be null");
        //Replace display name & lore of the item if they are not null.
        if(displayName != null){
            item.setStackDisplayName(displayName);
        }
        if(lore != null && !lore.isEmpty()){
            NBTTagList nbtLore = new NBTTagList();
            for(String line : lore){
                //If a line in the lore is null, just ignore it.
                if(line != null){
                    nbtLore.appendTag(new NBTTagString(line));
                }
            }
            item.getOrCreateSubCompound("display").setTag("Lore", nbtLore);
        }
        this.type = type;
        this.clickBehavior = behavior;
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack getDisplay() {
        return item;
    }

    /** {@inheritDoc} */
    @Override
    public ButtonType getType() {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(@Nonnull EntityPlayerMP player, @Nonnull ClickType clickType, @Nonnull Page page) {
        if(clickBehavior != null){
            ButtonAction action = new ButtonAction(this, player, clickType, page);
            clickBehavior.accept(action);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Button.Builder toBuilder() {
        return new Builder(this);
    }

    /** {@inheritDoc} */
    @Override
    public Button clone() {
        return new BaseButton(item.copy(), null, null, type, clickBehavior);
    }

    /**
     * Base implementation of {@link Button.Builder}.
     *
     * @author landonjw
     * @since  1.0.0
     */
    public static class Builder implements Button.Builder {

        /** The item to display as the button on {@link Page}s. */
        protected ItemStack item;
        /** The display name of the button. This will replace {@link #item}'s display name if not null. */
        protected String displayName;
        /** The lore of the button. This will replace {@link #item}'s lore if not null or empty. */
        protected List<String> lore;
        /** The type of the button. */
        protected ButtonType type = ButtonType.Standard;
        /** The behavior to be executed when the button is clicked. */
        protected Consumer<ButtonAction> clickBehavior;

        /**
         * Constructor for a builder, taking no arguments.
         */
        public Builder(){}

        /**
         * Constructor for a builder, taking a {@link BaseButton} argument.
         * This is intended for functionality of {@link Button#toBuilder()}.
         *
         * @param button the button to create builder from
         */
        public Builder(@Nonnull BaseButton button){
            this.item = button.item;
            this.type = button.type;
            this.clickBehavior = button.clickBehavior;
        }

        /** {@inheritDoc} */
        @Override
        public Button.Builder item(@Nullable ItemStack item) {
            this.item = item;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Button.Builder displayName(@Nullable String displayName) {
            this.displayName = displayName;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Button.Builder lore(@Nullable List<String> lore) {
            this.lore = lore;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Button.Builder type(@Nullable ButtonType type) {
            this.type = (type != null) ? type : ButtonType.Standard;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Button.Builder onClick(@Nullable Consumer<ButtonAction> behavior) {
            clickBehavior = behavior;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Button.Builder onClick(@Nullable Runnable behavior) {
            clickBehavior = (behavior != null) ? (action) -> behavior.run() : null;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Button.Builder reset() {
            item = null;
            displayName = null;
            lore = null;
            clickBehavior = null;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Button build() {
            if(item == null){
                throw new IllegalStateException("item must not be null");
            }
            return new BaseButton(item, displayName, lore, type, clickBehavior);
        }

    }

}
