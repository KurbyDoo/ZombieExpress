package UseCases.Register;

public interface RegisterInputBoundary {
    void register(String username, String password, String confirmPassword);
}
