package runner;

import UseCases.Login.*;
import interface_adapter.*;
import data_access.FirebaseAuthManager;
import presentation.view.LoginView;

public class testMain{
    public static void main(String[] args) {
    LoginViewModel viewModel = new LoginViewModel();
    LoginOutputBoundary presenter = new LoginPresenter(viewModel);
    LoginDataAccessInterface dataAccess = new FirebaseAuthManager();
    LoginInputBoundary interactor = new LoginInteractor(dataAccess, presenter);
    LoginController controller = new LoginController(interactor);
    new LoginView(controller, viewModel);
    }
}
