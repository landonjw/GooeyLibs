package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public interface Page {

	Template getTemplate();

	String getTitle();

	int getPageNumber();

	int getTotalPages();

	Optional<Page> getPreviousPage();

	void setPreviousPage(@Nullable Page page);

	Optional<Page> getNextPage();

	void setNextPage(@Nullable Page page);

	Optional<Page> getPage(int pageNumber);

	void onOpen(@Nonnull PageAction action);

	void onClose(@Nonnull PageAction action);

	Page clone();

	static PageBuilder builder() {
		return new BasePage.BasePageBuilder();
	}

	interface PageBuilder {

		PageBuilder title(@Nullable String title);

		PageBuilder template(@Nonnull Template template);

		PageBuilder previousPage(@Nullable Page page);

		PageBuilder nextPage(@Nullable Page page);

		PageBuilder replacePlaceholders(@Nonnull Iterable<Button> buttons);

		PageBuilder onOpen(@Nonnull Consumer<PageAction> behaviour);

		PageBuilder onOpen(@Nonnull Runnable behaviour);

		PageBuilder onClose(@Nonnull Consumer<PageAction> behaviour);

		PageBuilder onClose(@Nonnull Runnable behaviour);

		PageBuilder reset();

		Page build();

	}

}
