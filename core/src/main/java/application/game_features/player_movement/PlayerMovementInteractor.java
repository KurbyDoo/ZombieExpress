package application.game_features.player_movement;

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
