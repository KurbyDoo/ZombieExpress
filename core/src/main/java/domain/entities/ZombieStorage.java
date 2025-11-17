package domain.entities;

import java.util.ArrayList;
import java.util.List;

public class ZombieStorage {
    // Database of the zombies
    private List<Zombie> zombieList = new ArrayList<>();

    public void addZombie(Zombie z) { zombieList.add(z); }
    public List<Zombie> getZombies() { return zombieList; }
}
