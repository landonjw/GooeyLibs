package ca.landonjw.gooeylibs2.api.helpers;

import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlot;

public class TemplateHelper {

    public static TemplateSlot[] slotsOf(int size) {
        TemplateSlot[] elements = new TemplateSlot[size];
        for (int i = 0; i < size; i++) {
            elements[i] = new TemplateSlot(null, i, 0, 0);
        }
        return elements;
    }

}
