package domain.items;

import domain.AmmoType;

public class ItemTypes {

    private ItemTypes() {}

    // Fuel Items
//    public static final FuelItem WOOD_LOG   = new FuelItem("Wood Log", true, 1000);
//    public static final FuelItem COAL       = new FuelItem("Coal", true, 1500);
//    public static final FuelItem OIL_BARREL = new FuelItem("Oil Barrel", true, 2500);
    public static final FuelItem WOOD_LOG   = new FuelItem("Wood Log", true, 10);
    public static final FuelItem COAL       = new FuelItem("Coal", true, 20);
    public static final FuelItem OIL_BARREL = new FuelItem("Oil Barrel", true, 30);

    // Ranged Weapons
    public static final RangedWeapon RUSTY_PISTOL   = new RangedWeapon("Rusty Pistol", 15, AmmoType.PISTOL);
    public static final RangedWeapon COMBAT_PISTOL  = new RangedWeapon("Combat Pistol", 25, AmmoType.PISTOL);
    public static final RangedWeapon GOLDEN_PISTOL  = new RangedWeapon("Golden Pistol", 35, AmmoType.PISTOL);
    public static final RangedWeapon RAINBOW_PISTOL  = new RangedWeapon("Rainbow Pistol", 55, AmmoType.PISTOL);
    public static final RangedWeapon TACTICAL_RIFLE = new RangedWeapon("Tactical Rifle", 30, AmmoType.RIFLE);
    public static final RangedWeapon GOLDEN_RIFLE   = new RangedWeapon("Golden Rifle", 50, AmmoType.RIFLE);
    public static final RangedWeapon ZOMBIE_OBLITERATOR_RIFLE  = new RangedWeapon("Zombie Obliterator Rifle", 100, AmmoType.RIFLE);
}
