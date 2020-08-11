package ca.landonjw.gooeylibs.api.button;

import ca.landonjw.gooeylibs.api.page.Page;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;

import javax.annotation.Nonnull;

public class ButtonAction {

	private EntityPlayerMP player;
	private ClickType clickType;
	private Button button;
	private Page page;

	public ButtonAction(@Nonnull EntityPlayerMP player,
	                    @Nonnull ClickType clickType,
	                    @Nonnull Button button,
	                    @Nonnull Page page) {
		this.player = player;
		this.clickType = clickType;
		this.button = button;
		this.page = page;
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