package ca.landonjw.gooeylibs2.api.page;

import ca.landonjw.gooeylibs2.api.data.EventEmitter;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.types.InventoryTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class PageBase implements Page {

    private final EventEmitter<Page> eventEmitter = new EventEmitter<>();
    private Template template;
    private InventoryTemplate inventoryTemplate;
    private String title;

    public PageBase(@Nonnull Template template,
                    @Nullable InventoryTemplate inventoryTemplate,
                    @Nullable String title) {
        this.template = template;
        this.inventoryTemplate = inventoryTemplate;
        this.title = (title != null) ? title : "";
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

    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = (title == null) ? "" : title;
        update();
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