package ca.landonjw.gooeylibs.api.button;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ButtonTest {

	private ItemStack stack;

	private ButtonTest() {
		Bootstrap.register();
		stack = new ItemStack(Items.DIAMOND);
	}

	@Test
	void buttonReturnsGivenItemStack() {
		Button button = Button.builder()
				.item(stack)
				.build();

		assertEquals(button.getDisplay(), stack);
	}

	@Test
	void throwsExceptionWhenGivenNullItemStack() {
		assertThrows(IllegalStateException.class, () -> {
			Button.builder()
					.item(null)
					.build();
		});
	}

	@Test
	void buttonReturnsGivenName() {
		String expectedName = "Hello";

		Button button = Button.builder()
				.item(stack)
				.name(expectedName)
				.build();

		assertEquals(button.getDisplay().getDisplayName(), expectedName);
	}

	@Test
	void buttonReturnsEmptyStringWhenGivenNull() {
		String expectedName = "";

		Button button = Button.builder()
				.item(stack)
				.name(null)
				.build();

		assertEquals(button.getDisplay().getDisplayName(), expectedName);
	}

	@Test
	void buttonReturnsDefaultNameWhenNotSet() {
		String expectedName = stack.getDisplayName();

		Button button = Button.builder()
				.item(stack)
				.build();

		assertEquals(button.getDisplay().getDisplayName(), expectedName);
	}

	@Test
	void buttonReturnsGivenLore() {
		List<String> expectedLore = Lists.newArrayList("1", "2", "3");

		Button button = Button.builder()
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
		Button button = Button.builder()
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

		Button button = Button.builder()
				.item(stack)
				.onClick(() -> foo.set(expectedValue))
				.build();

		button.onClick(null);
		assertEquals(foo.get(), expectedValue);
	}

	@Test
	void onClickInvokesGivenConsumer() {
		EntityPlayerMP[] finalPlayer = new EntityPlayerMP[1];
		EntityPlayerMP player = mock(EntityPlayerMP.class);

		Button button = Button.builder()
				.item(stack)
				.onClick((action) -> finalPlayer[0] = action.getPlayer())
				.build();
		button.onClick(new ButtonAction(player, null, null, null));
		assertEquals(player, finalPlayer[0]);
	}

}