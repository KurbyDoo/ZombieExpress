package UseCases.Register;

public interface RegisterOutputBoundary {
    void registerSuccess(RegisterOutputData data);
    void registerFail(String errorMessage);
}
