// TODO: Implement PropertyChangeListener in this class to refresh UI when ViewModel updates.

package presentation.view;

import UseCases.PlayerData.LoadPlayerDataInteractor;
import UseCases.Register.RegisterInteractor;
import data_access.login.MockLoginRegisterDataAccess;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginViewModel;
import interface_adapter.register.RegisterController;
import interface_adapter.register.RegisterPresenter;
import interface_adapter.register.RegisterViewModel;

import java.awt.*;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends JFrame implements PropertyChangeListener {
    private LoginViewModel viewModel;
    private LoginController controller;
    private final MockLoginRegisterDataAccess mockUserDB;
    private final LoadPlayerDataInteractor loadPlayer;

    private final JButton goRegister = new JButton("Create Account");
    private final JTextField useremail = new JTextField(20);
    private final JPasswordField password = new JPasswordField(20);
    private final JButton loginButton = new JButton("Login");
    private final JLabel messageLabel = new JLabel("");

    public LoginView(LoginController loginController, LoginViewModel viewModel, MockLoginRegisterDataAccess mockUserDB,
                     LoadPlayerDataInteractor loadPlayer) {
        this.controller = loginController;
        this.viewModel = viewModel;
        this.mockUserDB = mockUserDB;
        this.loadPlayer = loadPlayer;

        viewModel.addPropertyChangeListener(this);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("LoginToAliveRail");

        initUI();
        addListeners();
        setVisible(true);

    }
    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(6,1,8,6));

        panel.add(new JLabel("Email:"));
        panel.add(useremail);

        panel.add(new JLabel("Password:"));
        panel.add(password);

        panel.add(loginButton);
        panel.add(goRegister);
        panel.add(messageLabel);

        add(panel);
    }
    private void addListeners() {
        loginButton.addActionListener(e -> {
            String email = useremail.getText();
            String passwordText = new String(password.getPassword());
            controller.login(email, passwordText);

        });
        goRegister.addActionListener(e -> {
            RegisterViewModel viewModel = new RegisterViewModel();
            RegisterPresenter presenter = new RegisterPresenter(viewModel);
            RegisterInteractor interactor = new RegisterInteractor(mockUserDB, presenter);
            RegisterController controller = new RegisterController(interactor);

            new RegisterView(controller, viewModel, mockUserDB, loadPlayer);

            dispose();
        });
    }
    @Override
    public void propertyChange(PropertyChangeEvent event){
        switch (event.getPropertyName()) {
            case "successfulLogin":
                boolean success = (boolean) event.getNewValue();
                if (success) {
                    messageLabel.setText("Login successful");
                }
                break;

            case "errorMessage":
                String errorMessage = (String) event.getNewValue();
                if (errorMessage != null) {
                    messageLabel.setText(errorMessage);
                }
                break;

            case "email":
                String email = (String) event.getNewValue();
                break;
        }
    }
}

// TODO: Update Swing UI components based on new ViewModel state.

