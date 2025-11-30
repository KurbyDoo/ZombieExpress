//package runner;
//
//import application.interface_use_cases.login.LoginDataAccessInterface;
//import application.interface_use_cases.login.LoginInteractor;
//import data_access.mock_logic.login.MockfirebaseAuthManager;
//import interface_adapter.login.LoginController;
//import interface_adapter.login.LoginPresenter;
//import interface_adapter.login.LoginViewModel;
//import presentation.view.LoginView;
//
//public class testLogin {
//    public static void main(String[] args) {
//
//        LoginViewModel vm = new LoginViewModel();
//        LoginPresenter presenter = new LoginPresenter(vm, null);
//
//        LoginDataAccessInterface firebaseDAO = new MockfirebaseAuthManager();
//        LoginInteractor interactor = new LoginInteractor(firebaseDAO, presenter);
//        LoginController controller = new LoginController(interactor);
//
//       // mock email and password: hahaha@gmail.com + hahaha123
//        new LoginView(controller, vm, null,null);
//    }
//}
