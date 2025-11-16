package UseCases.Login;

public interface LoginDataAccessInterface {
    String login(String email, String password);
    // I change the attribute from boolean to String
}
