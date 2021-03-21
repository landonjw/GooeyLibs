package ca.landonjw.gooeylibs.api.helpers;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs.api.page.LinkedPage;
import ca.landonjw.gooeylibs.api.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LinkedPageHelper {

    public static void linkPagesTogether(List<LinkedPage> pages) {
        for (int i = 0; i < pages.size(); i++) {
            if (i != 0) {
                pages.get(i).setPrevious(pages.get(i - 1));
            }
            if (i != pages.size() - 1) {
                pages.get(i).setNext(pages.get(i + 1));
            }
        }
    }

    public static LinkedPage generateLinkedPagesFromButtons(@Nonnull Template template,
                                                            @Nonnull List<Button> toReplace,
                                                            @Nullable LinkedPage.Builder pageBuilder) {
        List<Integer> placeholderIndexes = new ArrayList<>();
        for (int i = 0; i < template.getSize(); i++) {
            if (template.getSlot(i).getButton().orElse(null) instanceof PlaceholderButton) {
                placeholderIndexes.add(i);
            }
        }

        List<LinkedPage> pages = new ArrayList<>();

        int currentIndex = 0;
        LinkedPage.Builder builder = (pageBuilder != null) ? pageBuilder : LinkedPage.builder();
        while (currentIndex < toReplace.size()) {
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
