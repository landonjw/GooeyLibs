package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.data.Subject;
import ca.landonjw.gooeylibs.api.template.Template;

import javax.annotation.Nonnull;

public interface Page extends Subject<Page> {

    Template getTemplate();

    String getTitle();

    default void onOpen(@Nonnull PageAction action) {
    }

    default void onClose(@Nonnull PageAction action) {
    }

}