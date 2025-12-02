package domain.entities;

import domain.world.GamePosition;

public interface Rideable {
    int getSpeed();

    GamePosition getRideOffset();

    GamePosition getPosition();
}
