package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.data.EventEmitter;
import ca.landonjw.gooeylibs.api.template.ITemplate;

import javax.annotation.Nonnull;

public interface IPage extends EventEmitter<IPage> {

    ITemplate getTemplate();

    String getTitle();

    default void onOpen(@Nonnull PageAction action) {
    }

    default void onClose(@Nonnull PageAction action) {
    }

}