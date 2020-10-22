package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.data.EventEmitter;
import ca.landonjw.gooeylibs.api.data.Subject;
import ca.landonjw.gooeylibs.api.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class Page implements Subject<Page> {

    private final EventEmitter<Page> eventEmitter = new EventEmitter<>();
    private Template template;
    private String title;

    protected Page() {
        this.title = "";
    }

    public Page(@Nonnull Template template, @Nullable String title) {
        this.template = template;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = (title == null) ? "" : title;
        update();
    }

    public void onOpen(@Nonnull PageAction action) {
    }

    public void onClose(@Nonnull PageAction action) {
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