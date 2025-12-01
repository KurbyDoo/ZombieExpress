package application.interface_use_cases.register;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockRegisterDAO implements RegisterUserDataAccessInterface {
    String receivedEmail;
    String receivedPassword;
    String returnedUid;

    @Override
    public String newUser(String email, String password) {
        this.receivedEmail = email;
        this.receivedPassword = password;
        return returnedUid;
    }
}

class MockRegisterPresenter implements RegisterOutputBoundary {

    boolean successCalled = false;
    boolean failedCalled = false;

    RegisterOutputData successData = null;
    String failedMessage = null;

    @Override
    public void registerSuccess(RegisterOutputData data) {
        successCalled = true;
        successData = data;
    }

    @Override
    public void registerFail(String errorMessage) {
        failedCalled = true;
        failedMessage = errorMessage;
    }
}

public class RegisterInteractorTest {

    private MockRegisterDAO mockDAO;
    private MockRegisterPresenter mockPresenter;
    private RegisterInteractor interactor;

    @BeforeEach
    void setup() {
        mockDAO = new MockRegisterDAO();
        mockPresenter = new MockRegisterPresenter();
        interactor = new RegisterInteractor(mockDAO, mockPresenter);
    }

    @Test
    void passwordMismatchShouldFail() {
        interactor.register("a@mail.com", "123456", "654321");

        assertTrue(mockPresenter.failedCalled);
        assertEquals("Passwords do not match", mockPresenter.failedMessage);
    }

    @Test
    void invalidEmailShouldFail_null() {
        interactor.register(null, "123456", "123456");

        assertTrue(mockPresenter.failedCalled);
        assertTrue(mockPresenter.failedMessage.contains("Invalid email"));
    }

    @Test
    void invalidEmailShouldFail_missingSymbols() {
        interactor.register("wrongemail", "123456", "123456");

        assertTrue(mockPresenter.failedCalled);
        assertTrue(mockPresenter.failedMessage.contains("Invalid email"));
    }

    @Test
    void shortPasswordShouldFail() {
        interactor.register("mail@mail.com", "123", "123");

        assertTrue(mockPresenter.failedCalled);
        assertEquals("Password must be at least 6 characters.", mockPresenter.failedMessage);
    }

    @Test
    void daoReturnsNullShouldFail() {
        mockDAO.returnedUid = null;

        interactor.register("mail@mail.com", "123456", "123456");

        assertTrue(mockPresenter.failedCalled);
        assertEquals("Email is invalid", mockPresenter.failedMessage);
    }

    @Test
    void successfulRegisterShouldCallPresenterSuccess() {
        mockDAO.returnedUid = "abc123uid";

        interactor.register("user@mail.com", "123456", "123456");

        assertTrue(mockPresenter.successCalled);
        assertFalse(mockPresenter.failedCalled);

        assertEquals("user@mail.com", mockPresenter.successData.getEmail());
        assertEquals("abc123uid", mockPresenter.successData.getUid());
    }
}
