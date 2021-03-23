package ca.landonjw.gooeylibs.api.button.moveable;

import ca.landonjw.gooeylibs.api.button.Button;

public interface Movable extends Button {

    void onPickup(MovableButtonAction action);

    void onDrop(MovableButtonAction action);

}