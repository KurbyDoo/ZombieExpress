package application.interface_use_cases.login;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.account_features.login.LoginDataAccessInterface;
import application.account_features.login.LoginInteractor;
import application.account_features.login.LoginOutputBoundary;
import application.account_features.login.LoginOutputData;

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

    @Test
    void nullInputShouldFail() {
        interactor.login(null, "123");
        assertTrue(mockPresenter.failedCalled);
        assertEquals("Email or password is empty", mockPresenter.failedMessage);
    }

    @Test
    void nullPasswordShouldFail() {
        interactor.login("1234@mail.com", null);
        assertTrue(mockPresenter.failedCalled);
        assertEquals("Email or password is empty", mockPresenter.failedMessage);
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

        interactor.login("mail@mail.com", "wrongpw");

        assertTrue(mockPresenter.failedCalled);
        assertEquals("Invalid email or password", mockPresenter.failedMessage);
        assertFalse(mockPresenter.successCalled);
    }

    // success login
    @Test
    void successLogin() {
        mockDAO.returnedJson = "{ \"localId\": \"abc123uid\" }";

        interactor.login("mail@mail.com", "correct");

        assertTrue(mockPresenter.successCalled);
        assertFalse(mockPresenter.failedCalled);
        assertNotNull(mockPresenter.successData);

        assertEquals("mail@mail.com", mockPresenter.successData.getEmail());
        assertEquals("abc123uid", mockPresenter.successData.getUid());
    }

    @Test
    void malformedJsonShouldThrow() {
        mockDAO.returnedJson = "not_json";
        assertThrows(Exception.class, () -> {
            interactor.login("hi@mail.com", "correct");
        });
    }

    // the data which pass to the DAO is correct
    @Test
    void daoReceivesCorrectArguments() {
        interactor.login("a@b.com", "pass123");

        assertEquals("a@b.com", mockDAO.lastEmail);
        assertEquals("pass123", mockDAO.lastPassword);
    }

    @Test
    void daoIsCalledWhenInputIsValid() {
        mockDAO.returnedJson = null;
        interactor.login("x@y.com", "abc");

        assertEquals("x@y.com", mockDAO.lastEmail);
        assertEquals("abc", mockDAO.lastPassword);
    }
}
