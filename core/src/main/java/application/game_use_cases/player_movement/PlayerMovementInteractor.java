/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Interactor Pattern: Implements use case business logic.
 * - Implements Input Boundary for Clean Architecture.
 * - Strategy-like behavior: Different movement for walking vs. riding.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies (uses domain types).
 * - [PASS] Implements InputBoundary interface correctly.
 *
 * SOLID PRINCIPLES:
 * - [WARN] SRP: Handles both walking AND train riding movement.
 *   Consider Strategy Pattern: MovementStrategy interface with
 *   WalkingMovement and RidingMovement implementations.
 * - [WARN] OCP: Adding new movement types requires modifying this class.
 *   The instanceof check (line 25) violates OCP.
 * - [PASS] LSP: Implements PlayerMovementInputBoundary correctly.
 * - [N/A] ISP: No interfaces implemented beyond InputBoundary.
 * - [PASS] DIP: Depends on domain abstractions.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] SPRINT_MULTIPLIER constant should be static final (line 11).
 * - [WARN] Magic numbers (0.25f, 1f) should be named constants.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Instance constant should be static: 'private static final int SPRINT_MULTIPLIER'.
 * - [WARN] Magic numbers in lines 31, 34.
 * - [MINOR] Missing class-level and method-level Javadoc.
 */
package application.game_use_cases.player_movement;

import domain.GamePosition;
import domain.entities.Rideable;
import domain.entities.Train;
import domain.player.Player;

public class PlayerMovementInteractor implements PlayerMovementInputBoundary {
    private final Player player;

    private final int SPRINT_MULTIPLIER = 5;

    public PlayerMovementInteractor(Player player) {
        this.player = player;
    }

    @Override
    public void execute(PlayerMovementInputData inputData) {
        // --- Handle Rotation ---
        if (inputData.getDeltaX() != 0 || inputData.getDeltaY() != 0) {
            player.updateRotation(inputData.getDeltaX(), inputData.getDeltaY());
        }

        Rideable playerRide = player.getCurrentRide();

        if (playerRide instanceof Train) {
            Train train = (Train) playerRide;

            if (inputData.isForward() && train.getCurrentFuel() > 0) {
                // Take 4 seconds to get to max speed?
                train.setThrottle(
                    train.getThrottle() + train.getRemainingThrottle() * 0.25f * inputData.getDeltaTime()
                );
                // Consume 1 fuel per second
                train.consumeFuel(1f * inputData.getDeltaTime());
            }
        } else {
            GamePosition playerDirection = new GamePosition(player.getDirection()).nor();
            GamePosition playerUp = new GamePosition(player.getUp()).nor();

            GamePosition velocity = getOnGroundVelocity(inputData, playerDirection, playerUp);

            velocity.scl(player.getMovementSpeed());

            if (inputData.isSprinting()) {
                velocity.scl(SPRINT_MULTIPLIER);
            }

            // Apply the move immediately
            if (!velocity.isZero()) {
                player.updatePosition(velocity.scl(inputData.getDeltaTime()));
            }
        }
    }

    private GamePosition getOnGroundVelocity(PlayerMovementInputData inputData, GamePosition playerDirection, GamePosition playerUp) {
        GamePosition velocity = new GamePosition();
        GamePosition totalUp = new GamePosition(0f, playerDirection.y, 0f);
        GamePosition orthogonalDirection = playerDirection.sub(totalUp).nor();

        if (inputData.isForward()) {
            velocity.add(orthogonalDirection);
        }
        if (inputData.isBackward()) {
            velocity.sub(orthogonalDirection);
        }
        if (inputData.isLeft()) {
            GamePosition left = new GamePosition(orthogonalDirection).crs(playerUp).nor().scl(-1);
            velocity.add(left);
        }
        if (inputData.isRight()) {
            GamePosition right = new GamePosition(orthogonalDirection).crs(playerUp).nor();
            velocity.add(right);
        }
        return velocity;
    }
}
