package domain.entities;

import java.util.ArrayList;
import java.util.List;

public class PickupStorage {

    private final List<WorldPickup> pickups = new ArrayList<>();

    public void addPickup(WorldPickup pickup) {
        pickups.add(pickup);
    }

    public void removePickup(WorldPickup pickup) {
        pickups.remove(pickup);
    }

    public List<WorldPickup> getAll() {
        return new ArrayList<>(pickups);
    }
}
