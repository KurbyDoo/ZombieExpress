package domain;

public enum AmmoType {
    PISTOL("Pistol Ammo"),
    RIFLE("Rifle Ammo");

    private final String name;

    AmmoType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
