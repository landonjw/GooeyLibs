package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public interface Button {

	ItemStack getDisplay();

	void onClick(@Nonnull ButtonAction action);

	Button clone();

	static ButtonBuilder builder() {
		return new BaseButton.BaseButtonBuilder();
	}

	interface ButtonBuilder {

		ButtonBuilder display(@Nonnull ItemStack display);

		ButtonBuilder name(@Nullable String name);

		ButtonBuilder lore(@Nullable List<String> lore);

		ButtonBuilder onClick(@Nonnull Consumer<ButtonAction> behaviour);

		ButtonBuilder onClick(@Nonnull Runnable behaviour);

		ButtonBuilder reset();

		Button build();

	}

}
