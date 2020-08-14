package ca.landonjw.gooeylibs.api.button;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Button implements IButton {

	private ItemStack display;
	private Consumer<ButtonAction> clickBehaviour;

	protected Button(@Nonnull ButtonBuilder builder) {
		this.display = Objects.requireNonNull(builder.display);
		this.clickBehaviour = builder.clickBehaviour;
	}

	@Override
	public ItemStack getDisplay() {
		return display;
	}

	@Override
	public void onClick(ButtonAction action) {
		if(clickBehaviour != null) clickBehaviour.accept(action);
	}

	public Button clone() {
		return new ButtonBuilder(this).build();
	}

	public ButtonBuilder toBuilder() {
		return new ButtonBuilder(this);
	}

	public static ButtonBuilder builder() {
		return new ButtonBuilder();
	}

	public static class ButtonBuilder {

		private ItemStack display;
		private String name;
		private List<String> lore = Lists.newArrayList();
		private Consumer<ButtonAction> clickBehaviour;

		protected ButtonBuilder() {
		}

		protected ButtonBuilder(Button button) {
			this.display = button.getDisplay().copy();
			this.name = button.getDisplay().getDisplayName();
			this.clickBehaviour = button.clickBehaviour;
		}

		public ButtonBuilder item(@Nonnull ItemStack display) {
			this.display = display;
			return this;
		}

		public ButtonBuilder name(@Nullable String name) {
			this.name = (name != null) ? name : "";
			return this;
		}

		public ButtonBuilder lore(@Nullable List<String> lore) {
			this.lore = (lore != null) ? lore : Lists.newArrayList();
			return this;
		}

		public ButtonBuilder onClick(@Nonnull Consumer<ButtonAction> behaviour) {
			this.clickBehaviour = behaviour;
			return this;
		}

		public ButtonBuilder onClick(@Nonnull Runnable behaviour) {
			this.clickBehaviour = (action) -> behaviour.run();
			return this;
		}

		public ButtonBuilder reset() {
			display = null;
			name = "";
			lore = Lists.newArrayList();
			clickBehaviour = null;
			return this;
		}

		protected void validateBuild() {
			if(display == null) throw new IllegalStateException("button display must be defined");
			if(name != null) display.setStackDisplayName(name);

			if(!lore.isEmpty()) {
				NBTTagList nbtLore = new NBTTagList();
				for(String line : lore) {
					//If a line in the lore is null, just ignore it.
					if(line != null) {
						nbtLore.appendTag(new NBTTagString(line));
					}
				}
				display.getOrCreateSubCompound("display").setTag("Lore", nbtLore);
			}
			if(display.hasTagCompound()) {
				display.getTagCompound().setString("tooltip", "");
			}
		}

		public Button build() {
			validateBuild();
			return new Button(this);
		}

	}

}
