package domain.entities;

import domain.GamePosition;

public interface Rideable {
    int getSpeed();
    GamePosition getRideOffset();
    GamePosition getPosition();
}
