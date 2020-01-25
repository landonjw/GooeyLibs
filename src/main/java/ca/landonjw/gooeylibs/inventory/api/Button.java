package ca.landonjw.gooeylibs.inventory.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Interface for a button that can be displayed on a {@link Page} and clicked to invoke certain behaviour.
 *
 * These buttons may be reused across different pages and in different positions on a page.
 * It is encouraged to reuse buttons instead of creating identical ones in {@link Template}s through methods like
 * {@link Template.Builder#row(int, Button)}, which will fill a whole row with a single button.
 *
 * @author landonjw
 * @since  1.0.0
 */
public interface Button {

    /**
     * Gets the item stack that is displayed as the button on a {@link Page}.
     *
     * @return the item stack that will be displayed as the button on a page
     */
    ItemStack getDisplay();

    /**
     * Gets the {@link ButtonType} of the button.
     *
     * <p>Most buttons should be of type Standard.
     * Buttons of type NextPage and PreviousPage will open a linked page if it is present,
     * and buttons of type PageInfo will have any text with placeholders {total} or {current} replaced
     * with the {@link Page#getTotalPages()} and {@link Page#getPageNumber()} respectively.</p>
     *
     * @return the button type of the button
     */
    ButtonType getType();

    /**
     * Called when the button is clicked on a {@link Page}.
     *
     * @param player    the player that clicked the button
     * @param clickType the type of click the player used
     */
    void onClick(@Nonnull EntityPlayerMP player, @Nonnull ClickType clickType);

    /**
     * Returns a new {@link Builder} with the content and behavior of the button.
     * This can be used to edit the otherwise immutable button instance.
     *
     * @return a new button builder with content and behavior of the button
     */
    Builder toBuilder();

    /**
     * Clones the button to create a new button with identical content and behavior.
     * This produces a deep copy, changes to the copied button's item will not be reflected in the original.
     *
     * @return a clone of the button
     */
    Button clone();

    /**
     * Returns a new {@link Builder} for a button.
     *
     * @return a new builder for a button
     */
    static Builder builder(){
        return InventoryAPI.getInstance().buttonBuilder();
    }

    /**
     * Returns a new {@link Button} instance of a given item, with no behavior.
     *
     * @param item the item to set as display for the button
     * @return a new button instance with item as display
     */
    static Button of(@Nonnull ItemStack item){
        return InventoryAPI.getInstance().buttonBuilder()
                .item(item)
                .build();
    }

    /**
     * Interface for a builder that creates a {@link Button}.
     *
     * @author landonjw
     * @since  1.0.0
     */
    interface Builder {

        /**
         * Sets the item to be used as the display for the button.
         * This must be set to a non-null value for {@link #build()} to be successful.
         *
         * @param item the item to be used as the display for the button
         * @return builder with item set
         */
        Builder item(@Nullable ItemStack item);

        /**
         * Sets the display name of the button.
         *
         * @param name the display name of the button, null interpreted as default item's name
         * @return builder with display name set
         */
        Builder displayName(@Nullable String name);

        /**
         * Sets the lore of the button.
         * If this is not set, button will have no lore.
         *
         * @param lore the lore of the button, null for no lore
         * @return the builder with lore set
         */
        Builder lore(@Nullable List<String> lore);

        /**
         * Sets the {@link ButtonType} of the button.
         *
         * Button Types:
         * <ul>
         *     <li>Standard: Offers no additional functionality</li>
         *     <li>NextPage: Opens the next linked {@link Page} if there is one.</li>
         *     <li>PreviousPage: Opens the previous linked {@link Page} if there is one.</li>
         *     <li>PageInfo: Substitutes placeholders for page attribute's to allow for describing page numbers</li>
         * </ul>
         *
         * @param type the type of the button, null treated as Standard
         * @return the builder with the button type set
         */
        Builder type(@Nullable ButtonType type);

        /**
         * Sets the behavior of the button when it is clicked.
         *
         * @param behavior the behavior of the button, taking a {@link ButtonAction} argument
         * @return the builder with click behavior set
         */
        Builder onClick(@Nullable Consumer<ButtonAction> behavior);

        /**
         * Sets the behavior of the button when it is clicked.
         *
         * @param behavior the behavior of the button, taking no arguments
         * @return the builder with click behavior set
         */
        Builder onClick(@Nullable Runnable behavior);

        /**
         * Resets the builder to it's default state.
         *
         * @return the builder with values reset
         */
        Builder reset();

        /**
         * Builds a new {@link Button} instance from values in the builder.
         *
         * @return new button instance
         * @throws IllegalStateException if no item was defined
         */
        Button build();

    }

}
