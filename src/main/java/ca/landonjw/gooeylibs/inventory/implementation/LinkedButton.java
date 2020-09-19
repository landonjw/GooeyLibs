package ca.landonjw.gooeylibs.inventory.implementation;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.ButtonType;
import ca.landonjw.gooeylibs.inventory.api.Page;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation for a {@link Button} that has additional functionality relating to the page it is on.
 * This will automatically be created for any button that has a type that is not Standard.
 *
 * For more information on what each button type does, refer to {@link ButtonType}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class LinkedButton implements Button {

    /** The button that links to a page. */
    private Button button;
    /** The page that is being linked to. */
    private Page page;

    /**
     * Constructor for a linked button.
     *
     * @param button the button that links to a page
     * @param page   the page being linked to
     */
    public LinkedButton(@Nonnull Button button, @Nullable Page page){
        this.button = Objects.requireNonNull(button, "button must not be null");
        this.page = page;
    }

    /**
     * Constructor for a linked button.
     *
     * @param button the button that links to a page
     */
    public LinkedButton(@Nonnull Button button){
        this(button, null);
    }

    /**
     * Gets the page being linked to, if present
     *
     * @return the page being linked to, if present
     */
    public Optional<Page> getPage() {
        return Optional.ofNullable(page);
    }

    /**
     * Sets the page being linked to.
     *
     * @param page the page being linked to
     */
    public void setPage(@Nullable Page page) {
        this.page = page;
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack getDisplay() {
        if(button.getType() ==  ButtonType.PageInfo){
            ItemStack display = button.getDisplay().copy();
            String displayName = display.getDisplayName();
            displayName = displayName.replace(Page.CURRENT_PAGE_PLACEHOLDER, "" + page.getPageNumber());
            displayName = displayName.replace(Page.TOTAL_PAGES_PLACEHOLDER, "" + page.getTotalPages());
            display.setStackDisplayName(displayName);
            return display;
        }
        return button.getDisplay();
    }

    /** {@inheritDoc} */
    @Override
    public ButtonType getType() {
        return button.getType();
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(@Nonnull EntityPlayerMP player, @Nonnull ClickType clickType, @Nonnull Page page) {
        button.onClick(player, clickType, page);
        if(button.getType() == ButtonType.PreviousPage){
            page.getPreviousPage().ifPresent((previousPage) -> previousPage.forceOpenPage(player));
        }
        else if(button.getType() == ButtonType.NextPage){
            page.getNextPage().ifPresent((nextPage) -> nextPage.forceOpenPage(player));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Builder toBuilder() {
        return button.toBuilder();
    }

    /** {@inheritDoc} */
    @Override
    public Button clone() {
        return new LinkedButton(button.clone(), page);
    }

}
