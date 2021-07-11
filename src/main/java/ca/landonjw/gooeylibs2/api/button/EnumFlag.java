package ca.landonjw.gooeylibs2.api.button;

public enum EnumFlag {
    /**
        ENCHANTS only hides tool enchants, as book ones are classified as stored enchantments.
        EXTRAS hides multiple things, according to the minecraft wiki (https://minecraft.fandom.com/wiki/Tutorials/Command_NBT_tags)
     */
    PIXELMON(0),
    ENCHANTS(1),
    ATTRIBUTE_MODIFIERS(2),
    UNBREAKABLE(4),
    CAN_DESTROY(8),
    CAN_PLACE_ON(16),
    EXTRAS(32),
    LEATHER_DYED(64),
    ALL(127);

    private final int value;

    EnumFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
