/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Entity Pattern: Base class for all game entities with identity (ID).
 * - Template Method potential: Subclasses can override behavior.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies (uses GamePosition which has violations).
 * - [PASS] Pure domain entity definition.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages entity identity and basic positioning.
 * - [PASS] OCP: Open for extension via subclasses (Zombie, Train, Bullet, etc.).
 * - [PASS] LSP: Subclasses can substitute for Entity.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] setID() method is empty (line 25) - should be implemented or removed.
 * - [WARN] Protected fields expose internal state to subclasses - consider getters.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Empty method body on line 25: public void setID(Integer id) {}
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [PASS] Field visibility is protected for inheritance.
 */
package domain.entities;

import domain.GamePosition;

public class Entity {
    protected Integer id;
    protected GamePosition position;
    protected GamePosition velocity;
    protected boolean isVisible;
    protected float yaw;
    private final EntityType type;

    public Entity(Integer id, EntityType type, GamePosition position, boolean isVisible) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.isVisible = isVisible;
        this.velocity = new GamePosition(0, 0, 0);
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {}

    public EntityType getType() {
        return type;
    }

    public GamePosition getPosition() {
        return position;
    }

    public void setPosition(GamePosition position) {
        this.position = position;
    }

    public GamePosition getVelocity() {
        return velocity;
    }

    public void setVelocity(GamePosition velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(float x, float y, float z) {
        this.velocity.set(x, y, z);
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean boolValue) {
        this.isVisible = boolValue;
    }
}
