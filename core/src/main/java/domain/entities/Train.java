/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Entity Pattern: Concrete entity representing a train vehicle.
 * - Interface Implementation: Implements Rideable interface.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [WARN] SRP: Train manages many concerns (fuel, throttle, speed, position).
 *   Consider separating FuelSystem and ThrottleSystem as separate domain components.
 * - [PASS] OCP: Extends Entity without modifying it.
 * - [PASS] LSP: Can substitute for Entity and Rideable.
 * - [PASS] ISP: Implements only the Rideable interface which is focused.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Constants MAX_FUEL and MAX_THROTTLE should be public static final
 *   if they represent domain constants, or truly private if internal.
 * - [PASS] Methods follow camelCase naming.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [PASS] Consistent code formatting.
 * - [PASS] Proper implementation of Rideable interface methods.
 */
package domain.entities;

import domain.GamePosition;

public class Train extends Entity implements Rideable {
    private final float MAX_FUEL = 100;
    private final float MAX_THROTTLE = 2f;
    private float currentThrottle = 0f;

    private float fuel;
    private int speed;
    public Train(Integer id, GamePosition position) {
        super(id, EntityType.TRAIN, position, true);
        fuel = 20;
        speed = 30;
    }

    public void setCurrentFuel(float fuel) {
        this.fuel = Math.min(fuel, MAX_FUEL);
    }

    public float getCurrentFuel() {
        return fuel;
    }

    public float getMaxFuel() {
        return MAX_FUEL;
    }

    public void addFuel(float amount) {
        this.fuel = Math.min(this.fuel + amount, MAX_FUEL);
    }

    public void consumeFuel(float amount) {
        this.fuel = Math.max(this.fuel - amount, 0);
    }

    public GamePosition getRideOffset() {
        return new GamePosition(2f, 6f, 0f);
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setThrottle(float throttle) {
        this.currentThrottle = throttle;
    }

    public float getThrottle() {
        return currentThrottle;
    }

    public void resetThrottle() {
        this.currentThrottle = 0f;
    }

    public float getRemainingThrottle() {
        return MAX_THROTTLE - currentThrottle;
    }
}
