package ca.landonjw.gooeylibs2.api.adventure;

import ca.landonjw.gooeylibs2.api.adventure.serializer.ForgeComponentSerializer;
import net.kyori.adventure.text.Component;
import net.minecraft.util.text.ITextComponent;

public class ForgeTranslator {

    private static final ForgeComponentSerializer serializer = new ForgeComponentSerializer();

    public static ITextComponent asMinecraft(Component parent) {
        return serializer.serialize(parent);
    }

    public static Component asAdventure(ITextComponent component) {
        return serializer.deserialize(component);
    }

}
