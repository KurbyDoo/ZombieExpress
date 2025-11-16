// TODO: Implement PropertyChangeListener in this class to refresh UI when ViewModel updates.

package presentation.view;

import interface_adapter.LoginController;
import interface_adapter.LoginViewModel;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends JFrame implements PropertyChangeListener {
    private LoginViewModel viewModel;
    private LoginController controller;

    private final JTextField useremail = new JTextField(20);
    private final JPasswordField password = new JPasswordField(20);
    private final JButton loginButton = new JButton("Login");
    private final JLabel messageLabel = new JLabel("");

    public LoginView(LoginController loginController, LoginViewModel viewModel) {
        this.controller = loginController;
        this.viewModel = viewModel;

        viewModel.addPropertyChangeListener(this);

        setSize(800, 600);
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
        panel.add(messageLabel);

        add(panel);
    }
    private void addListeners() {
        loginButton.addActionListener(e -> {
            String email = useremail.getText();
            String passwordText = new String(password.getPassword());
            controller.login(email, passwordText);

        });
    }
    @Override
    public void propertyChange(PropertyChangeEvent event){
        if (viewModel.isSuccessfulLogin()){
            messageLabel.setText("Login Successful");
        }else{
            messageLabel.setText(viewModel.getErrorMessage());
        }
    }
}

// TODO: Update Swing UI components based on new ViewModel state.

