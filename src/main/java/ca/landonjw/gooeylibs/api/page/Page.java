package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.template.ITemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class Page extends AbstractPage {

    private final Consumer<PageAction> onOpen, onClose;

    public Page(@Nonnull ITemplate template, @Nullable String title, @Nullable Consumer<PageAction> onOpen, @Nullable Consumer<PageAction> onClose) {
        super(template, title);
        this.onOpen = onOpen;
        this.onClose = onClose;
    }

    @Override
    public void onOpen(@Nonnull PageAction action) {
        if(onOpen != null) onOpen.accept(action);
    }

    @Override
    public void onClose(@Nonnull PageAction action) {
        if(onClose != null) onClose.accept(action);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String title;
        private ITemplate template;
        private Consumer<PageAction> onOpen, onClose;

        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        public Builder template(@Nonnull ITemplate template) {
            this.template = template;
            return this;
        }

        public Builder onOpen(@Nullable Consumer<PageAction> behaviour) {
            this.onOpen = behaviour;
            return this;
        }

        public Builder onOpen(@Nullable Runnable behaviour) {
            if(behaviour == null) {
                this.onOpen = null;
            }
            else{
                onOpen((action) -> behaviour.run());
            }
            return this;
        }

        public Builder onClose(@Nullable Consumer<PageAction> behaviour) {
            this.onClose = behaviour;
            return this;
        }

        public Builder onClose(@Nullable Runnable behaviour) {
            if(behaviour == null) {
                this.onClose = null;
            }
            else{
                onClose((action) -> behaviour.run());
            }
            return this;
        }

        public Page build() {
            if(template == null) throw new IllegalStateException("template must be defined");
            return new Page(template, title, onOpen, onClose);
        }

    }

}