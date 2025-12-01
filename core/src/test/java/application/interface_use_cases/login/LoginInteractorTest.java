package application.interface_use_cases.login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// This is the Mock Data Access
class MockLoginDAO implements LoginDataAccessInterface {

    String returnedJson = null;
    String lastEmail;
    String lastPassword;

    @Override
    public String login(String email, String password) {
        this.lastEmail = email;
        this.lastPassword = password;
        return returnedJson;
    }
}

// This is the Mock Presenter
class MockLoginPresenter implements LoginOutputBoundary {

    boolean successCalled = false;
    boolean failedCalled = false;

    LoginOutputData successData = null;
    String failedMessage = null;

    @Override
    public void loginSuccess(LoginOutputData data) {
        successCalled = true;
        successData = data;
    }

    @Override
    public void loginFailed(String error_message) {
        failedCalled = true;
        failedMessage = error_message;
    }
}

// below is TestClass
public class LoginInteractorTest {

    private MockLoginDAO mockDAO;
    private MockLoginPresenter mockPresenter;
    private LoginInteractor interactor;

    @BeforeEach
    void setup() {
        mockDAO = new MockLoginDAO();
        mockPresenter = new MockLoginPresenter();
        interactor = new LoginInteractor(mockDAO, mockPresenter);
    }

    // Email or password is empty
    @Test
    void emptyInputShouldFail() {
        interactor.login("", "123");

        assertTrue(mockPresenter.failedCalled);
        assertEquals("Email or password is empty", mockPresenter.failedMessage);
        assertFalse(mockPresenter.successCalled);
    }

    // Invalid email or password
    @Test
    void invalidCredentialsShouldFail() {
        mockDAO.returnedJson = null;  // Simulate Firebase rejects login

        interactor.login("meiyi@mail.com", "wrongpw");

        assertTrue(mockPresenter.failedCalled);
        assertEquals("Invalid email or password", mockPresenter.failedMessage);
        assertFalse(mockPresenter.successCalled);
    }

    // success login
    @Test
    void successLogin() {
        mockDAO.returnedJson = "{ \"localId\": \"abc123uid\" }";

        interactor.login("meiyi@mail.com", "correct");

        assertTrue(mockPresenter.successCalled);
        assertFalse(mockPresenter.failedCalled);
        assertNotNull(mockPresenter.successData);

        assertEquals("meiyi@mail.com", mockPresenter.successData.getEmail());
        assertEquals("abc123uid", mockPresenter.successData.getUid());
    }

    // the data which pass to the DAO is correct
    @Test
    void daoReceivesCorrectArguments() {
        interactor.login("a@b.com", "pass123");

        assertEquals("a@b.com", mockDAO.lastEmail);
        assertEquals("pass123", mockDAO.lastPassword);
    }
}
