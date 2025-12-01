/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Entity Pattern: Concrete entity representing a pickupable item in the world.
 * - Composition: Contains an Item domain object.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [CRITICAL VIOLATION] Imports com.badlogic.gdx.math.Vector3 in domain layer (line 3).
 *   This import appears to be UNUSED but still violates Clean Architecture principles.
 *   Domain entities MUST NOT import framework-specific types.
 *
 * RECOMMENDED FIX:
 *   Remove the unused import: import com.badlogic.gdx.math.Vector3;
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages pickup entity data (item reference).
 * - [PASS] OCP: Extends Entity without modifying it.
 * - [PASS] LSP: Can substitute for Entity in any context.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Unused import on line 3 (Vector3 is never used).
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Unused import: com.badlogic.gdx.math.Vector3 should be removed.
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [PASS] Proper use of final for immutable fields.
 */
package domain.entities;

import com.badlogic.gdx.math.Vector3;
import domain.GamePosition;
import domain.items.Item;

public class PickupEntity extends Entity {

    private final Item item;

    public PickupEntity(Integer id, Item item, GamePosition position, boolean visible) {
        super(id, EntityType.PICKUP, new GamePosition(position), visible);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
