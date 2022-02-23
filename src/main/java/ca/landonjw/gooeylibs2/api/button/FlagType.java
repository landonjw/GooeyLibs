package ca.landonjw.gooeylibs2.api.button;

public enum FlagType
{
    /**
     * ENCHANTS only hides tool enchants, as book ones are classified as stored enchantments.
     * EXTRAS hides multiple things, according to the minecraft wiki (https://minecraft.fandom.com/wiki/Tutorials/Command_NBT_tags)
     */
    Reforged(0),
    Generations(0),
    Enchantments(1),
    Attribute_Modifiers(2),
    Unbreakable(4),
    Can_Destroy(8),
    Can_Place_On(16),
    Extras(32),
    Dyed_Leather(64),
    All(127);

    private final int value;

    FlagType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}
