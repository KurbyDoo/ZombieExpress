package application.game_features.dismount_entity;

import domain.GamePosition;
import domain.entities.Rideable;
import domain.entities.Train;
import domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DismountEntityInteractorTest {
    private Player player;
    private Rideable ride;
    private DismountEntityInteractor interactor;

    @BeforeEach
    void setUp() {
        player = new Player(new GamePosition(0, 0, 0));
        ride = new Train(0, new GamePosition(0, 0, 0));
        player.setPosition(ride.getRideOffset());
        interactor = new DismountEntityInteractor(player);
    }

    @Test
    @DisplayName("Dismount entity")
    void dismountEntity() {
        player.setCurrentRide(ride);
        DismountEntityOutputData outputData = interactor.execute(new DismountEntityInputData());
        GamePosition position = new GamePosition(
            player.getPosition().x, 2f, player.getPosition().z
        );

        assertNull(player.getCurrentRide(), "Ride should be null after dismount");
        assertTrue(outputData.isDismountSuccess(), "Success should be true.");
        assertTrue(
            player.getPosition().epsilonEquals(position, 0.001f),
            "Player should be at ground level after dismount, instead at " + player.getPosition()
        );

    }

    @Test
    @DisplayName("Dismount null entity")
    void dismountNull() {
        DismountEntityOutputData outputData = interactor.execute(new DismountEntityInputData());

        assertNull(player.getCurrentRide(), "Ride should be null after dismount");
        assertFalse(outputData.isDismountSuccess(), "Success should be false.");
    }
}
