package presentation.view;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import interface_adapter.register.RegisterController;
import interface_adapter.register.RegisterViewModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RegisterView extends BaseAuthView implements PropertyChangeListener {

    private final RegisterController controller;
    private final RegisterViewModel viewModel;

    private TextField emailField;
    private TextField passwordField;
    private TextField confirmField;
    private Label messageLabel;

    public RegisterView(ViewManager viewManager,
                        RegisterController controller,
                        RegisterViewModel vmRegister) {
        super(viewManager);
        this.controller = controller;
        this.viewModel = vmRegister;

        vmRegister.addPropertyChangeListener(this);
    }

    @Override
    protected void buildContent(Table root) {
        emailField = new TextField("", skin);
        passwordField = new TextField("", skin);
        confirmField = new TextField("", skin);

        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        confirmField.setPasswordMode(true);
        confirmField.setPasswordCharacter('*');

        messageLabel = new Label("", skin);
        TextButton registerBtn = new TextButton("Register", skin);
        TextButton backToLoginBtn = new TextButton("Back to Login", skin);

        registerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.register(
                    emailField.getText(),
                    passwordField.getText(),
                    confirmField.getText()
                );
            }
        });

        backToLoginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                viewManager.switchTo(ViewType.LOGIN);
            }
        });

        addLabeledField(root, "Email:", emailField);
        addLabeledField(root, "Password:", passwordField);
        addLabeledField(root, "Confirm:", confirmField);

        root.add(registerBtn).pad(10).row();
        root.add(backToLoginBtn).pad(10).row();
        root.add(messageLabel).pad(10);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {

        switch (event.getPropertyName()) {

            case "successfulRegister":
                if ((boolean) event.getNewValue()) {
                    messageLabel.setText("Account created!");
                    viewManager.switchTo(ViewType.LOGIN);
                }
                break;

            case "errorMessage":
                messageLabel.setText((String) event.getNewValue());
                break;
        }
    }
}
