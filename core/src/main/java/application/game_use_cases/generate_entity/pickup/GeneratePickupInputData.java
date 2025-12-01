/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Extends GenerateEntityInputData for pickup creation.
 * - Specialization: Adds item reference for pickup entities.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds pickup creation input.
 * - [PASS] OCP: Extends base class without modification.
 * - [PASS] LSP: Can substitute for GenerateEntityInputData.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: Pure data class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Fields are private and final.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.generate_entity.pickup;

import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.GamePosition;
import domain.entities.EntityType;
import domain.items.Item;

public class GeneratePickupInputData extends GenerateEntityInputData {

    private final Item item;

    public GeneratePickupInputData(Item item, GamePosition position) {
        super(EntityType.PICKUP, position);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
