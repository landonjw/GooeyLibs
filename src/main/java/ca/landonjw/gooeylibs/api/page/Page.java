package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class Page implements IPage {

	private Template template;
	private String title;
	private Consumer<PageAction> openBehaviour, closeBehaviour;

	protected Page(PageBuilder builder) {
		this.template = builder.template;
		this.title = builder.title;
		this.openBehaviour = builder.openBehaviour;
		this.closeBehaviour = builder.closeBehaviour;
		template.loadButtonDisplays();
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
	public void onOpen(@Nonnull PageAction action) {
		if(openBehaviour != null) openBehaviour.accept(action);
	}

	@Override
	public void onClose(@Nonnull PageAction action) {
		if(closeBehaviour != null) closeBehaviour.accept(action);
	}

	public PageBuilder toBuilder() {
		return new PageBuilder(this);
	}

	public Page clone() {
		return new PageBuilder(this).build();
	}

	public static PageBuilder builder() {
		return new PageBuilder();
	}

	public static class PageBuilder {

		private String title = "";
		protected Template template;
		private Consumer<PageAction> openBehaviour, closeBehaviour;

		protected PageBuilder() {

		}

		protected PageBuilder(Page page) {
			this.title = page.getTitle();
			this.template = page.getTemplate();
			this.openBehaviour = page.openBehaviour;
			this.closeBehaviour = page.closeBehaviour;
		}

		public PageBuilder title(@Nullable String title) {
			this.title = (title != null) ? title : "";
			return this;
		}

		public PageBuilder template(@Nonnull Template template) {
			this.template = template;
			return this;
		}

		public PageBuilder onOpen(@Nonnull Consumer<PageAction> behaviour) {
			this.openBehaviour = behaviour;
			return this;
		}

		public PageBuilder onOpen(@Nonnull Runnable behaviour) {
			this.openBehaviour = (action) -> behaviour.run();
			return this;
		}

		public PageBuilder onClose(@Nonnull Consumer<PageAction> behaviour) {
			this.closeBehaviour = behaviour;
			return this;
		}

		public PageBuilder onClose(@Nonnull Runnable behaviour) {
			this.closeBehaviour = (action) -> behaviour.run();
			return this;
		}

		public PageBuilder reset() {
			this.title = "";
			this.template = null;
			this.openBehaviour = null;
			this.closeBehaviour = null;
			return this;
		}

		public Page build() {
			validateBuild();
			return new Page(this);
		}

		protected void validateBuild() {
			if(template == null) throw new IllegalStateException("page template must be defined");
		}

	}

}
