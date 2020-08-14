package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.IButton;
import ca.landonjw.gooeylibs.api.button.LinkedPageButton;
import ca.landonjw.gooeylibs.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs.api.template.Template;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class LinkedPage extends Page {

	public static final String PAGE_NUMBER_PLACEHOLDER = "{page-number}";
	public static final String TOTAL_PAGES_PLACEHOLDER = "{total-pages}";

	private LinkedPage previousPage, nextPage;

	protected LinkedPage(@Nonnull LinkedPageBuilder builder) {
		super(builder);
		this.previousPage = builder.previousPage;
		this.nextPage = builder.nextPage;

		for(int i = 0; i < getTemplate().getSlots(); i++) {
			IButton button = getTemplate().getButton(i).orElse(null);
			if(button instanceof LinkedPageButton) {
				((LinkedPageButton) button).setPage(this);
			}
		}
		getTemplate().loadButtonDisplays();
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

	@Override
	public String getTitle() {
		return super.getTitle()
				.replace(PAGE_NUMBER_PLACEHOLDER, getPageNumber() + "")
				.replace(TOTAL_PAGES_PLACEHOLDER, getTotalPages() + "");
	}

	@Override
	public LinkedPageBuilder toBuilder() {
		return new LinkedPageBuilder(this);
	}

	public LinkedPage clone() {
		return new LinkedPageBuilder(this).build();
	}

	public static LinkedPageBuilder builder() {
		return new LinkedPageBuilder();
	}

	public static class LinkedPageBuilder extends Page.PageBuilder {

		private LinkedPage previousPage, nextPage;

		protected LinkedPageBuilder() {

		}

		protected LinkedPageBuilder(LinkedPage page) {
			super(page);
			this.previousPage = page.previousPage;
			this.nextPage = page.nextPage;
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

		public LinkedPageBuilder template(@Nonnull Template template) {
			super.template(template);
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

		public LinkedPage replacePlaceholders(@Nonnull Iterable<Button> replacements) {
			validateBuild();

			previousPage = null;
			nextPage = null;

			List<LinkedPage> generatedPages = Lists.newArrayList();
			Template originalTemplate = this.template;

			Iterator<Button> replacementIter = replacements.iterator();
			while(replacementIter.hasNext()) {

				this.template = replacePlaceholdersInTemplate(replacementIter, originalTemplate);
				LinkedPage page = new LinkedPage(this);

				if(!generatedPages.isEmpty()) {
					LinkedPage previousPage = generatedPages.get(generatedPages.size() - 1);
					page.setPreviousPage(previousPage);
					previousPage.setNextPage(page);
				}
				generatedPages.add(page);
			}

			for(LinkedPage page : generatedPages) {
				page.getTemplate().loadButtonDisplays();
			}
			return generatedPages.get(0);
		}

		private Template replacePlaceholdersInTemplate(Iterator<Button> replacementIter, Template originalTemplate) {
			Template.TemplateBuilder templateBuilder = new Template.TemplateBuilder(originalTemplate);
			for(int i = 0; i < template.getSlots(); i++) {
				if(!replacementIter.hasNext()) break;

				int buttonSlot = i;
				originalTemplate.getButton(i).ifPresent((button) -> {
					if(button instanceof PlaceholderButton) {
						templateBuilder.set(buttonSlot, replacementIter.next());
					}
				});
			}
			return templateBuilder.build();
		}

		public LinkedPage build() {
			super.validateBuild();
			return new LinkedPage(this);
		}

	}

}