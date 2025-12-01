package presentation.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class BaseAuthView extends ScreenAdapter {

    protected final ViewManager viewManager;

    protected Stage stage;
    protected Skin skin;
    private Texture logoTexture;

    protected BaseAuthView(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        BitmapFont font = skin.getFont("default-font");
        font.getData().setScale(1.5f);
        skin.get(Label.LabelStyle.class).font = font;
        skin.get(TextField.TextFieldStyle.class).font = font;
        skin.get(TextButton.TextButtonStyle.class).font = font;
        skin.get(TextField.TextFieldStyle.class).messageFont = font;

        logoTexture = new Texture(Gdx.files.internal("logo.png"));
        Image logoImage = new Image(logoTexture);
        Texture bg = new Texture(Gdx.files.internal("background.png"));
        Image bgImage = new Image(bg);
        bgImage.setFillParent(true);
        bgImage.setScaling(Scaling.fill);

        Table root = new Table();
        root.setFillParent(true);
        root.top();
        root.add(logoImage).expandX().center().row();

        buildContent(root);
        stage.addActor(bgImage);
        stage.addActor(root);
    }

    /**
     * Subclasses (LoginView, RegisterView) implement this
     * to add their specific form UI.
     */
    protected abstract void buildContent(Table root);

    protected void addLabeledField(Table table, String labelText, TextField field) {
        table.add(new Label(labelText, skin));
        table.row();
        table.add(field).width(450).pad(5);
        table.row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (logoTexture != null) logoTexture.dispose();
    }
}
