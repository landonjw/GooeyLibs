package ca.landonjw.gooeylibs.api.button;

import ca.landonjw.gooeylibs.api.page.IPage;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class LinkedPageButtonTests {

	private ItemStack stack;

	private LinkedPageButtonTests() {
		Bootstrap.register();
		stack = new ItemStack(Items.DIAMOND);
	}

	@Test
	void buttonReturnsGivenItemStack() {
		LinkedPageButton button = LinkedPageButton.builder()
				.item(stack)
				.build();

		assertEquals(button.getDisplay(), stack);
	}

	@Test
	void throwsExceptionWhenGivenNullItemStack() {
		assertThrows(IllegalStateException.class, () -> {
			LinkedPageButton.builder()
					.item(null)
					.build();
		});
	}

	@Test
	void buttonReturnsGivenName() {
		String expectedName = "Hello";

		LinkedPageButton button = LinkedPageButton.builder()
				.item(stack)
				.name(expectedName)
				.build();

		assertEquals(button.getDisplay().getDisplayName(), expectedName);
	}

	@Test
	void buttonReturnsEmptyStringWhenGivenNull() {
		String expectedName = "";

		LinkedPageButton button = LinkedPageButton.builder()
				.item(stack)
				.name(null)
				.build();

		assertEquals(button.getDisplay().getDisplayName(), expectedName);
	}

	@Test
	void buttonReturnsDefaultNameWhenNotSet() {
		String expectedName = stack.getDisplayName();

		LinkedPageButton button = LinkedPageButton.builder()
				.item(stack)
				.build();

		assertEquals(button.getDisplay().getDisplayName(), expectedName);
	}

	@Test
	void buttonReturnsGivenLore() {
		List<String> expectedLore = Lists.newArrayList("1", "2", "3");

		LinkedPageButton button = LinkedPageButton.builder()
				.item(stack)
				.lore(expectedLore)
				.build();

		List<String> actualLore = Lists.newArrayList();
		button.getDisplay().getTagCompound().getCompoundTag("display").getTagList("Lore", 8).forEach((lore) -> {
			actualLore.add(((NBTTagString) lore).getString());
		});

		for(int i = 0; i < expectedLore.size(); i++) {
			assertEquals(expectedLore.get(i), actualLore.get(i));
		}
	}

	@Test
	void buttonReturnsNoLoreWhenGivenNullLore() {
		LinkedPageButton button = LinkedPageButton.builder()
				.item(stack)
				.lore(null)
				.build();

		if(button.getDisplay().getTagCompound() != null) {
			assert (button.getDisplay().getTagCompound().getCompoundTag("display").getTagList("Lore", 8).isEmpty());
		}
	}

	@Test
	void onClickInvokesGivenRunnable() {
		int expectedValue = 1;
		AtomicInteger foo = new AtomicInteger();

		LinkedPageButton button = LinkedPageButton.builder()
				.item(stack)
				.onClick(() -> foo.set(expectedValue))
				.build();

		EntityPlayerMP player = mock(EntityPlayerMP.class);
		ClickType clickType = ClickType.QUICK_MOVE;
		IPage page = mock(IPage.class);

		button.onClick(new ButtonAction(player, clickType, button, page));
		assertEquals(foo.get(), expectedValue);
	}

	@Test
	void onClickInvokesGivenConsumer() {
		EntityPlayerMP[] finalPlayer = new EntityPlayerMP[1];
		EntityPlayerMP player = mock(EntityPlayerMP.class);

		LinkedPageButton button = LinkedPageButton.builder()
				.item(stack)
				.onClick((action) -> finalPlayer[0] = action.getPlayer())
				.build();

		ClickType clickType = ClickType.QUICK_MOVE;
		IPage page = mock(IPage.class);

		button.onClick(new ButtonAction(player, clickType, button, page));
		assertEquals(player, finalPlayer[0]);
	}

}