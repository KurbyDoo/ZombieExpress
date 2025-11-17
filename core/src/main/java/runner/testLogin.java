package runner;

import UseCases.Login.*;
import interface_adapter.*;
import data_access.FirebaseAuthManager;
import presentation.view.LoginView;

public class testLogin {
    public static void main(String[] args) {

        LoginViewModel vm = new LoginViewModel();
        LoginPresenter presenter = new LoginPresenter(vm, null);

        LoginDataAccessInterface firebaseDAO = new FirebaseAuthManager();
        LoginInteractor interactor = new LoginInteractor(firebaseDAO, presenter);
        LoginController controller = new LoginController(interactor);

       // mock email and password: hahaha@gmail.com + hahaha123
        new LoginView(controller, vm, null,null);
    }
}
