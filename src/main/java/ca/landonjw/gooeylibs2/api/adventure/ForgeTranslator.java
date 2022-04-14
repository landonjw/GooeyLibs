package ca.landonjw.gooeylibs2.api.adventure;

import ca.landonjw.gooeylibs2.api.adventure.serializer.ForgeComponentSerializer;
import net.kyori.adventure.text.Component;

public class ForgeTranslator {

    private static final ForgeComponentSerializer serializer = new ForgeComponentSerializer();

    public static net.minecraft.network.chat.Component  asMinecraft(Component parent) {
        return serializer.serialize(parent);
    }

    public static Component asAdventure(net.minecraft.network.chat.Component  component) {
        return serializer.deserialize(component);
    }

}
