package ca.landonjw.gooeylibs.commands.shooter;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.GooeyButton;
import ca.landonjw.gooeylibs.api.data.UpdateEmitter;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.page.PageAction;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs.api.template.types.InventoryTemplate;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ShooterPage extends UpdateEmitter<Page> implements Page {

    private final ShooterController controller = new ShooterController();
    private final ChestTemplate template;
    private final InventoryTemplate inventoryTemplate;

    private final Button ship = GooeyButton.builder()
            .title("")
            .display(new ItemStack(Items.EXPERIENCE_BOTTLE))
            .build();

    private final Button projectile = GooeyButton.builder()
            .title("")
            .display(new ItemStack(Items.FIREWORKS))
            .build();

    public ShooterPage() {
        controller.subscribe(this, this::refresh);

        Button moveLeft = GooeyButton.builder()
                .title(TextFormatting.GOLD + "Move Left")
                .display(new ItemStack(PixelmonItems.LtradeHolderLeft))
                .onClick(() -> controller.movePlayer(ShooterController.Direction.LEFT))
                .build();

        Button shoot = GooeyButton.builder()
                .title(TextFormatting.GOLD + "Shoot")
                .display(new ItemStack(Items.FIREWORKS))
                .onClick(controller::shoot)
                .build();

        Button moveRight = GooeyButton.builder()
                .title(TextFormatting.GOLD + "Move Right")
                .display(new ItemStack(PixelmonItems.tradeHolderRight))
                .onClick(() -> controller.movePlayer(ShooterController.Direction.RIGHT))
                .build();

        this.inventoryTemplate = InventoryTemplate.builder()
                .set(3, 3, moveLeft)
                .set(3, 4, shoot)
                .set(3, 5, moveRight)
                .build();

        this.template = ChestTemplate.builder(6).build();
    }

    private void refresh() {
        // If the game is over, Fill the page with all red panes and change title.
        if (controller.isGameOver()) {
            Button redFiller = GooeyButton.builder()
                    .display(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.RED.getMetadata()))
                    .title("")
                    .build();
            for (int i = 0; i < template.getSize(); i++)
                template.getSlot(i).setButton(redFiller);
            for (int i = 0; i < inventoryTemplate.getSize(); i++)
                inventoryTemplate.getSlot(i).setButton(redFiller);
            update();
            return;
        }

        // Flush last tick's contents on the page
        for (int i = 0; i < template.getSize(); i++)
            template.getSlot(i).setButton(null);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                inventoryTemplate.getSlot(row, col).setButton(null);
            }
        }

        // Set all projectiles in the template
        for (ShooterProjectile projectile : controller.getProjectiles()) {
            if (projectile.getRow() >= 6) {
                inventoryTemplate.getSlot(projectile.getRow() - 6, projectile.getCol()).setButton(this.projectile);
            } else {
                template.getSlot(projectile.getRow(), projectile.getCol()).setButton(this.projectile);
            }
        }

        // Set all enemies in the template
        for (ShooterEnemy enemy : controller.getEnemies()) {
            Button enemyDisplay = GooeyButton.builder()
                    .display(new ItemStack(enemy.getDisplay()))
                    .title("")
                    .build();
            if (enemy.getRow() >= 6) {
                inventoryTemplate.getSlot(enemy.getRow() - 6, enemy.getCol()).setButton(enemyDisplay);
            } else {
                template.getSlot(enemy.getRow(), enemy.getCol()).setButton(enemyDisplay);
            }
        }

        // Set player in the template
        PlayerShip player = controller.getPlayer();
        inventoryTemplate.getSlot(1, player.getCol()).setButton(ship);

        // If the controller is dirty, update the container to change the title.
        if (controller.isHealthDirty()) {
            update();
            controller.setHealthClean();
        }
    }

    @Override
    public void onClose(@Nonnull PageAction action) {
        if (!controller.isGameOver()) {
            controller.setGameOver();
        }
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public Optional<InventoryTemplate> getInventoryTemplate() {
        return Optional.of(inventoryTemplate);
    }

    @Override
    public String getTitle() {
        if (controller.isGameOver()) {
            return TextFormatting.BOLD + "GAME OVER";
        }
        return TextFormatting.BOLD + "Health: " + TextFormatting.RED + "" + TextFormatting.BOLD + controller.getHealth();
    }

}
