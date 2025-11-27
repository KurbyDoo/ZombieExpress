package application.use_cases.player_movement;

import domain.entities.Rideable;
import domain.entities.Train;
import domain.player.Player;
import com.badlogic.gdx.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class PlayerMovementInteractor implements PlayerMovementInputBoundary {
    private final Player player;

    private final int SPRINT_SPEED = 5;

    // The interactor only needs a reference to the entity it manipulates.
    public PlayerMovementInteractor(Player player) {
        this.player = player;
    }

    @Override
    public void execute(PlayerMovementInputData inputData) {
        // --- Handle Rotation ---
        if (inputData.getDeltaX() != 0 || inputData.getDeltaY() != 0) {
            player.updateRotation(inputData.getDeltaX(), inputData.getDeltaY());
        }

        // --- Handle Movement ---
        Vector3 playerDirection = new Vector3(player.getDirection()).nor();
        Vector3 playerUp = new Vector3(player.getUp()).nor();
        Rideable playerRide = player.getCurrentRide();

        Vector3 velocity;
        if (playerRide instanceof Train) {
            velocity = getOnTrainVelocity(inputData, playerDirection, (Train) playerRide);
        } else {
            velocity = getOnGroundVelocity(inputData, playerDirection, playerUp);
            if (inputData.isSprinting()) velocity.scl(SPRINT_SPEED);
        }

        // Only update the player's position if there is movement input.
        if (!velocity.isZero()) {
            player.updatePosition(velocity.scl(inputData.getDeltaTime()));
        }
    }

    private Vector3 getOnGroundVelocity(PlayerMovementInputData inputData, Vector3 playerDirection, Vector3 playerUp) {
        Vector3 velocity = new Vector3();
        Vector3 totalUp = new Vector3(0f, playerDirection.y, 0f);
        Vector3 orthogonalDirection = playerDirection.sub(totalUp).nor();

        if (inputData.isForward()) {
            velocity.add(orthogonalDirection);
        }
        if (inputData.isBackward()) {
            velocity.sub(orthogonalDirection);
        }
        if (inputData.isLeft()) {
            Vector3 left = new Vector3(orthogonalDirection).crs(playerUp).nor().scl(-1);
            velocity.add(left);
        }
        if (inputData.isRight()) {
            Vector3 right = new Vector3(orthogonalDirection).crs(playerUp).nor();
            velocity.add(right);
        }
        return velocity;
    }


    // TODO: We will update train position through behaviour
    // This should be changed to set the velo to diff between train and player
    // and set the velocity of the train
    private Vector3 getOnTrainVelocity(PlayerMovementInputData inputData, Vector3 playerDirection, Train train) {
        if (inputData.isForward() && train.getTotalFuel() > 0) {
            return new Vector3(Math.abs(playerDirection.x), 0f, 0f).nor().scl(train.getSpeed());
        }

        return new Vector3();
    }
}
