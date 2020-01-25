package ca.landonjw.gooeylibs.inventory.implementation;

import ca.landonjw.gooeylibs.inventory.api.*;
import ca.landonjw.gooeylibs.inventory.internal.FuturePageListener;
import ca.landonjw.gooeylibs.inventory.internal.UIContainer;
import ca.landonjw.gooeylibs.inventory.internal.UIInventory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.SPacketOpenWindow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Base implementation of {@link Page}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BasePage implements Page {

    /** The template that is used for the page. This defines what {@link Button}s will be shown on the page. */
    private Template template;
    /**
     * The title of the page. This will be shown at the top of the user interface.
     * Formatting can be used with TextFormatting.
     */
    private String title;
    /**
     * The linked previous page and next page. Buttons of type NextPage or PreviousPage will link to these.
     * These values may be null if the page does not link to any other page.
     */
    private Page previousPage, nextPage;
    /**
     * The behavior the page will have when the page is opened or closed by a player.
     * These values may be null if the page does not have any associated behavior.
     */
    private Consumer<PageAction> openBehavior, closeBehavior;

    /**
     * This determines if the page should suppress the {@link #onClose(EntityPlayerMP)} method.
     * This is used to prevent the page from invoking the method when it is simply reopening itself.
     */
    private boolean closeSilently;

    /**
     * Constructor for the page.
     *
     * @param template      the template for the page
     * @param title         the title for the page, null interpreted as an empty string
     * @param previousPage  the previous page to link to
     * @param nextPage      the next page to link to
     * @param openBehavior  the behavior to have when the page is opened, null interpreted as no behavior
     * @param closeBehavior the behavior to have when the page is closed, null interpreted as no behavior
     */
    protected BasePage(@Nonnull  Template template,
                       @Nullable String title,
                       @Nullable Page previousPage,
                       @Nullable Page nextPage,
                       @Nullable Consumer<PageAction> openBehavior,
                       @Nullable Consumer<PageAction> closeBehavior){
        this.template = Objects.requireNonNull(template, "template must not be null");
        this.title = (title != null) ? title : "";
        this.previousPage = previousPage;
        this.nextPage = nextPage;
        this.openBehavior = openBehavior;
        this.closeBehavior = closeBehavior;

        // Associates any button of type PreviousPage or NextPage to the page, creating a new LinkedButton instance.
        for(int row = 0; row < template.getRows(); row++){
            for(int col = 0; col < 9; col++){
                Button button = template.getButtons()[row][col];
                if(button != null && button.getType() != ButtonType.Standard){
                    template.getButtons()[row][col] = new LinkedButton(button, this);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Template getTemplate() {
        return template;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return title.replace(Page.TOTAL_PAGES_PLACEHOLDER, "" + getTotalPages())
                .replace(Page.CURRENT_PAGE_PLACEHOLDER, "" + getPageNumber());
    }

    /** {@inheritDoc} */
    @Override
    public int getPageNumber() {
        return (previousPage != null) ? previousPage.getPageNumber() + 1 : 1;
    }

    /** {@inheritDoc} */
    @Override
    public int getTotalPages() {
        return (nextPage != null) ? nextPage.getTotalPages() : getPageNumber();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Page> getPreviousPage() {
        return Optional.ofNullable(previousPage);
    }

    /** {@inheritDoc} */
    @Override
    public void setPreviousPage(Page page) {
        this.previousPage = page;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Page> getNextPage() {
        return Optional.ofNullable(nextPage);
    }

    /** {@inheritDoc} */
    @Override
    public void setNextPage(Page page) {
        this.nextPage = page;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Page> getPage(int pageNumber) {
        // Return empty option if page number is a bad value.
        if(pageNumber <= 0 || pageNumber > getTotalPages()){
            return Optional.empty();
        }
        // Calculate the difference between this page's current number to see if we're going back or forward.
        int pageDifference = pageNumber - getPageNumber();

        if(pageDifference < 0){
            // Go back until we find the right page or return empty optional if it's not found.
            Page page = this;
            for(int i = 0; i < Math.abs(pageDifference); i++){
                if(!page.getPreviousPage().isPresent()){
                    return Optional.empty();
                }
                else{
                    page = page.getPreviousPage().get();
                }
            }
            return Optional.ofNullable(page);
        }
        else if(pageDifference > 0){
            // Go forward until we find the right page or return empty optional if it's not found.
            Page page = this;
            for(int i = 0; i < Math.abs(pageDifference); i++){
                if(!page.getNextPage().isPresent()){
                    return Optional.empty();
                }
                else{
                    page = page.getNextPage().get();
                }
            }
            return Optional.ofNullable(page);
        }
        else{
            // Return this page if it's the page number being looked for.
            return Optional.of(this);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void openPage(@Nullable EntityPlayerMP player) {
        if(player != null){
            FuturePageListener.addFuturePage(player.getUniqueID(), this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void forceOpenPage(@Nullable EntityPlayerMP player) {
        if(player != null){
            // This causes the page to not invoke it's onClose method when it is reopening itself.
            closeSilently = true;
            // Closes player's current container and sets it to new container for page.
            player.closeContainer();
            UIInventory inventory = new UIInventory(this, player);
            Container container = new UIContainer(inventory);
            player.sendAllWindowProperties(container, inventory);
            player.openContainer = container;
            player.currentWindowId = 1;

            // Sends packet to player to open the container window.
            SPacketOpenWindow openWindow = new SPacketOpenWindow(
                    player.currentWindowId,
                    "minecraft:container",
                    inventory.getDisplayName(),
                    template.getRows() * 9
            );
            player.connection.sendPacket(openWindow);
            container.detectAndSendChanges();
            inventory.markDirty();
            closeSilently = false;

            // For each slot in the inventory, set the contents of the page's template.
            for(int row = 0; row < template.getRows(); row++){
                for(int col = 0; col < 9; col++){
                    Button button = template.getButtons()[row][col];
                    int linearIndex = row * 9 + col;
                    if(button != null){
                        player.sendSlotContents(container, linearIndex, button.getDisplay());
                        inventory.getInventoryContents().set(linearIndex, button.getDisplay());
                    }
                }
            }
            //Send the contents to the player and invoke the page's open behavior.
            player.sendAllContents(container, inventory.getInventoryContents());
            onOpen(player);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onOpen(@Nonnull EntityPlayerMP player) {
        if(openBehavior != null){
            PageAction action = new PageAction(this, player, PageAction.Type.Open);
            openBehavior.accept(action);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onClose(@Nonnull EntityPlayerMP player) {
        if(closeBehavior != null && !closeSilently){
            PageAction action = new PageAction(this, player, PageAction.Type.Close);
            closeBehavior.accept(action);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Page.Builder toBuilder() {
        return new Builder(this);
    }

    /** {@inheritDoc} */
    @Override
    public Page clone() {
        return new BasePage(template.clone(), title, previousPage, nextPage, openBehavior, closeBehavior);
    }

    /**
     * Base implementation of {@link Page.Builder}.
     *
     * @author landonjw
     * @since  1.0.0
     */
    public static class Builder implements Page.Builder {

        /** The template that is used for the page. This defines what {@link Button}s will be shown on the page. */
        private Template template;
        /** The title of the page. This will be shown at the top of the user interface. */
        private String title;
        /** The linked previous page and next page. Buttons of type NextPage or PreviousPage will link to these. */
        private Page previousPage, nextPage;
        /** The behavior the page will have when the page is opened or closed by a player. */
        private Consumer<PageAction> openBehavior, closeBehavior;

        /** The row and column of the top left corner of the dynamic content area. */
        private int dynamicContentAreaRow = -1, dynamicContentAreaCol = -1;
        /** The length and width of the dynamic content area. */
        private int dynamicContentAreaLength = 0, dynamicContentAreaWidth = 0;
        /** The list of buttons to create pages for dynamically. */
        private List<Button> dynamicContents;

        /**
         * Constructor for the builder, taking no arguments.
         */
        public Builder(){}

        /**
         * Constructor for the builder, taking a {@link BasePage} argument.
         * This is intended for functionality of {@link Page#toBuilder()}.
         *
         * @param page the page to create builder from
         */
        public Builder(BasePage page){
            this.template = page.template;
            this.title = page.title;
            this.previousPage = page.previousPage;
            this.nextPage = page.nextPage;
            this.openBehavior = page.openBehavior;
            this.closeBehavior = page.closeBehavior;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder template(@Nullable Template template) {
            this.template = template;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder dynamicContentArea(int row, int col, int length, int width) {
            dynamicContentAreaRow = row;
            dynamicContentAreaCol = col;
            dynamicContentAreaLength = length;
            dynamicContentAreaWidth = width;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder dynamicContents(@Nullable List<Button> contents) {
            dynamicContents = contents;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder previousPage(@Nullable Page page) {
            this.previousPage = page;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder nextPage(@Nullable Page page) {
            this.nextPage = page;
            return this;
        }

        @Override
        public Page.Builder onOpen(@Nullable Consumer<PageAction> behavior) {
            openBehavior = behavior;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder onOpen(@Nullable Runnable behavior) {
            openBehavior = (behavior != null) ? (action) -> behavior.run() : null;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder onClose(@Nullable Consumer<PageAction> behavior) {
            closeBehavior = behavior;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder onClose(@Nullable Runnable behavior) {
            closeBehavior = (behavior != null) ? (action) -> behavior.run() : null;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page.Builder reset() {
            template = null;
            title = null;
            previousPage = null;
            nextPage = null;
            openBehavior = null;
            closeBehavior = null;
            dynamicContentAreaRow = -1;
            dynamicContentAreaCol = -1;
            dynamicContentAreaLength = -1;
            dynamicContentAreaWidth = -1;
            dynamicContents = null;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Page build() {
            // Throw exception if there's no template set.
            if(template == null){
                throw new IllegalStateException("template is required for building page");
            }

            // If dynamic contents are defined, start process of generating as many pages as required to fit them.
            if(dynamicContents != null && !dynamicContents.isEmpty()){
                // Throw exception if the length or width are bad values
                if(dynamicContentAreaLength <= 0 || dynamicContentAreaWidth <= 0){
                    throw new IllegalStateException("trying to set dynamic contents when dynamic content area" +
                            " has length or width below or equal to 0");
                }

                // Check the dynamic area is in bounds.
                boolean startRowOutOfBounds = dynamicContentAreaRow < 0 || dynamicContentAreaRow >= template.getRows();
                boolean startColOutOfBounds = dynamicContentAreaCol < 0 || dynamicContentAreaRow >= 9;

                int endRow = dynamicContentAreaRow + dynamicContentAreaLength;
                int endCol = dynamicContentAreaCol + dynamicContentAreaWidth;
                boolean endRowOutOfBounds = endRow < 0 || endRow > template.getRows();
                boolean endColOutOfBounds = endCol < 0 || endCol > 9;
                if(startRowOutOfBounds || startColOutOfBounds || endRowOutOfBounds || endColOutOfBounds){
                    throw new IllegalStateException("trying to set dynamic contents when dynamic content area is out of template bounds");
                }

                // Calculate how many pages are required to fit the dynamic content.
                int contentSlots = dynamicContentAreaLength * dynamicContentAreaWidth;
                int totalPages = (int) Math.ceil((double) dynamicContents.size() / contentSlots);

                // Creates each page and return the first page generated.
                List<Page> pages = new ArrayList<>();
                int contentIndex = 0;
                for(int pageNum = 0; pageNum < totalPages; pageNum++){
                    Template.Builder builder = template.toBuilder();
                    //Clear dynamic area of all buttons
                    builder.rectangle(dynamicContentAreaRow, dynamicContentAreaCol,
                            dynamicContentAreaLength, dynamicContentAreaWidth, null);
                    //Place dynamic contents in each slot
                    for(int row = dynamicContentAreaRow; row < endRow; row++){
                        for(int col = dynamicContentAreaCol; col < endCol; col++){
                            if(contentIndex < dynamicContents.size()){
                                builder.set(row, col, dynamicContents.get(contentIndex));
                                contentIndex++;
                            }
                        }
                    }
                    Template filledTemplate = builder.build();
                    Page page = new BasePage(filledTemplate, title, previousPage, nextPage, openBehavior, closeBehavior);
                    //Links each page together.
                    if(!pages.isEmpty()){
                        Page previousPage = pages.get(pages.size() - 1);
                        page.setPreviousPage(previousPage);
                        previousPage.setNextPage(page);
                    }
                    pages.add(page);
                }
                return pages.get(0);
            }
            return new BasePage(template.clone(), title, previousPage, nextPage, openBehavior, closeBehavior);
        }

    }

}
