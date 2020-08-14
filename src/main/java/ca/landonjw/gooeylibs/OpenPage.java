package ca.landonjw.gooeylibs;

import ca.landonjw.gooeylibs.api.UIManager;
import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.LinkType;
import ca.landonjw.gooeylibs.api.button.LinkedPageButton;
import ca.landonjw.gooeylibs.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs.api.page.LinkedPage;
import ca.landonjw.gooeylibs.api.template.Template;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class OpenPage extends CommandBase {

	@Override
	public String getName() {
		return "openpage";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/openpage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		PlaceholderButton placeholder = PlaceholderButton.builder()
				.item(new ItemStack(Items.DIAMOND))
				.name("Placeholder")
				.build();

		List<Button> someButtons = Lists.newArrayList();
		for(int i = 0; i < 100; i++) {
			Button button = Button.builder()
					.item(new ItemStack(Items.GOLDEN_APPLE))
					.name(i + "")
					.build();
			someButtons.add(button);
		}

		LinkedPageButton previous = LinkedPageButton.builder()
				.item(new ItemStack(Items.EMERALD))
				.linkType(LinkType.PREVIOUS_PAGE)
				.name("Previous Page")
				.build();

		LinkedPageButton next = LinkedPageButton.builder()
				.item(new ItemStack(Items.EMERALD))
				.linkType(LinkType.NEXT_PAGE)
				.name("Next Page")
				.build();

		LinkedPageButton current = LinkedPageButton.builder()
				.item(new ItemStack(Items.EMERALD))
				.name(LinkedPage.PAGE_NUMBER_PLACEHOLDER + "/" + LinkedPage.TOTAL_PAGES_PLACEHOLDER)
				.build();

		Template template = Template.builder(3)
				.fill(placeholder)
				.set(2, 0, previous)
				.set(2, 8, next)
				.set(2, 4, current)
				.build();

		LinkedPage page1 = LinkedPage.builder()
				.title(LinkedPage.PAGE_NUMBER_PLACEHOLDER + "/" + LinkedPage.TOTAL_PAGES_PLACEHOLDER)
				.template(template)
				.replacePlaceholders(someButtons);
		System.out.println(page1.getPageNumber());

		UIManager.openUIForcefully((EntityPlayerMP) sender, page1);
	}

}
