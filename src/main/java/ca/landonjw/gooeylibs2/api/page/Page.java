package ca.landonjw.gooeylibs2.api.page;

import ca.landonjw.gooeylibs2.api.data.Subject;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.types.InventoryTemplate;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface Page extends Subject<Page> {

    Template getTemplate();

    default Optional<InventoryTemplate> getInventoryTemplate() {
        return Optional.empty();
    }

    String getTitle();

    default void onOpen(@Nonnull PageAction action) {
    }

    default void onClose(@Nonnull PageAction action) {
    }

}