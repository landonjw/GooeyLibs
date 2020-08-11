package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BasePage extends AbstractPage {

	private Consumer<PageAction> openBehaviour, closeBehaviour;

	protected BasePage(BasePageBuilder builder) {
		super(builder.template, builder.title, builder.previousPage, builder.nextPage);
		this.openBehaviour = builder.openBehaviour;
		this.closeBehaviour = builder.closeBehaviour;
	}

	@Override
	public void onOpen(@Nonnull PageAction action) {
		if(openBehaviour != null) openBehaviour.accept(action);
	}

	@Override
	public void onClose(@Nonnull PageAction action) {
		if(closeBehaviour != null) closeBehaviour.accept(action);
	}

	@Override
	public Page clone() {
		return new BasePageBuilder()
				.title(getTitle())
				.template(getTemplate().clone())
				.nextPage(getNextPage().orElse(null))
				.previousPage(getPreviousPage().orElse(null))
				.onOpen(openBehaviour)
				.onClose(closeBehaviour)
				.build();
	}

	public PageBuilder toBuilder() {
		return new BasePageBuilder(this);
	}

	public static class BasePageBuilder implements PageBuilder {

		private String title = "";
		private Template template;
		private Page previousPage;
		private Page nextPage;
		private Consumer<PageAction> openBehaviour, closeBehaviour;

		public BasePageBuilder() {

		}

		public BasePageBuilder(BasePage page) {
			this.title = page.getTitle();
			this.template = page.getTemplate().clone();
			this.previousPage = page.getPreviousPage().orElse(null);
			this.nextPage = page.getNextPage().orElse(null);
			this.openBehaviour = page.openBehaviour;
			this.closeBehaviour = page.closeBehaviour;
		}

		@Override
		public BasePageBuilder title(@Nullable String title) {
			this.title = (title != null) ? title : "";
			return this;
		}

		@Override
		public BasePageBuilder template(@Nonnull Template template) {
			this.template = template;
			return this;
		}

		@Override
		public BasePageBuilder previousPage(@Nullable Page page) {
			this.previousPage = page;
			return this;
		}

		@Override
		public BasePageBuilder nextPage(@Nullable Page page) {
			this.nextPage = page;
			return this;
		}

		@Override
		public BasePageBuilder replacePlaceholders(@Nonnull Iterable<Button> buttons) {
			//TODO
			return this;
		}

		@Override
		public BasePageBuilder onOpen(@Nonnull Consumer<PageAction> behaviour) {
			this.openBehaviour = behaviour;
			return this;
		}

		@Override
		public BasePageBuilder onOpen(@Nonnull Runnable behaviour) {
			this.openBehaviour = (action) -> behaviour.run();
			return this;
		}

		@Override
		public BasePageBuilder onClose(@Nonnull Consumer<PageAction> behaviour) {
			this.closeBehaviour = behaviour;
			return this;
		}

		@Override
		public BasePageBuilder onClose(@Nonnull Runnable behaviour) {
			this.closeBehaviour = (action) -> behaviour.run();
			return this;
		}

		@Override
		public BasePageBuilder reset() {
			this.title = "";
			this.template = null;
			this.previousPage = null;
			this.nextPage = null;
			this.openBehaviour = null;
			this.closeBehaviour = null;
			return this;
		}

		@Override
		public BasePage build() {
			if(template == null) throw new IllegalStateException("page template must be defined");
			return new BasePage(this);
		}

	}

}
