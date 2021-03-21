package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.types.InventoryTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LinkedPage extends GooeyPage {

    private Page previous;
    private Page next;

    public LinkedPage(@Nonnull Template template,
                      @Nullable InventoryTemplate inventoryTemplate,
                      @Nullable String title,
                      @Nullable Consumer<PageAction> onOpen,
                      @Nullable Consumer<PageAction> onClose,
                      @Nullable Page previous,
                      @Nullable Page next) {
        super(template, inventoryTemplate, title, onOpen, onClose);
        this.previous = previous;
        this.next = next;
    }

    public Page getPrevious() {
        return previous;
    }

    public void setPrevious(Page previous) {
        this.previous = previous;
    }

    public Page getNext() {
        return next;
    }

    public void setNext(Page next) {
        this.next = next;
    }

    @Override
    public Template getTemplate() {
        return super.getTemplate();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GooeyPage.Builder {

        protected Page previousPage;
        protected Page nextPage;

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

        public Builder nextPage(@Nullable Page next) {
            this.nextPage = next;
            return this;
        }

        public Builder previousPage(@Nullable Page previous) {
            this.previousPage = previous;
            return this;
        }

        public LinkedPage build() {
            validate();
            return new LinkedPage(template, inventoryTemplate, title, onOpen, onClose, previousPage, nextPage);
        }

    }

}
