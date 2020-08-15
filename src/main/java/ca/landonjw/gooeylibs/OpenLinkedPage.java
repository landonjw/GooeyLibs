package ca.landonjw.gooeylibs;

import ca.landonjw.gooeylibs.api.UIManager;
import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.LinkType;
import ca.landonjw.gooeylibs.api.button.LinkedPageButton;
import ca.landonjw.gooeylibs.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs.api.page.LinkedPage;
import ca.landonjw.gooeylibs.api.template.ChestTemplate;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class OpenLinkedPage extends CommandBase {

	@Override
	public String getName() {
		return "openlinkedpage";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/openlinkedpage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Button filler = Button.builder()
				.item(new ItemStack(Items.DIAMOND))
				.name("")
				.build();

		PlaceholderButton placeholder = PlaceholderButton.builder()
				.item(ItemStack.EMPTY)
				.name("")
				.build();

		LinkedPageButton next = LinkedPageButton.builder()
				.item(new ItemStack(Items.EMERALD))
				.name("Next")
				.linkType(LinkType.NEXT_PAGE)
				.build();

		LinkedPageButton previous = LinkedPageButton.builder()
				.item(new ItemStack(Items.EMERALD))
				.name("Previous")
				.linkType(LinkType.PREVIOUS_PAGE)
				.build();

		LinkedPageButton current = LinkedPageButton.builder()
				.item(new ItemStack(Items.EMERALD))
				.name(LinkedPage.PAGE_NUMBER_PLACEHOLDER + "/" + LinkedPage.TOTAL_PAGES_PLACEHOLDER)
				.build();

		List<Button> contents = Lists.newArrayList();
		for(int i = 1; i <= 50; i++) {
			Button button = Button.builder()
					.item(new ItemStack(Items.GOLDEN_APPLE))
					.name("Button" + i)
					.build();
			contents.add(button);
		}

		ChestTemplate template = ChestTemplate.builder(6)
				.border(0, 0, 6, 9, filler)
				.set(5, 0, previous)
				.set(5, 8, next)
				.set(5, 4, current)
				.fill(placeholder)
				.build();

		LinkedPage page = LinkedPage.builder()
				.template(template)
				.replacePlaceholders(contents);

		UIManager.openUIForcefully((EntityPlayerMP) sender, page);
	}

}
