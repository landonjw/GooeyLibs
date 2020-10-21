package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.data.EventEmitterBase;
import ca.landonjw.gooeylibs.api.template.ITemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractPage extends EventEmitterBase<IPage> implements IPage {

    protected ITemplate template;
    protected String title;

    public AbstractPage(@Nonnull ITemplate template, String title) {
        this.template = template;
        this.title = (title != null) ? title : "";
    }

    @Override
    public ITemplate getTemplate() {
        return template;
    }

    @Override
    public void setTemplate(@Nonnull ITemplate template) {
        this.template = template;
        this.emit(this);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(@Nullable String title) {
        this.title = (title == null) ? "" : title;
        this.emit(this);
    }

}