package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class PlaceholderButton extends Button {

	private String identifier;

	protected PlaceholderButton(@Nonnull PlaceholderButtonBuilder builder) {
		super(builder);
		this.identifier = builder.identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public Button clone() {
		return new PlaceholderButtonBuilder(this).build();
	}

	@Override
	public ButtonBuilder toBuilder() {
		return new PlaceholderButtonBuilder();
	}

	public static PlaceholderButtonBuilder builder() {
		return new PlaceholderButtonBuilder();
	}

	public static class PlaceholderButtonBuilder extends ButtonBuilder {

		private String identifier = "default";

		public PlaceholderButtonBuilder() {
		}

		public PlaceholderButtonBuilder(PlaceholderButton button) {
			super(button);
			this.identifier = button.identifier;
		}

		public PlaceholderButtonBuilder identifier(@Nonnull String identifier) {
			this.identifier = identifier;
			return this;
		}

		@Override
		public PlaceholderButtonBuilder item(ItemStack display) {
			super.item(display);
			return this;
		}

		@Override
		public PlaceholderButtonBuilder name(@Nullable String name) {
			super.name(name);
			return this;
		}

		@Override
		public PlaceholderButtonBuilder lore(@Nullable List<String> lore) {
			super.lore(lore);
			return this;
		}

		@Override
		public PlaceholderButtonBuilder onClick(@Nonnull Consumer<ButtonAction> behaviour) {
			super.onClick(behaviour);
			return this;
		}

		@Override
		public PlaceholderButtonBuilder onClick(@Nonnull Runnable behaviour) {
			super.onClick(behaviour);
			return this;
		}

		@Override
		public PlaceholderButtonBuilder reset() {
			super.reset();
			this.identifier = "default";
			return this;
		}

		@Override
		public PlaceholderButton build() {
			validateBuild();
			return new PlaceholderButton(this);
		}

	}

}