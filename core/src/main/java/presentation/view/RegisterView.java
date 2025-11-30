package presentation.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import interface_adapter.register.RegisterController;
import interface_adapter.register.RegisterViewModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RegisterView extends ScreenAdapter implements PropertyChangeListener {

    private final ViewManager viewManager;
    private final RegisterController controller;
    private final RegisterViewModel viewModel;

    private Stage stage;
    private Skin skin;

    private TextField emailField;
    private TextField passwordField;
    private TextField confirmField;
    private Label messageLabel;

    public RegisterView(ViewManager vm,
                        RegisterController controller,
                        RegisterViewModel vmRegister) {

        this.viewManager = vm;
        this.controller = controller;
        this.viewModel = vmRegister;

        vmRegister.addPropertyChangeListener(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        emailField = new TextField("", skin);
        passwordField = new TextField("", skin);
        confirmField = new TextField("", skin);

        passwordField.setPasswordCharacter('●');
        passwordField.setPasswordMode(true);

        confirmField.setPasswordCharacter('●');
        confirmField.setPasswordMode(true);

        messageLabel = new Label("", skin);
        TextButton registerBtn = new TextButton("Register", skin);

        registerBtn.addListener(event -> {
            controller.register(
                emailField.getText(),
                passwordField.getText(),
                confirmField.getText()
            );
            return true;
        });

        table.add(new Label("Email:", skin)).pad(5); table.row();
        table.add(emailField).width(300).pad(5); table.row();

        table.add(new Label("Password:", skin)).pad(5); table.row();
        table.add(passwordField).width(300).pad(5); table.row();

        table.add(new Label("Confirm:", skin)).pad(5); table.row();
        table.add(confirmField).width(300).pad(5); table.row();

        table.add(registerBtn).pad(10); table.row();
        table.add(messageLabel).pad(10);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
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

    @Override
    public void dispose() {
        stage.dispose();
    }
}
