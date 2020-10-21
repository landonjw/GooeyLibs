package ca.landonjw.gooeylibs.api.button;

import ca.landonjw.gooeylibs.api.page.IPage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ButtonAction {

	private final EntityPlayerMP player;
	private final ClickType clickType;
	private final IButton button;
	private final IPage page;

	public ButtonAction(@Nonnull EntityPlayerMP player,
	                    @Nonnull ClickType clickType,
	                    @Nonnull IButton button,
	                    @Nonnull IPage page) {
		this.player = Objects.requireNonNull(player);
		this.clickType = Objects.requireNonNull(clickType);
		this.button = Objects.requireNonNull(button);
		this.page = Objects.requireNonNull(page);
	}

	public EntityPlayerMP getPlayer() {
		return player;
	}

	public ClickType getClickType() {
		return clickType;
	}

	public IButton getButton() {
		return button;
	}

	public IPage getPage() {
		return page;
	}

}