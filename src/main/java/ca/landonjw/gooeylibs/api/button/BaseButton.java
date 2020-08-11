package ca.landonjw.gooeylibs.api.button;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class BaseButton extends AbstractButton {

	private Consumer<ButtonAction> clickBehaviour;

	protected BaseButton(BaseButtonBuilder builder) {
		super(builder.display);
		this.clickBehaviour = builder.clickBehaviour;
	}

	@Override
	public void onClick(@Nonnull ButtonAction action) {
		if(clickBehaviour != null) clickBehaviour.accept(action);
	}

	@Override
	public Button clone() {
		return new BaseButtonBuilder()
				.display(getDisplay().copy())
				.name(getDisplay().getDisplayName())
				.onClick(clickBehaviour)
				.build();
	}

	public ButtonBuilder toBuilder() {
		return new BaseButtonBuilder(this);
	}

	public static class BaseButtonBuilder implements ButtonBuilder {

		private ItemStack display;
		private String name = "";
		private List<String> lore = Lists.newArrayList();
		private Consumer<ButtonAction> clickBehaviour;

		public BaseButtonBuilder() {

		}

		public BaseButtonBuilder(BaseButton button) {
			this.display = button.getDisplay().copy();
			this.name = button.getDisplay().getDisplayName();
			this.clickBehaviour = button.clickBehaviour;
		}

		@Override
		public ButtonBuilder display(ItemStack display) {
			this.display = display;
			return this;
		}

		@Override
		public BaseButtonBuilder name(@Nullable String name) {
			this.name = (name != null) ? name : "";
			return this;
		}

		@Override
		public BaseButtonBuilder lore(@Nullable List<String> lore) {
			this.lore = (lore != null) ? lore : Lists.newArrayList();
			return this;
		}

		@Override
		public BaseButtonBuilder onClick(@Nonnull Consumer<ButtonAction> behaviour) {
			this.clickBehaviour = behaviour;
			return this;
		}

		@Override
		public BaseButtonBuilder onClick(@Nonnull Runnable behaviour) {
			this.clickBehaviour = (action) -> behaviour.run();
			return this;
		}

		@Override
		public BaseButtonBuilder reset() {
			display = null;
			name = "";
			lore = Lists.newArrayList();
			clickBehaviour = null;
			return this;
		}

		@Override
		public BaseButton build() {
			if(display == null) throw new IllegalStateException("button display must be defined");

			display.setStackDisplayName(name);

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
			display.getTagCompound().setString("tooltip", "");

			return new BaseButton(this);
		}

	}

}
