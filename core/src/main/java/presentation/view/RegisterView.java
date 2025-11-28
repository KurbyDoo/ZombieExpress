package presentation.view;

import application.interface_use_cases.login.LoginInteractor;
import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.register.RegisterController;
import interface_adapter.register.RegisterViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RegisterView extends JFrame implements PropertyChangeListener{

    private RegisterViewModel viewModel;
    private RegisterController controller;
    // private MockLoginRegisterDataAccess mockUserDB;
    // I use the mockUserDB to test the mock Register logic but now we need to use the real
    // data access(firebase)

    private FirebaseLoginRegisterDataAccess firebaseAuth;
    private final LoadPlayerDataInteractor loadPlayer;

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JPasswordField confirmField = new JPasswordField(20);
    private final JButton registerButton = new JButton("Create Account");
    private final JLabel messageLabel = new JLabel("");

    public RegisterView(RegisterController  controller, RegisterViewModel viewModel, FirebaseLoginRegisterDataAccess firebaseAuth,
                        LoadPlayerDataInteractor loadPlayer) {
        this.controller = controller;
        this.viewModel = viewModel;
        //this.mockUserDB = mockUserDB;
        this.firebaseAuth = firebaseAuth;
        this.loadPlayer = loadPlayer;

        viewModel.addPropertyChangeListener(this);

        setTitle("New Player? Create your Account first!");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        addListeners();

        setVisible(true);
    }
    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5,1,8,6));

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmField);

        panel.add(registerButton);
        panel.add(messageLabel);

        add(panel);
    }

    private void addListeners() {
        registerButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmField.getPassword());
            controller.register(email, password, confirmPassword);
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case "successfulRegister":
                if ((Boolean) event.getNewValue()) {
                    messageLabel.setText("Account Created!");

                    SwingUtilities.invokeLater(() -> {
                        LoginViewModel viewModel = new LoginViewModel();
                        LoginPresenter presenter = new LoginPresenter(viewModel,loadPlayer);

                        LoginInteractor loginInteractor = new LoginInteractor(
                            firebaseAuth, presenter
                        );
                        LoginController loginController = new LoginController(loginInteractor);

                        new LoginView(loginController, viewModel, firebaseAuth, loadPlayer);
                        dispose();
                    });

                }
                break;
            case "errorMessage":
                messageLabel.setText((String) event.getNewValue());
                break;
        }
    }
}
