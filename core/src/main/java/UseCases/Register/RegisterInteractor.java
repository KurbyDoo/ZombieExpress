package UseCases.Register;

/**
 * this is a mock version
 */

public class RegisterInteractor implements RegisterInputBoundary{
    private final RegisterUserDataAccessInterface dataAccess;
    private final RegisterOutputBoundary presenter;

    public RegisterInteractor(RegisterUserDataAccessInterface dataAccess, RegisterOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void register(String email, String password, String confirmPassword){
        if (!password.equals(confirmPassword)){
            presenter.registerFail("Passwords do not match");
            return;
        }

        // else create a new user
        String uid = dataAccess.newUser(email, password);

        if (uid == null){
            presenter.registerFail("Email is invalid");
        }

        presenter.registerSuccess(new RegisterOutputData(email, uid));
    }
}
