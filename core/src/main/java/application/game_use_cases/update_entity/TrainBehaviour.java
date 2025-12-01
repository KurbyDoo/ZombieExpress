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
