package domain.items;

import domain.AmmoType;

public class ItemTypes {

    private ItemTypes() {}

    // Fuel Items
    public static final FuelItem WOOD_LOG   = new FuelItem("Wood Log", true, 10);
    public static final FuelItem COAL       = new FuelItem("Coal", true, 25);
    public static final FuelItem OIL_BARREL = new FuelItem("Oil Barrel", false, 50);

    // Melee Weapons
    public static final MeleeWeapon BASEBALL_BAT = new MeleeWeapon("Baseball Bat", 10);
    public static final MeleeWeapon KATANA   = new MeleeWeapon("Katana", 30);
    public static final MeleeWeapon TOMAHAWK_AXE   = new MeleeWeapon("Tomahawk Axe", 40);

    // Ranged Weapons
    public static final RangedWeapon RUSTY_PISTOL   = new RangedWeapon("Rusty Pistol", 15, AmmoType.PISTOL);
    public static final RangedWeapon COMBAT_PISTOL  = new RangedWeapon("Combat Pistol", 25, AmmoType.PISTOL);
    public static final RangedWeapon TACTICAL_RIFLE = new RangedWeapon("Tactical Rifle", 35, AmmoType.RIFLE);
    public static final RangedWeapon GOLDEN_RIFLE   = new RangedWeapon("Golden Rifle", 45, AmmoType.RIFLE);
}
