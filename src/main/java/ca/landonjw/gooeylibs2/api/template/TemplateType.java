package ca.landonjw.gooeylibs2.api.template;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nonnull;
import java.util.function.Function;

public enum TemplateType {
    CHEST(template -> {
        switch (template.getSize() / 9) {
            case 1:
                return ContainerType.GENERIC_9x1;
            case 2:
                return ContainerType.GENERIC_9x2;
            case 3:
                return ContainerType.GENERIC_9x3;
            case 4:
                return ContainerType.GENERIC_9x4;
            case 5:
                return ContainerType.GENERIC_9x5;
            default:
                return ContainerType.GENERIC_9x6;
        }
    }),
    FURNACE(template -> ContainerType.FURNACE),
    BREWING_STAND(template -> ContainerType.BREWING_STAND),
    HOPPER(template -> ContainerType.HOPPER),
    DISPENSER(template -> ContainerType.GENERIC_3x3),
    CRAFTING_TABLE(template -> ContainerType.CRAFTING);

    private final Function<Template, ContainerType<? extends Container>> containerTypeSupplier;

    TemplateType(@Nonnull Function<Template, ContainerType<? extends Container>> containerTypeSupplier) {
        this.containerTypeSupplier = containerTypeSupplier;
    }

    public ContainerType<? extends Container> getContainerType(@Nonnull Template template) {
        return containerTypeSupplier.apply(template);
    }

}