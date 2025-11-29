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
 * delegating the exit command to the infrastructure port is fulfilled.
 */
class ExitGameUseCaseTest {

    private TestApplicationLifecyclePort testLifecyclePort;
    private ExitGameUseCase exitGameUseCase;

    @BeforeEach
    void setUp() {
        testLifecyclePort = new TestApplicationLifecyclePort();

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
            exitGameUseCase.execute();

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

            assertEquals(3, testLifecyclePort.getCloseCallCount(),
                "closeApplication() should be called three times.");
        }

        /**
         * Tests that no method on the port is called when the use case is only initialized.
         */
        @Test
        @DisplayName("Does not interact with port before execution")
        void shouldNotCallCloseApplicationBeforeExecute() {

            assertEquals(0, testLifecyclePort.getCloseCallCount(),
                "closeApplication() should not be called before execute().");
        }
    }
 }

