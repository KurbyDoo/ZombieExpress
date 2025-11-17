package domain.entities;

public class RangedWeapon extends Weapon {

    private final Item ammoType;

    public RangedWeapon(String name, int damage, Item ammoType) {
        super(name, damage);
        this.ammoType = ammoType;
    }

    public Item getAmmoType() {
        return ammoType;
    }

    @Override
    public String toString() {
        return getName() + " [Ranged] (Damage: " + damage + ", Ammo: " + ammoType.getName() + ")";
    }
}
