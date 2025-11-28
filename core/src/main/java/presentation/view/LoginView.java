package presentation.view;

import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import application.interface_use_cases.register.RegisterInteractor;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import domain.player.PlayerSession;
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

    // private final MockLoginRegisterDataAccess mockUserDB;

    private FirebaseLoginRegisterDataAccess firebaseAuth;
    private final LoadPlayerDataInteractor loadPlayer;

    private final JButton goRegister = new JButton("Create Account");
    private final JTextField useremail = new JTextField(20);
    private final JPasswordField password = new JPasswordField(20);
    private final JButton loginButton = new JButton("Login");
    private final JLabel messageLabel = new JLabel("");

    public LoginView(LoginController loginController, LoginViewModel viewModel, FirebaseLoginRegisterDataAccess firebaseAuth,
                     LoadPlayerDataInteractor loadPlayer) {
        this.controller = loginController;
        this.viewModel = viewModel;
        // this.mockUserDB = mockUserDB;
        this.firebaseAuth = firebaseAuth;
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
            RegisterInteractor interactor = new RegisterInteractor(firebaseAuth, presenter);
            RegisterController controller = new RegisterController(interactor);

            new RegisterView(controller, viewModel, firebaseAuth, loadPlayer);
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

            case "playerSession":
                PlayerSession session = (PlayerSession) event.getNewValue();

                System.out.println("   Player data updated!");
                System.out.println("   UID = " + session.getUid());
                System.out.println("   Highest score = " + session.getHeightScore());

                JOptionPane.showMessageDialog(this,
                    "Welcome back!\nHigh Score: " + session.getHeightScore());

                break;
        }
        }
    }
