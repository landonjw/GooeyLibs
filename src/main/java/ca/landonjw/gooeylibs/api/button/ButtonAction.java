package ca.landonjw.gooeylibs.api.button;

import ca.landonjw.gooeylibs.api.page.Page;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ButtonAction {

	private final EntityPlayerMP player;
	private final ClickType clickType;
	private final Button button;
	private final Page page;

	public ButtonAction(@Nonnull EntityPlayerMP player,
						@Nonnull ClickType clickType,
						@Nonnull Button button,
						@Nonnull Page page) {
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

	public Button getButton() {
		return button;
	}

	public Page getPage() {
		return page;
	}

}