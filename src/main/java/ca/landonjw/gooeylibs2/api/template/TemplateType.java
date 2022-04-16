package ca.landonjw.gooeylibs2.api.template;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nonnull;
import java.util.function.Function;

public enum TemplateType {
    CHEST(template -> switch (template.getSize() / 9) {
        case 1 -> MenuType.GENERIC_9x1;
        case 2 -> MenuType.GENERIC_9x2;
        case 3 -> MenuType.GENERIC_9x3;
        case 4 -> MenuType.GENERIC_9x4;
        case 5 -> MenuType.GENERIC_9x5;
        default -> MenuType.GENERIC_9x6;
    }),
    FURNACE(template -> MenuType.FURNACE),
    BREWING_STAND(template -> MenuType.BREWING_STAND),
    HOPPER(template -> MenuType.HOPPER),
    DISPENSER(template -> MenuType.GENERIC_3x3),
    CRAFTING_TABLE(template -> MenuType.CRAFTING);

    private final Function<Template, MenuType<? extends AbstractContainerMenu>> containerTypeSupplier;

    TemplateType(@Nonnull Function<Template, MenuType<? extends AbstractContainerMenu>> containerTypeSupplier) {
        this.containerTypeSupplier = containerTypeSupplier;
    }

    public MenuType<? extends AbstractContainerMenu> getContainerType(@Nonnull Template template) {
        return containerTypeSupplier.apply(template);
    }

}