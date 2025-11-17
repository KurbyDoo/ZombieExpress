package UseCases.Register;

public class RegisterOutputData {
    private final String email;
    private final String uid;

    public RegisterOutputData(String email, String uid) {
        this.email = email;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }
}
