package domain.items;

public class RangedWeapon extends Weapon {

    private final AmmoType ammoType;

    public RangedWeapon(String name, int damage, AmmoType ammoType) {
        super(name, damage);
        this.ammoType = ammoType;
    }

    public AmmoType getAmmoType() {
        return ammoType;
    }

    @Override
    public String toString() {
        return getName() + " [Ranged] (Damage: " + damage + ", Ammo: " + ammoType.getName() + ")";
    }
}
