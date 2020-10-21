package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.data.EventEmitter;
import ca.landonjw.gooeylibs.api.template.ITemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IPage extends EventEmitter<IPage> {

    ITemplate getTemplate();

    void setTemplate(@Nonnull ITemplate template);

    String getTitle();

    void setTitle(@Nullable String title);

    void onOpen(@Nonnull PageAction action);

    void onClose(@Nonnull PageAction action);

}