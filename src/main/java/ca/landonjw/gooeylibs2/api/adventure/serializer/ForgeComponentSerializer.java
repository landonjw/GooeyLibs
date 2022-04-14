package ca.landonjw.gooeylibs2.api.adventure.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class ForgeComponentSerializer implements ComponentSerializer<Component, Component, net.minecraft.network.chat.Component > {

    private static final GsonComponentSerializer GSON_COMPONENT_SERIALIZER = GsonComponentSerializer.builder().build();

    @Override
    public @NotNull Component deserialize(@NotNull net.minecraft.network.chat.Component  input) {
        return GSON_COMPONENT_SERIALIZER.deserialize(input.toString());
    }

    @NotNull
    @Override
    public net.minecraft.network.chat.Component serialize(@NotNull Component component) {
        try {
            net.minecraft.network.chat.Component result = net.minecraft.network.chat.Component.Serializer.fromJson(GSON_COMPONENT_SERIALIZER.serialize(component));
            if(result == null) {
                throw new RuntimeException("Serialization failed to create a valid object");
            }

            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize component to native");
        }
    }
}
