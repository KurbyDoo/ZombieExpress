package domain.entities;

import domain.GamePosition;

public interface Rideable {
    GamePosition getPlayerPosition();
    int getSpeed();
}
