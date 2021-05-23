package ca.landonjw.gooeylibs2.api.template;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nonnull;
import java.util.function.Function;

public enum TemplateType {
    CHEST(template -> {
        switch (template.getSize() / 9) {
            case 1:
                return ContainerType.GENERIC_9X1;
            case 2:
                return ContainerType.GENERIC_9X2;
            case 3:
                return ContainerType.GENERIC_9X3;
            case 4:
                return ContainerType.GENERIC_9X4;
            case 5:
                return ContainerType.GENERIC_9X5;
            default:
                return ContainerType.GENERIC_9X6;
        }
    }),
    FURNACE(template -> ContainerType.FURNACE),
    BREWING_STAND(template -> ContainerType.BREWING_STAND),
    HOPPER(template -> ContainerType.HOPPER),
    DISPENSER(template -> ContainerType.GENERIC_3X3),
    CRAFTING_TABLE(template -> ContainerType.CRAFTING);

    private final Function<Template, ContainerType<? extends Container>> containerTypeSupplier;

    TemplateType(@Nonnull Function<Template, ContainerType<? extends Container>> containerTypeSupplier) {
        this.containerTypeSupplier = containerTypeSupplier;
    }

    public ContainerType<? extends Container> getContainerType(@Nonnull Template template) {
        return containerTypeSupplier.apply(template);
    }

}