package ca.landonjw.gooeylibs2.api.page;

import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.types.InventoryTemplate;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                      @Nullable net.minecraft.network.chat.Component title,
                      @Nullable Consumer<PageAction> onOpen,
                      @Nullable Consumer<PageAction> onClose,
                      @Nullable LinkedPage previous,
                      @Nullable LinkedPage next) {
        super(template, inventoryTemplate, title, onOpen, onClose);
        this.previous = previous;
        this.next = next;
    }

    public LinkedPage getPrevious() {
        return previous;
    }

    public void setPrevious(LinkedPage previous) {
        this.previous = previous;
        update();
    }

    public LinkedPage getNext() {
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
    public net.minecraft.network.chat.Component getTitle() {
        return replace(
                replace(super.getTitle(), Pattern.compile(CURRENT_PAGE_PLACEHOLDER, Pattern.LITERAL), "" + getCurrentPage()),
                Pattern.compile(TOTAL_PAGES_PLACEHOLDER, Pattern.LITERAL),
                "" + getTotalPages()
        );
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
        public Builder title(@Nullable net.minecraft.network.chat.Component title) {
            super.title(title);
            return this;
        }

        @Override
        public Builder title(@Nullable Component title) {
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

    private net.minecraft.network.chat.Component replace(net.minecraft.network.chat.Component parent, Pattern pattern, String replacement) {
        MutableComponent result;
        if(parent instanceof TextComponent) {
            TextComponent stc = (TextComponent) parent;
            String content = stc.getText();
            if (!content.isEmpty()) {
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    content = matcher.replaceAll(replacement);
                }

                result = new TextComponent(content);
                result.setStyle(parent.getStyle());
            } else {
                result = new TextComponent(stc.getText());
                result.setStyle(parent.getStyle());
            }
        } else {
            result = parent.plainCopy();
            result.setStyle(parent.getStyle());
        }

        List<TextComponent> children = parent.getSiblings().stream()
                .filter(c -> c instanceof TextComponent)
                .map(TextComponent.class::cast)
                .collect(Collectors.toList());
        for(TextComponent child : children) {
            result.append(this.replace(child, pattern, replacement));
        }

        return result;
    }

}
