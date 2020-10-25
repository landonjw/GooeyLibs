package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.types.InventoryTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class GooeyPage extends PageBase {

    private final Consumer<PageAction> onOpen, onClose;

    public GooeyPage(@Nonnull Template template,
                     @Nullable InventoryTemplate inventoryTemplate,
                     @Nullable String title,
                     @Nullable Consumer<PageAction> onOpen,
                     @Nullable Consumer<PageAction> onClose) {
        super(template, inventoryTemplate, title);
        this.onOpen = onOpen;
        this.onClose = onClose;
    }

    @Override
    public void onOpen(@Nonnull PageAction action) {
        if (onOpen != null) onOpen.accept(action);
    }

    @Override
    public void onClose(@Nonnull PageAction action) {
        if (onClose != null) onClose.accept(action);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String title;
        private Template template;
        private InventoryTemplate inventoryTemplate;
        private Consumer<PageAction> onOpen, onClose;

        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        public Builder template(@Nonnull Template template) {
            this.template = template;
            return this;
        }

        public Builder inventory(@Nullable InventoryTemplate template) {
            this.inventoryTemplate = template;
            return this;
        }

        public Builder onOpen(@Nullable Consumer<PageAction> behaviour) {
            this.onOpen = behaviour;
            return this;
        }

        public Builder onOpen(@Nullable Runnable behaviour) {
            if (behaviour == null) {
                this.onOpen = null;
            } else {
                onOpen((action) -> behaviour.run());
            }
            return this;
        }

        public Builder onClose(@Nullable Consumer<PageAction> behaviour) {
            this.onClose = behaviour;
            return this;
        }

        public Builder onClose(@Nullable Runnable behaviour) {
            if (behaviour == null) {
                this.onClose = null;
            } else {
                onClose((action) -> behaviour.run());
            }
            return this;
        }

        public GooeyPage build() {
            if (template == null) throw new IllegalStateException("template must be defined");
            return new GooeyPage(template, inventoryTemplate, title, onOpen, onClose);
        }

    }

}