package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.button.IButton;
import ca.landonjw.gooeylibs.api.template.ITemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class LinkedPage extends Page {

	private LinkedPage previousPage, nextPage;

	protected LinkedPage(@Nonnull LinkedPageBuilder builder) {
		super(builder);
		this.previousPage = builder.previousPage;
		this.nextPage = builder.nextPage;
	}

	public int getPageNumber() {
		return (previousPage != null) ? previousPage.getPageNumber() + 1 : 1;
	}

	public int getTotalPages() {
		return (nextPage != null) ? nextPage.getTotalPages() : getPageNumber();
	}

	public Optional<LinkedPage> getPreviousPage() {
		return Optional.ofNullable(previousPage);
	}

	public void setPreviousPage(@Nullable LinkedPage page) {
		this.previousPage = page;
	}

	public Optional<LinkedPage> getNextPage() {
		return Optional.ofNullable(nextPage);
	}

	public void setNextPage(@Nullable LinkedPage page) {
		this.nextPage = page;
	}

	public Optional<LinkedPage> getPage(int pageNumber) {
		if(pageNumber < 0 || pageNumber > getTotalPages()) return Optional.empty();

		int pageDifference = pageNumber - getPageNumber();
		if(pageDifference == 0) return Optional.of(this);

		LinkedPage page = this;
		for(int i = 0; i < Math.abs(pageDifference); i++) {
			page = (pageDifference < 0) ? page.getPreviousPage().get() : page.getNextPage().get();
		}

		return Optional.of(page);
	}

	public static class LinkedPageBuilder extends Page.PageBuilder {

		private LinkedPage previousPage, nextPage;

		public LinkedPageBuilder() {

		}

		public LinkedPageBuilder(LinkedPage page) {
			super(page);
		}

		public LinkedPageBuilder previousPage(@Nullable LinkedPage previousPage) {
			this.previousPage = previousPage;
			return this;
		}

		public LinkedPageBuilder nextPage(@Nullable LinkedPage nextPage) {
			this.nextPage = nextPage;
			return this;
		}

		public LinkedPageBuilder title(@Nullable String title) {
			super.title(title);
			return this;
		}

		public LinkedPageBuilder template(@Nonnull ITemplate template) {
			super.template(template);
			return this;
		}

		public LinkedPageBuilder replacePlaceholders(@Nonnull Iterable<IButton> buttons) {
			//TODO
			return this;
		}

		public LinkedPageBuilder onOpen(@Nonnull Consumer<PageAction> behaviour) {
			super.onOpen(behaviour);
			return this;
		}

		public LinkedPageBuilder onOpen(@Nonnull Runnable behaviour) {
			super.onOpen(behaviour);
			return this;
		}

		public LinkedPageBuilder onClose(@Nonnull Consumer<PageAction> behaviour) {
			super.onClose(behaviour);
			return this;
		}

		public LinkedPageBuilder onClose(@Nonnull Runnable behaviour) {
			super.onClose(behaviour);
			return this;
		}

		public LinkedPageBuilder reset() {
			super.reset();
			this.previousPage = null;
			this.nextPage = null;
			return this;
		}

		public LinkedPage build() {
			super.validateBuild();
			return new LinkedPage(this);
		}

	}

}
