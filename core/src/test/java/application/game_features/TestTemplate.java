package application.game_features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test template.
 */
class TestTemplate {
    // Declare private variables here to access throughout the tests
    // private GameObject object;
    // private SampleInteractor interactor;

    /*
        This runs before every test, set up your needed variables
     */
    @BeforeEach
    void setUp() {
        // object = new GameObject()
        // interactor = new SampleInteractor(object)
    }

    /*
        This is a test suite centered around a specific feature
     */
    @Nested
    @DisplayName("Test flow A")
    class FlowATests {

        @Test
        @DisplayName("Flow fails")
        /*
            In each test case, set up your mock classes and
            run the thing you want to test
         */
        void shouldFail() {
            // GamePosition pos = object.getPosition();

            // SampleOutputData output = interactor.execute(new SampleInputData(pos, object));

            // Use asserts to check conditions on the outputs
            // assertFalse(output.getSuccess(), "Should have failed");
        }

        @Test
        @DisplayName("Flow should fail if input is invalid")
        void shouldFailWithInvalidData() {
            // GamePosition pos = new GamePosition(0, 0, 0);

            // SampleOutputData output = interactor.execute(new SampleInputData(pos, object));

            // Notice this uses a different assert
            // assertEquals(output.getSuccess(), false, "Should have failed if position does not match object");
        }

        @Test
        @DisplayName("Flow succeeds")
        void shouldNotUpdateRotationWhenDeltasAreZero() {
            // object.setPosition(new GamePosition(0, 0, 0));
            // GamePosition pos = object.getPosition();

            // SampleOutputData output = interactor.execute(new SampleInputData(pos, object));

            // There are many different types of asserts
            // assertTrue(output.getSuccess(), "Should have succeeded if at the origin");
        }
    }

    @Nested
    @DisplayName("Alternate flow tests")
    class AlternateFlowTests {

        @BeforeEach
        void setUpGroundMovement() {
            // Here you would do additional setup
        }

        @Test
        @DisplayName("Make sure to give each test a good name")
        void nameShouldIndicateFunction() {
            assertFalse(false, "Should always be false");
        }
    }

    // You can add as many groups as needed
    // Look at other use case tests for mode examples
}

