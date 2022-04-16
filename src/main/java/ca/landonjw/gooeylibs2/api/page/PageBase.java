package ca.landonjw.gooeylibs2.api.page;

import ca.landonjw.gooeylibs2.api.adventure.ForgeTranslator;
import ca.landonjw.gooeylibs2.api.data.EventEmitter;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.types.InventoryTemplate;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.TextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class PageBase implements Page {

    private final EventEmitter<Page> eventEmitter = new EventEmitter<>();
    private Template template;
    private InventoryTemplate inventoryTemplate;
    private net.minecraft.network.chat.Component title;

    public PageBase(@Nonnull Template template,
                    @Nullable InventoryTemplate inventoryTemplate,
                    @Nullable net.minecraft.network.chat.Component title) {
        this.template = template;
        this.inventoryTemplate = inventoryTemplate;
        this.title = (title != null) ? title : TextComponent.EMPTY;
    }

    public Template getTemplate() {
        if (template == null) throw new IllegalStateException("template could not be found on the page!");
        return template;
    }

    public void setTemplate(@Nonnull Template template) {
        this.template = template;
        update();
    }

    @Override
    public Optional<InventoryTemplate> getInventoryTemplate() {
        return Optional.ofNullable(inventoryTemplate);
    }

    public void setPlayerInventoryTemplate(@Nullable InventoryTemplate inventoryTemplate) {
        this.inventoryTemplate = inventoryTemplate;
    }

    public net.minecraft.network.chat.Component getTitle() {
        return this.title;
    }

    public void setTitle(@Nullable String title) {
        this.setTitle(title == null ? null : new TextComponent(title));
    }

    public void setTitle(@Nullable net.minecraft.network.chat.Component title) {
        this.title = (title == null) ? TextComponent.EMPTY : title;
        update();
    }

    public void setTitle(@Nullable Component title) {
        if(title == null) {
            return;
        }

        this.setTitle(ForgeTranslator.asMinecraft(title));
    }

    public void subscribe(@Nonnull Object observer, @Nonnull Consumer<Page> consumer) {
        this.eventEmitter.subscribe(observer, consumer);
    }

    public void unsubscribe(@Nonnull Object observer) {
        this.eventEmitter.unsubscribe(observer);
    }

    public void update() {
        this.eventEmitter.emit(this);
    }

}