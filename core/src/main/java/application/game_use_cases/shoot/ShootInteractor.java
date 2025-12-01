/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Interactor Pattern: Implements shooting use case business logic.
 * - Factory Usage: Uses EntityFactory to create bullet entities.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Implements InputBoundary interface correctly.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - handles shooting logic.
 * - [PASS] OCP: Uses factory for entity creation.
 * - [PASS] LSP: Implements ShootInputBoundary correctly.
 * - [N/A] ISP: Single interface implemented.
 * - [PASS] DIP: Depends on EntityFactory abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Missing space before '{' on line 7: 'implements ShootInputBoundary{'.
 * - [WARN] Magic numbers (1.5f, -0.5f) should be named constants.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Missing space before opening brace.
 * - [WARN] Magic numbers on line 17.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.shoot;

import application.game_use_cases.generate_entity.bullet.GenerateBulletInputData;
import domain.GamePosition;
import domain.entities.EntityFactory;

public class ShootInteractor implements ShootInputBoundary{
    private final EntityFactory entityFactory;

    public ShootInteractor(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public ShootOutputData execute(ShootInputData inputData) {
        GamePosition bulletPos = new GamePosition(inputData.getPlayerPos());
        bulletPos.sub(0, 1.5f, -0.5f); // Bullet spawns at lower right corner of camera
        GamePosition bulletDir = new GamePosition(inputData.getPlayerDir());

        GenerateBulletInputData bulletInput = new GenerateBulletInputData(bulletPos, bulletDir);
        int id = entityFactory.create(bulletInput);

        ShootOutputData outputData = new ShootOutputData(id, bulletPos, bulletDir);

        return outputData;
    }
}
