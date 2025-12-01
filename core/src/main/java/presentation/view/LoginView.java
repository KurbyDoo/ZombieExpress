package presentation.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginViewModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends ScreenAdapter implements PropertyChangeListener {

    private final ViewManager viewManager;
    private final LoginController controller;
    private final LoginViewModel viewModel;

    private Stage stage;
    private Skin skin;

    private TextField emailField;
    private TextField passwordField;
    private Label messageLabel;

    public LoginView(ViewManager viewManager,
                     LoginController controller,
                     LoginViewModel viewModel) {
        this.viewManager = viewManager;
        this.controller = controller;
        this.viewModel = viewModel;

        viewModel.addPropertyChangeListener(this);
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
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('●');
        messageLabel = new Label("", skin);

        TextButton loginBtn = new TextButton("Login", skin);
        TextButton registerBtn = new TextButton("Create Account", skin);

        loginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.login(
                    emailField.getText(),
                    passwordField.getText()
                );
            }
        });

        registerBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                viewManager.switchTo(ViewType.REGISTER);
            }
        });

        table.add(new Label("Email:", skin)).pad(5); table.row();
        table.add(emailField).width(300).pad(5); table.row();

        table.add(new Label("Password:", skin)).pad(5); table.row();
        table.add(passwordField).width(300).pad(5); table.row();

        table.add(loginBtn).pad(10); table.row();
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
                System.out.println("[LoginView] PlayerSession received → switching to GAME");
                viewManager.switchTo(ViewType.GAME);
                break;
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
