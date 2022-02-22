package ca.landonjw.gooeylibs2.api.adventure.serializer;

import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;

import javax.annotation.Nullable;

/**
 * Consumes a style created with Adventure (net.kyori.adventure), and directly translates it into a
 * minecraft native format.
 *
 * @since 2.3.0
 */
public class StyleTranslator {

    /**
     * Translates each component that makes up a style into the respective minecraft native style typing.
     * As of 2.3.0, this only considers basic text styling, such as decorations, colors, and font. Click,
     * hover, and insertions are not yet supported as chat is not a main focus of this library.
     *
     * @param parent The parent style created via Adventure
     * @return The native minecraft style mirroring the parent style
     * @since 2.3.0
     */
    public static Style translate(net.kyori.adventure.text.format.Style parent) {
        Style translated = Style.EMPTY;
        translated = translated.setColor(ColorTranslator.translate(parent.color()));
        translated = decorate(translated, parent);

        return translated;
    }

    /**
     * Translates all possible decorations for a text style into their minecraft native format. Really this
     * is just transferring boolean states.
     *
     * @param target The style that is being created to mirror the parent
     * @param parent The parent style from Adventure
     * @return The target style updated with any set decorations
     * @since 2.3.0
     */
    private static Style decorate(Style target, net.kyori.adventure.text.format.Style parent) {
        for(TextDecoration decoration : TextDecoration.values()) {
            if(parent.hasDecoration(decoration)) {
                switch (decoration) {
                    case BOLD:
                        target = target.setBold(true);
                        break;
                    case ITALIC:
                        target = target.setItalic(true);
                        break;
                    case OBFUSCATED:
                        target = target.setObfuscated(true);
                        break;
                    case UNDERLINED:
                        target = target.setUnderlined(true);
                        break;
                    case STRIKETHROUGH:
                        target = target.setStrikethrough(true);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown decoration: " + decoration.name());
                }
            }
        }

        return target;
    }

    /**
     * Translates colors into their native minecraft format.
     *
     * @since 2.3.0
     */
    public static class ColorTranslator {

        /**
         * Translates a color based on its hex string into the minecraft native coloring.
         *
         * @param parent The Adventure compatible color
         * @return The native minecraft color mirroring Adventure
         * @since 2.3.0
         */
        public static Color translate(@Nullable net.kyori.adventure.text.format.TextColor parent) {
            return parent == null ? null : Color.fromHex(parent.asHexString());
        }

    }

}
