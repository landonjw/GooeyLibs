package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.types.InventoryTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Represents a page that is intended for pagination.
 * <p>
 * This acts as a doubly linked list, allowing for navigation between a chain of pages.
 *
 * @author landonjw
 */
public class LinkedPage extends GooeyPage {

    public static final String CURRENT_PAGE_PLACEHOLDER = "{current}";
    public static final String TOTAL_PAGES_PLACEHOLDER = "{total}";

    private LinkedPage previous;
    private LinkedPage next;

    public LinkedPage(@Nonnull Template template,
                      @Nullable InventoryTemplate inventoryTemplate,
                      @Nullable String title,
                      @Nullable Consumer<PageAction> onOpen,
                      @Nullable Consumer<PageAction> onClose,
                      @Nullable LinkedPage previous,
                      @Nullable LinkedPage next) {
        super(template, inventoryTemplate, title, onOpen, onClose);
        this.previous = previous;
        this.next = next;
    }

    public Page getPrevious() {
        return previous;
    }

    public void setPrevious(LinkedPage previous) {
        this.previous = previous;
        update();
    }

    public Page getNext() {
        return next;
    }

    public void setNext(LinkedPage next) {
        this.next = next;
        update();
    }

    public int getCurrentPage() {
        return (previous != null) ? previous.getCurrentPage() + 1 : 1;
    }

    public int getTotalPages() {
        return (next != null) ? next.getTotalPages() : getCurrentPage();
    }

    @Override
    public String getTitle() {
        return super.getTitle()
                .replace(CURRENT_PAGE_PLACEHOLDER, "" + getCurrentPage())
                .replace(TOTAL_PAGES_PLACEHOLDER, "" + getTotalPages());
    }

    @Override
    public Template getTemplate() {
        return super.getTemplate();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GooeyPage.Builder {

        protected LinkedPage previousPage;
        protected LinkedPage nextPage;

        @Override
        public Builder title(@Nullable String title) {
            super.title(title);
            return this;
        }

        @Override
        public Builder template(@Nonnull Template template) {
            super.template(template);
            return this;
        }

        @Override
        public Builder inventory(@Nullable InventoryTemplate template) {
            super.inventory(template);
            return this;
        }

        @Override
        public Builder onOpen(@Nullable Consumer<PageAction> behaviour) {
            super.onOpen(behaviour);
            return this;
        }

        @Override
        public Builder onOpen(@Nullable Runnable behaviour) {
            super.onOpen(behaviour);
            return this;
        }

        @Override
        public Builder onClose(@Nullable Consumer<PageAction> behaviour) {
            super.onClose(behaviour);
            return this;
        }

        @Override
        public Builder onClose(@Nullable Runnable behaviour) {
            super.onClose(behaviour);
            return this;
        }

        public Builder nextPage(@Nullable LinkedPage next) {
            this.nextPage = next;
            return this;
        }

        public Builder previousPage(@Nullable LinkedPage previous) {
            this.previousPage = previous;
            return this;
        }

        public LinkedPage build() {
            validate();
            return new LinkedPage(template, inventoryTemplate, title, onOpen, onClose, previousPage, nextPage);
        }

    }

}
