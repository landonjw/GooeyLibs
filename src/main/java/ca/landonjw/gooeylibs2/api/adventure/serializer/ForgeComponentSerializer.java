package ca.landonjw.gooeylibs2.api.adventure.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ForgeComponentSerializer implements ComponentSerializer<Component, Component, ITextComponent> {

    private static final GsonComponentSerializer GSON_COMPONENT_SERIALIZER = GsonComponentSerializer.builder().build();

    @Override
    public @NotNull Component deserialize(@NotNull ITextComponent input) {
        return GSON_COMPONENT_SERIALIZER.deserialize(input.toString());
    }

    @NotNull
    @Override
    public ITextComponent serialize(@NotNull Component component) {
        try {
            ITextComponent result = ITextComponent.Serializer.getComponentFromJson(GSON_COMPONENT_SERIALIZER.serialize(component));
            if(result == null) {
                throw new RuntimeException("Serialization failed to create a valid object");
            }

            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize component to native");
        }
    }

//    private ITextComponent translate(Component parent) {
//        StringTextComponent result = this.construct(parent);
//        if(!parent.children().isEmpty()) {
//            this.translate$recursive(result, parent);
//        }
//
//        return result;
//    }
//
//    private void translate$recursive(ITextComponent result, Component parent) {
//        if(parent.children().isEmpty()) {
//            return;
//        }
//
//        List<TextComponent> children = parent.children().stream()
//                .filter(c -> c instanceof TextComponent)
//                .map(c -> (TextComponent) c)
//                .collect(Collectors.toList());
//        for(TextComponent child : children) {
//            result.appendSibling(this.translate(child));
//            this.translate$recursive(result, child);
//        }
//    }
//
//    private StringTextComponent construct(Component focus) {
//        StringTextComponent component = new StringTextComponent(focus.content());
//        component.setStyle(StyleTranslator.translate(focus.style()));
//
//        return component;
//    }
}
