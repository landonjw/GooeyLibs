package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractPage implements Page {

	private Template template;
	private String title;
	private Page previousPage, nextPage;

	public AbstractPage(@Nonnull Template template,
	                    @Nonnull String title,
	                    @Nullable Page previousPage,
	                    @Nullable Page nextPage) {
		this.template = Objects.requireNonNull(template);
		this.title = Objects.requireNonNull(title);
		this.previousPage = previousPage;
		this.nextPage = nextPage;
	}

	@Override
	public Template getTemplate() {
		return template;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public int getPageNumber() {
		return (previousPage != null) ? previousPage.getPageNumber() + 1 : 1;
	}

	@Override
	public int getTotalPages() {
		return (nextPage != null) ? nextPage.getTotalPages() : getPageNumber();
	}

	@Override
	public Optional<Page> getPreviousPage() {
		return Optional.ofNullable(previousPage);
	}

	@Override
	public void setPreviousPage(@Nullable Page page) {
		this.previousPage = page;
	}

	@Override
	public Optional<Page> getNextPage() {
		return Optional.ofNullable(nextPage);
	}

	@Override
	public void setNextPage(@Nullable Page page) {
		this.nextPage = page;
	}

	@Override
	public Optional<Page> getPage(int pageNumber) {
		if(pageNumber < 0 || pageNumber > getTotalPages()) return Optional.empty();

		int pageDifference = pageNumber - getPageNumber();
		if(pageDifference == 0) return Optional.of(this);

		Page page = this;
		for(int i = 0; i < Math.abs(pageDifference); i++) {
			page = (pageDifference < 0) ? page.getPreviousPage().get() : page.getNextPage().get();
		}

		return Optional.of(page);
	}

	public abstract Page clone();

}
