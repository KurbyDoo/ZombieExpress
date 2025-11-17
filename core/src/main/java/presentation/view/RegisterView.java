package presentation.view;

import UseCases.Login.LoginInteractor;
import UseCases.PlayerData.LoadPlayerDataInteractor;
import data_access.FirebaseAuthManager;
import data_access.MockLoginRegisterDataAccess;
import interface_adapter.*;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RegisterView extends JFrame implements PropertyChangeListener{

    private RegisterViewModel viewModel;
    private RegisterController controller;
    private MockLoginRegisterDataAccess mockUserDB;
    private final LoadPlayerDataInteractor loadPlayer;

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JPasswordField confirmField = new JPasswordField(20);
    private final JButton registerButton = new JButton("Create Account");
    private final JLabel messageLabel = new JLabel("");

    public RegisterView(RegisterController  controller, RegisterViewModel viewModel, MockLoginRegisterDataAccess mockUserDB,
                        LoadPlayerDataInteractor loadPlayer) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.mockUserDB = mockUserDB;
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
                            mockUserDB, presenter
                        );
                        LoginController loginController = new LoginController(loginInteractor);

                        new LoginView(loginController, viewModel, mockUserDB, loadPlayer);
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
