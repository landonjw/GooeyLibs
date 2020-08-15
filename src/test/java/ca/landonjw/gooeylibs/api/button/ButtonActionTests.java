package ca.landonjw.gooeylibs.api.button;

import ca.landonjw.gooeylibs.api.page.IPage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ButtonActionTests {

	private EntityPlayerMP player;
	private ClickType clickType;
	private IButton button;
	private IPage page;

	private ButtonActionTests() {
		this.player = mock(EntityPlayerMP.class);
		this.clickType = ClickType.QUICK_MOVE;
		this.button = mock(IButton.class);
		this.page = mock(IPage.class);
	}

	@Test
	void constructorTakesValidValues() {
		new ButtonAction(player, clickType, button, page);
	}

	@Test
	void constructorDoesNotTakeNullPlayer() {
		assertThrows(NullPointerException.class, () -> {
			new ButtonAction(null, clickType, button, page);
		});
	}

	@Test
	void constructorDoesNotTakeNullClickType() {
		assertThrows(NullPointerException.class, () -> {
			new ButtonAction(player, null, button, page);
		});
	}

	@Test
	void constructorDoesNotTakeNullButton() {
		assertThrows(NullPointerException.class, () -> {
			new ButtonAction(player, clickType, null, page);
		});
	}

	@Test
	void constructorDoesNotTakeNullPage() {
		assertThrows(NullPointerException.class, () -> {
			new ButtonAction(player, clickType, button, null);
		});
	}

	@Test
	void getPlayerReturnsGivenPlayer() {
		ButtonAction action = new ButtonAction(player, clickType, button, page);
		assertEquals(action.getPlayer(), player);
	}

	@Test
	void getClickTypeReturnsGivenClickType() {
		ButtonAction action = new ButtonAction(player, clickType, button, page);
		assertEquals(action.getClickType(), clickType);
	}

	@Test
	void getButtonReturnsGivenButton() {
		ButtonAction action = new ButtonAction(player, clickType, button, page);
		assertEquals(action.getButton(), button);
	}

	@Test
	void getPageReturnsGivenPage() {
		ButtonAction action = new ButtonAction(player, clickType, button, page);
		assertEquals(action.getPage(), page);
	}

}