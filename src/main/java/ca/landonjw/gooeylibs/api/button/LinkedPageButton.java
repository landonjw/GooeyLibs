package ca.landonjw.gooeylibs.api.button;

import ca.landonjw.gooeylibs.api.UIManager;
import ca.landonjw.gooeylibs.api.page.LinkedPage;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class LinkedPageButton extends Button {

	private LinkType linkType;
	private LinkedPage page;

	protected LinkedPageButton(@Nonnull LinkedPageButtonBuilder builder) {
		super(builder);
		this.linkType = builder.linkType;
	}

	public void setPage(@Nonnull LinkedPage page) {
		this.page = page;
	}

	public LinkedPage getPage() {
		return page;
	}

	@Override
	public ItemStack getDisplay() {
		ItemStack display = super.getDisplay();
		if(page == null) return display;

		String replacedDisplayName = display.getDisplayName()
				.replace(LinkedPage.PAGE_NUMBER_PLACEHOLDER, page.getPageNumber() + "")
				.replace(LinkedPage.TOTAL_PAGES_PLACEHOLDER, page.getTotalPages() + "");
		return display.copy().setStackDisplayName(replacedDisplayName);
	}

	@Override
	public void onClick(ButtonAction action) {
		super.onClick(action);

		if(action.getPage() instanceof LinkedPage && linkType != null) {
			LinkedPage linkedPage = (LinkedPage) action.getPage();
			if(linkType == LinkType.NEXT_PAGE) {
				linkedPage.getNextPage().ifPresent((page) -> {
					UIManager.openUIForcefully(action.getPlayer(), page);
				});
			}
			else {
				linkedPage.getPreviousPage().ifPresent((page) -> {
					UIManager.openUIForcefully(action.getPlayer(), page);
				});
			}
		}
	}

	@Override
	public LinkedPageButton clone() {
		return new LinkedPageButtonBuilder(this).build();
	}

	public LinkedPageButtonBuilder toBuilder() {
		return new LinkedPageButtonBuilder(this);
	}

	public static LinkedPageButtonBuilder builder() {
		return new LinkedPageButtonBuilder();
	}

	public static class LinkedPageButtonBuilder extends ButtonBuilder {

		private LinkType linkType;

		protected LinkedPageButtonBuilder() {
		}

		protected LinkedPageButtonBuilder(LinkedPageButton button) {
			super(button);
			this.linkType = button.linkType;
		}

		public LinkedPageButtonBuilder linkType(@Nullable LinkType linkType) {
			this.linkType = linkType;
			return this;
		}

		@Override
		public LinkedPageButtonBuilder item(@Nonnull ItemStack display) {
			super.item(display);
			return this;
		}

		@Override
		public LinkedPageButtonBuilder name(@Nullable String name) {
			super.name(name);
			return this;
		}

		@Override
		public LinkedPageButtonBuilder lore(@Nullable List<String> lore) {
			super.lore(lore);
			return this;
		}

		@Override
		public LinkedPageButtonBuilder onClick(@Nonnull Consumer<ButtonAction> behaviour) {
			super.onClick(behaviour);
			return this;
		}

		@Override
		public LinkedPageButtonBuilder onClick(@Nonnull Runnable behaviour) {
			super.onClick(behaviour);
			return this;
		}

		@Override
		public LinkedPageButtonBuilder reset() {
			super.reset();
			return this;
		}

		@Override
		protected void validateBuild() {
			super.validateBuild();
		}

		@Override
		public LinkedPageButton build() {
			validateBuild();
			return new LinkedPageButton(this);
		}

	}

}