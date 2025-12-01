/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Flyweight Pattern: Pre-defined item instances shared across the application.
 * - Registry Pattern: Central registry of all available items.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain constants class.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines item type constants.
 * - [PASS] OCP: Can add new items without modifying existing code.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: No dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Constants follow UPPER_SNAKE_CASE.
 * - [PASS] Private constructor prevents instantiation.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc describing available items.
 * - [PASS] Proper utility class structure with private constructor.
 */
package domain.items;

import domain.AmmoType;

public class ItemTypes {

    private ItemTypes() {}

    // Fuel Items
    public static final FuelItem WOOD_LOG   = new FuelItem("Wood Log", true, 1000);
    public static final FuelItem COAL       = new FuelItem("Coal", true, 1500);
    public static final FuelItem OIL_BARREL = new FuelItem("Oil Barrel", true, 2500);

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
