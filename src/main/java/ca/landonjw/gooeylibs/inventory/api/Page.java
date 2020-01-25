package ca.landonjw.gooeylibs.inventory.api;

import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Interface for a page to be displayed to a user.
 *
 * <p>Each page has a {@link Template} that defines the layout of the page,
 * and a page may have many different {@link Button}s that make the page interactive.
 *
 * In order to send a page to a player, use {@link #openPage(EntityPlayerMP)} or {@link #forceOpenPage(EntityPlayerMP)}.</p>
 *
 * @author landonjw
 * @since  1.0.0
 */
public interface Page {

    /** The placeholder to substitute current page number. */
    String CURRENT_PAGE_PLACEHOLDER = "{current}";
    /** The placeholder to substitute total page number. */
    String TOTAL_PAGES_PLACEHOLDER = "{total}";

    /**
     * Gets the {@link Template} that is used in the page.
     *
     * @return the template being used
     */
    Template getTemplate();

    /**
     * Gets the title of the page. Supports formatting with TextFormatting.
     * This will be displayed at the top of the user interface.
     *
     * @return the title of the page
     */
    String getTitle();

    /**
     * Gets the page number of the page.
     * For example, if the page has a single previous page, the page number will be 2.
     *
     * @return the page number of the page
     */
    int getPageNumber();

    /**
     * Gets the total number of linked pages.
     * For example, if the page has a single previous page and a single next page, the page number will be 3.
     *
     * @return the total number of linked pages
     */
    int getTotalPages();

    /**
     * Gets the previous page, if present
     *
     * @return the previous page, if present
     */
    Optional<Page> getPreviousPage();

    /**
     * Sets the previous page.
     *
     * @param page the previous page
     */
    void setPreviousPage(@Nullable Page page);

    /**
     * Gets the next page, if present
     *
     * @return the previous page, if present
     */
    Optional<Page> getNextPage();

    /**
     * Sets the next page.
     *
     * @param page the next page
     */
    void setNextPage(@Nullable Page page);

    /**
     * Gets a page at a certain page number, if present
     *
     * @param pageNumber the page number to get page from
     * @return page at the page number, if present
     */
    Optional<Page> getPage(int pageNumber);

    /**
     * Opens the page for a player.
     * If the player is null, this will do nothing.
     *
     * <p>If the player is currently in an inventory, this will wait until that inventory
     * is closed before opening. If you want to forcibly open the page, use
     * {@link #forceOpenPage(EntityPlayerMP)} instead.</p>
     *
     * @param player the player to open page for
     */
    void openPage(@Nullable EntityPlayerMP player);

    /**
     * Forcibly opens the page for a player.
     * If the player is null, this will do nothing.
     *
     * If the player is currently in an inventory, this will close that inventory and open this one instead.
     *
     * @param player the player to forcibly open page for
     */
    void forceOpenPage(@Nullable EntityPlayerMP player);

    /**
     * Called when the page is opened.
     *
     * @param player the player opening the page
     */
    void onOpen(@Nonnull EntityPlayerMP player);

    /**
     * Called when the page is closed.
     *
     * @param player the player closing the page
     */
    void onClose(@Nonnull EntityPlayerMP player);

    /**
     * Returns a new {@link Button.Builder} with the content and behavior of the page.
     * This can be used to edit the otherwise immutable page instance.
     *
     * @return a new page builder with content and behavior of the page
     */
    Builder toBuilder();

    /**
     * Clones the page to create a new page with identical content and behavior.
     * This produces a deep copy, changes to the copied page will not be reflected in the original.
     *
     * @return a clone of the button
     */
    Page clone();

    /**
     * Returns a new {@link Page.Builder} for a page.
     *
     * @return a new builder for a page
     */
    static Builder builder(){
        return InventoryAPI.getInstance().pageBuilder();
    }

    /**
     * Interface for a builder that creates a {@link Page}.
     *
     * @author landonjw
     * @since  1.0.0
     */
    interface Builder {

        /**
         * Sets the title of the page. Supports formatting with TextFormatting.
         *
         * @param title the title of the page, null for no title
         * @return builder with title set
         */
        Builder title(@Nullable String title);

        /**
         * Sets the template of the page.
         * The buttons in the template will be displayed when the page is opened.
         *
         * @param template the template of the page
         * @return builder with template set
         */
        Builder template(@Nonnull Template template);

        /**
         * Sets a dynamic content area for the page.
         *
         * <p>This can be used alongside {@link #dynamicContents(List)} and will generate
         * as many pages as required to fix the size of the contents.</p>
         *
         * <p>If there are dynamic contents set and this isn't defined with a valid space,
         * invoking {@link #build()} will result in an {@link IllegalStateException}.</p>
         *
         * @param row    the row of the content area's top left corner
         * @param col    the column of the content area's top left corner
         * @param length the length of the content area
         * @param width  the width of the content area
         * @return builder with dynamic content area set
         */
        Builder dynamicContentArea(int row, int col, int length, int width);

        /**
         * Sets dynamic contents for the page.
         *
         * <p>This can be used alongside {@link #dynamicContentArea(int, int, int, int)} to generate
         * as many pages as required to fix the size of the contents.</p>
         *
         * <p>If this is set, a valid dynamic content area must be set or invoking {@link #build()}
         * will result in an {@link IllegalStateException}.</p>
         *
         * @param contents the contents to fit into pages
         * @return builder with dynamic contents set
         */
        Builder dynamicContents(@Nullable List<Button> contents);

        /**
         * Sets the previous page.
         *
         * @param page the page to set as previous page
         * @return builder with previous page set
         */
        Builder previousPage(@Nullable Page page);

        /**
         * Sets the next page.
         *
         * @param page the page to set as next page
         * @return builder with next page set
         */
        Builder nextPage(@Nullable Page page);

        /**
         * Sets the behavior of the page when it is opened.
         *
         * @param behavior the behavior to set when the page is opened, taking a {@link PageAction} argument
         * @return builder with behavior set
         */
        Builder onOpen(@Nullable Consumer<PageAction> behavior);

        /**
         * Sets the behavior of the page when it is opened.
         *
         * @param behavior the behavior to set when the page is opened, taking no arguments
         * @return builder with behavior set
         */
        Builder onOpen(@Nullable Runnable behavior);

        /**
         * Sets the behavior of the page when it is closed.
         *
         * @param behavior the behavior to set when the page is closed, taking a {@link PageAction} argument
         * @return builder with behavior set
         */
        Builder onClose(@Nullable Consumer<PageAction> behavior);

        /**
         * Sets the behavior of the page when it is closed.
         *
         * @param behavior the behavior to set when the page is closed, taking no arguments
         * @return builder with behavior set
         */
        Builder onClose(@Nullable Runnable behavior);

        /**
         * Resets the builder to it's default state.
         *
         * @return the builder with values reset
         */
        Builder reset();

        /**
         * Builds a new {@link Page} instance from values in the builder.
         *
         * @return new page instance
         * @throws IllegalStateException if no template is defined
         * @throws IllegalStateException if dynamic contents are defined but content area dimensions
         *                               are less than or equal to 0
         * @throws IllegalStateException if dynamic contents are defined but content area is out of bounds
         */
        Page build();

    }

}
