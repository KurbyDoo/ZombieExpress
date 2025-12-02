package framework.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import interface_adapter.controllers.LoginController;
import interface_adapter.view_models.LoginViewModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends AbstractBaseAuthView implements PropertyChangeListener {

    private final LoginController controller;

    private TextField emailField;
    private TextField passwordField;
    private Label messageLabel;

    public LoginView(ViewManager viewManager,
                     LoginController controller,
                     LoginViewModel viewModel) {
        super(viewManager);
        this.controller = controller;

        viewModel.addPropertyChangeListener(this);
    }

    @Override
    protected void buildContent(Table root) {
        emailField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        messageLabel = new Label("", skin);

        TextButton loginBtn = new TextButton("Login", skin);
        TextButton registerBtn = new TextButton("Create Account", skin);

        loginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.login(emailField.getText(), passwordField.getText());
            }
        });

        registerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                viewManager.switchTo(ViewType.REGISTER);
            }
        });

        addLabeledField(root, "Email:", emailField);
        addLabeledField(root, "Password:", passwordField);

        root.add(loginBtn).pad(10).row();
        root.add(registerBtn).pad(10).row();
        root.add(messageLabel).pad(10);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {

            case "errorMessage":
                messageLabel.setText((String) evt.getNewValue());
                break;

            case "successfulLogin":
                if ((boolean) evt.getNewValue()) {
                    messageLabel.setText("Login Successful!");
                }
                break;

            case "playerSession":
                viewManager.switchTo(ViewType.LEADERBOARD);
                break;
        }
    }
}
