package ca.landonjw.gooeylibs.commands;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.GooeyButton;
import ca.landonjw.gooeylibs.api.button.RateLimitedButton;
import ca.landonjw.gooeylibs.api.data.UpdateEmitter;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs.api.template.types.InventoryTemplate;
import ca.landonjw.gooeylibs.implementation.tasks.Task;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RateLimitPage extends UpdateEmitter<Page> implements Page {

    private final ChestTemplate template;
    private final InventoryTemplate playerTemplate;
    private String title = "Rate Limit Demonstration";

    private final Button greenPane = GooeyButton.builder()
            .display(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.GREEN.getMetadata()))
            .build();

    private final Button yellowPane = GooeyButton.builder()
            .display(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.YELLOW.getMetadata()))
            .build();

    private final Button redPane = GooeyButton.builder()
            .display(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.RED.getMetadata()))
            .build();

    private final RateLimitedButton rateLimitedButton;

    private static int tracker = 0;

    public RateLimitPage() {
        Button clickMe = GooeyButton.builder()
                .display(new ItemStack(Items.SLIME_BALL))
                .title("Click me!")
                .onClick(() -> System.out.println(tracker++))
                .build();

        this.rateLimitedButton = RateLimitedButton.builder()
                .button(clickMe)
                .limit(9)
                .interval(3, TimeUnit.SECONDS)
                .build();

        this.template = ChestTemplate.builder(6).build();
        this.playerTemplate = InventoryTemplate.builder()
                .set(3, 4, clickMe)
                .build();
        Task.builder()
                .execute(() -> updateBar())
                .infinite()
                .interval(1)
                .build();
    }

    private void updateBar() {
        int clicksUsed = 9 - rateLimitedButton.getAllowedRemainingClicks();
        int rowsFilled = (int) Math.ceil((clicksUsed / 9.0) * 9);
        int rowsNotFilled = 9 - rowsFilled;
        for (int row = 0; row < Math.min(rowsNotFilled, 6); row++) {
            for (int col = 0; col < 9; col++) {
                template.getSlot(row, col).setButton(null);
            }
        }
        if (rowsNotFilled > 6) {
            for (int row = 0; row < Math.min(rowsNotFilled - 6, 4); row++) {
                for (int col = 0; col < 9; col++) {
                    playerTemplate.getSlot(row, col).setButton(null);
                }
            }
        }
        for (int row = 0; row < Math.min(rowsFilled, 3); row++) {
            for (int col = 0; col < 9; col++) {
                if (!playerTemplate.getSlot(2 - row, col).getButton().isPresent()) {
                    playerTemplate.getSlot(2 - row, col).setButton(greenPane);
                }
            }
        }
        if (rowsFilled > 3) {
            for (int row = 0; row < Math.min(rowsFilled - 3, 3); row++) {
                for (int col = 0; col < 9; col++) {
                    if (!template.getSlot(5 - row, col).getButton().isPresent()) {
                        template.getSlot(5 - row, col).setButton(yellowPane);
                    }
                }
            }
        }
        if (rowsFilled > 6) {
            for (int row = 0; row < Math.min(rowsFilled - 6, 3); row++) {
                for (int col = 0; col < 9; col++) {
                    if (!template.getSlot(2 - row, col).getButton().isPresent()) {
                        template.getSlot(2 - row, col).setButton(redPane);
                    }
                }
            }
        }
        if (clicksUsed == 9 && !title.equals(TextFormatting.RED + "Currently rate limited!")) {
            this.title = TextFormatting.RED + "Currently rate limited!";
            update();
        } else if (clicksUsed != 9 && title.equals(TextFormatting.RED + "Currently rate limited!")) {
            this.title = "Rate Limit Demonstration";
            update();
        }
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Optional<InventoryTemplate> getInventoryTemplate() {
        return Optional.of(playerTemplate);
    }

}
