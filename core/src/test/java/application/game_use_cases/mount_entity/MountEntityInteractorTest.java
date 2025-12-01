/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Test
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package application.game_use_cases.mount_entity;

import application.game_use_cases.dismount_entity.DismountEntityInputData;
import application.game_use_cases.dismount_entity.DismountEntityInteractor;
import application.game_use_cases.dismount_entity.DismountEntityOutputData;
import domain.GamePosition;
import domain.entities.Rideable;
import domain.entities.Train;
import domain.player.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MountEntityInteractorTest {
    private Player player;
    private Rideable ride;
    private MountEntityInteractor interactor;

    @BeforeEach
    void setUp() {
        player = new Player(new GamePosition(0, 0, 0));
        ride = new Train(0, new GamePosition(0, 0, 0));
        interactor = new MountEntityInteractor(player);
    }

    @Test
    @DisplayName("Mount entity")
    void mountEntity() {
        MountEntityOutputData outputData = interactor.execute(new MountEntityInputData(ride));
        GamePosition offsetPos = ride.getPosition().add(ride.getRideOffset());

        assertNotNull(player.getCurrentRide(), "Ride should be not be null after mount");
        assertTrue(
            player.getPosition().epsilonEquals(offsetPos, 0.001f),
            "Player should be at mount position after dismount, instead at " + player.getPosition()
        );
        assertTrue(outputData.isMountSuccess(), "Success should be true.");
    }

    @Test
    @DisplayName("Mount null entity")
    void mountNull() {
        GamePosition initialPosition = player.getPosition();
        MountEntityOutputData outputData = interactor.execute(new MountEntityInputData(null));

        assertNull(player.getCurrentRide(), "Ride should be null after mount");
        assertFalse(outputData.isMountSuccess(), "Success should be false.");
        assertTrue(
            initialPosition.epsilonEquals(player.getPosition(), 0.001f),
            "Position should not change after failed mount"
        );
    }

    @Test
    @DisplayName("Attempt already mounted")
    void mountEntityAlreadyMounted() {
        player.setCurrentRide(ride);
        GamePosition initialPosition = player.getPosition();
        MountEntityOutputData outputData = interactor.execute(new MountEntityInputData(ride));

        assertNotNull(player.getCurrentRide(), "Ride should not be modified");
        assertFalse(outputData.isMountSuccess(), "Success should be false.");
        assertTrue(
            initialPosition.epsilonEquals(player.getPosition(), 0.001f),
            "Position should not change after failed mount"
        );
    }
}
