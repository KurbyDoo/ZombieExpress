/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern: Concrete strategy for train behavior.
 * - Implements EntityBehaviour interface.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [WARN] SRP: Handles train movement AND player position update.
 *   Consider separating player synchronization to a different component.
 * - [PASS] OCP: Can add new behaviors without modifying existing code.
 * - [PASS] LSP: Correctly implements EntityBehaviour.
 * - [PASS] ISP: Implements focused EntityBehaviour interface.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Magic numbers (0.5f) should be named constants.
 * - [WARN] instanceof check (line 19) could be avoided with type-safe design.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Magic number on line 41 (0.5f).
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Entity;
import domain.entities.Train;
import domain.player.Player;

public class TrainBehaviour implements EntityBehaviour {
    private final Player player;

    public TrainBehaviour(Player player) {
        this.player = player;
    }

    @Override
    public void execute(EntityBehaviourInputData inputData) {
        Entity entity = inputData.getEntity();
        float deltaTime = inputData.getDeltaTime();
        if (!(entity instanceof Train)) return;
        Train train = (Train) entity;
        float throttle = train.getThrottle();

        // Only move if there is input and fuel
        if (throttle != 0) {
            float speed = train.getSpeed();

            GamePosition moveDelta = new GamePosition(speed * throttle * deltaTime, 0, 0);

            GamePosition newTrainPos = train.getPosition();
            newTrainPos.add(moveDelta);
            train.setPosition(newTrainPos);

            // Move the Player to match the Train
            if (player.getCurrentRide() == train) {
                GamePosition seatOffset = train.getRideOffset();
                GamePosition newPlayerPos = newTrainPos.cpy().add(seatOffset);
                player.setPosition(newPlayerPos);
            }
        }

        // Slow down train
        train.setThrottle(train.getThrottle() * (1f - 0.5f * deltaTime));
    }
}
