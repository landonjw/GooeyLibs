package ca.landonjw.gooeylibs2.api.page;

import ca.landonjw.gooeylibs2.api.adventure.ForgeTranslator;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.types.InventoryTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class GooeyPage extends PageBase {

    private final Consumer<PageAction> onOpen, onClose;

    public GooeyPage(@Nonnull Template template,
                     @Nullable InventoryTemplate inventoryTemplate,
                     @Nullable ITextComponent title,
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

        protected ITextComponent title;
        protected Template template;
        protected InventoryTemplate inventoryTemplate;
        protected Consumer<PageAction> onOpen, onClose;

        public Builder title(@Nullable ITextComponent title) {
            this.title = title;
            return this;
        }

        public Builder title(@Nullable Component title) {
            if(title == null) {
                return this;
            }

            return this.title(ForgeTranslator.asMinecraft(title));
        }

        public Builder template(@Nonnull Template template) {
            if (template instanceof InventoryTemplate) {
                throw new IllegalArgumentException("you can not use an inventory template here!");
            }
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
            validate();
            return new GooeyPage(template, inventoryTemplate, title, onOpen, onClose);
        }

        protected void validate() {
            if (template == null) {
                throw new IllegalStateException("template must be defined");
            }
        }

    }

}