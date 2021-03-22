package ca.landonjw.gooeylibs.api.helpers;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs.api.page.LinkedPage;
import ca.landonjw.gooeylibs.api.page.PageAction;
import ca.landonjw.gooeylibs.api.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds various convenience methods for the generation and manipulation of {@link LinkedPage}s.
 *
 * @author landonjw
 */
public class PaginationHelper {

    /**
     * Takes a list of {@link LinkedPage} and connects them in the order supplied.
     *
     * @param pages list of pages in order to link
     */
    public static void linkPagesTogether(@Nonnull List<LinkedPage> pages) {
        for (int i = 0; i < pages.size(); i++) {
            if (i != 0) {
                pages.get(i).setPrevious(pages.get(i - 1));
            }
            if (i != pages.size() - 1) {
                pages.get(i).setNext(pages.get(i + 1));
            }
        }
    }

    /**
     * Generates one or many {@link LinkedPage}s, replacing any {@link PlaceholderButton}s
     * in the supplied {@link Template} with buttons from the supplied list of buttons in their given order.
     * <p>
     * You may optionally supply a {@link LinkedPage.Builder} to specify properties of the constructed pages.
     * An example usecase of using this would be for paginating a collection of items to choose from.
     * You would give the template of the page, and the list of items to put in, and this would
     * create as many pages as are required to display those items and link them together.
     * <p>
     * If you have a template with 5 placeholder spots, and supply a list of 15 buttons,
     * this would create 3 {@link LinkedPage}s and link them together.
     * <p>
     * If you have a template with 5 placeholder spots, and supply a list of 18 buttons,
     * this would create 4 {@link LinkedPage}s and link them together.
     *
     * @param template    The {@link Button} of the pages to create.
     *                    Any buttons of type {@link PlaceholderButton} will be replaced with buttons
     *                    in the toReplace parameter.
     * @param toReplace   A list of buttons that are to be added to the template.
     * @param pageBuilder An optional builder to generate the pages from.
     *                    This can be used to add additional functionality to the pages,
     *                    for example specifying {@link LinkedPage#onClose(PageAction)}
     * @return the first page generated, with all pages linked together.
     */
    public static LinkedPage createPagesFromPlaceholders(@Nonnull Template template,
                                                         @Nonnull List<Button> toReplace,
                                                         @Nullable LinkedPage.Builder pageBuilder) {
        // Get all the indexes of placeholder buttons.
        List<Integer> placeholderIndexes = new ArrayList<>();
        for (int i = 0; i < template.getSize(); i++) {
            if (template.getSlot(i).getButton().orElse(null) instanceof PlaceholderButton) {
                placeholderIndexes.add(i);
            }
        }

        // If page builder isn't specified, just create a default builder.
        LinkedPage.Builder builder = (pageBuilder != null) ? pageBuilder : LinkedPage.builder();

        List<LinkedPage> pages = new ArrayList<>();
        int currentIndex = 0; // Stores the index of the current button being replaced
        while (currentIndex < toReplace.size()) {
            // Clones the template, and replaces all placeholders with buttons from toReplace.
            Template replacement = template.clone();
            for (int i = 0; i < placeholderIndexes.size(); i++) {
                int targetIndex = placeholderIndexes.get(i);
                if (currentIndex >= toReplace.size()) {
                    break;
                }
                replacement.getSlot(targetIndex).setButton(toReplace.get(currentIndex));
                currentIndex++;
            }
            pages.add(builder.template(replacement).build());
        }

        linkPagesTogether(pages);
        return pages.get(0);
    }

}
