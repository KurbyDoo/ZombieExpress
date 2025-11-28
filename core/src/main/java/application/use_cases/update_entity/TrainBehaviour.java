package application.use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Entity;
import domain.entities.Train;
import domain.player.Player;

public class TrainBehaviour implements EntityBehaviour {

    @Override
    public void update(Entity entity, BehaviourContext context) {
        if (!(entity instanceof Train)) return;
        Train train = (Train) entity;

        float throttle = train.getThrottle();

        // Only move if there is input and fuel
        if (throttle != 0) {
            float speed = train.getSpeed();
            float deltaTime = context.getDeltaTime();

            GamePosition moveDelta = new GamePosition(speed * throttle * deltaTime, 0, 0);

            GamePosition newTrainPos = train.getPosition();
            newTrainPos.add(moveDelta);
            train.setPosition(newTrainPos);

            // Move the Player to match the Train
            Player player = context.getPlayer();
            if (player.getCurrentRide() == train) {
                GamePosition seatOffset = train.getRideOffset();
                GamePosition newPlayerPos = newTrainPos.cpy().add(seatOffset);
                player.setPosition(newPlayerPos);
            }
        }

        // Slow down train
        train.decelerate();
    }
}
