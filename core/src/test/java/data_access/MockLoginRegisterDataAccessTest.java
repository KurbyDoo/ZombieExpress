package data_access;


import data_access.mock_logic.login.MockLoginRegisterDataAccess;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class MockLoginRegisterDataAccessTest {
    @Test
    public void testRegisterSuccess() {
        MockLoginRegisterDataAccess dataAccess = new MockLoginRegisterDataAccess();
        String uid = dataAccess.newUser("a@gmail.com", "password1");

        assertNotNull(uid);
        assertEquals(uid, dataAccess.login("a@gmail.com", "password1"));
    }

    @Test
    public void testRegisterWithSameEmail() {
        MockLoginRegisterDataAccess dataAccess = new MockLoginRegisterDataAccess();
       dataAccess.newUser("a@gmail.com", "password1");
       String uid2 = dataAccess.newUser("a@gmail.com","password2" );

       assertNull(uid2);
       // The second registration should fail, and the returned user ID should be null.
    }

}
