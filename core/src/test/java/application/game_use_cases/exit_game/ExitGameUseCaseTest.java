package application.game_use_cases.exit_game;

import application.game_use_cases.ports.ApplicationLifecyclePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A manual test double (Spy) for the ApplicationLifecyclePort that tracks
 * how many times the closeApplication method has been called.
 */
class TestApplicationLifecyclePort implements ApplicationLifecyclePort {
    private int closeCallCount = 0;

    @Override
    public void closeApplication() {
        closeCallCount++;
    }

    public int getCloseCallCount() {
        return closeCallCount;
    }
}


/**
 * Unit tests for the ExitGameUseCase, ensuring the core responsibility of
 * delegating the exit command to the infrastructure port is fulfilled,
 * using a manual test double instead of Mockito.
 */
class ExitGameUseCaseTest {

    private TestApplicationLifecyclePort testLifecyclePort;
    private ExitGameUseCase exitGameUseCase;

    /*
     * This runs before every test, setting up the environment without Mockito.
     */
    @BeforeEach
    void setUp() {
        // 1. Create the manual test double
        testLifecyclePort = new TestApplicationLifecyclePort();

        // 2. Inject the test double into the Use Case
        exitGameUseCase = new ExitGameUseCase(testLifecyclePort);
    }

    @Nested
    @DisplayName("Exit Command Execution")
    class ExitCommandExecutionTests {

        /**
         * Verifies that when the use case is executed once, the
         * TestApplicationLifecyclePort.closeApplication() method is called exactly once.
         */
        @Test
        @DisplayName("Successfully calls closeApplication() once")
        void shouldCallCloseApplicationOnce() {
            // Act: Run the Use Case
            exitGameUseCase.execute();

            // Assert: Verify the expected behavior (call count == 1)
            assertEquals(1, testLifecyclePort.getCloseCallCount(),
                "closeApplication() should be called exactly once.");
        }

        /**
         * Verifies that if the execute method is called multiple times,
         * the underlying port method is called the corresponding number of times.
         */
        @Test
        @DisplayName("Calls closeApplication() three times when executed repeatedly")
        void shouldCallCloseApplicationMultipleTimes() {
            // Act: Run the Use Case three times
            exitGameUseCase.execute();
            exitGameUseCase.execute();
            exitGameUseCase.execute();

            // Assert: Verify the expected behavior (call count == 3)
            assertEquals(3, testLifecyclePort.getCloseCallCount(),
                "closeApplication() should be called three times.");
        }

        /**
         * Tests that no method on the port is called when the use case is only initialized.
         */
        @Test
        @DisplayName("Does not interact with port before execution")
        void shouldNotCallCloseApplicationBeforeExecute() {
            // Arrange (Use Case is initialized in @BeforeEach, but not executed here)

            // Assert: Verify that the method has not been called yet (call count == 0)
            assertEquals(0, testLifecyclePort.getCloseCallCount(),
                "closeApplication() should not be called before execute().");
        }
    }
 }

