/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/HUD)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package presentation.view.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import application.game_use_cases.exit_game.ExitGameUseCase;
import domain.repositories.EntityStorage;
import domain.player.Player;
import presentation.controllers.PickupController;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class GameHUD {

    private final Stage uiStage;
    private final HudElement[] hudElements;
    private final ExitGameUseCase exitGameUseCase;
    private final Skin skin;
    private boolean dialogShown = false;

    public GameHUD(Player player, EntityStorage entityStorage, PickupController pickupController, ExitGameUseCase exitGameUseCase) {
        this.uiStage = new Stage(new ScreenViewport());
        this.exitGameUseCase = exitGameUseCase;

        this.skin = createGameSkin();


        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label.LabelStyle mainStyle  = UIFontFactory.createMainHudStyle();
        Label.LabelStyle largeStyle = UIFontFactory.createLargeHudStyle();

        hudElements = new HudElement[] {
            new TimeHudElement(uiStage, mainStyle),
            new DistanceHudElement(uiStage, mainStyle, player),
            new FuelHudElement(uiStage, mainStyle, entityStorage),
            new HealthHudElement(uiStage, player),
            new AmmoHudElement(uiStage, mainStyle, player),
            new HeldItemHudElement(uiStage, player),
            new HotbarHudElement(uiStage, style, player),
            new PickupPromptHudElement(uiStage, largeStyle, pickupController),
        };
    }

    /**
     * Returns the Stage object for the InputMultiplexer in GameView to use.
     * @return The UI Stage.
     */
    public Stage getUiStage() {
        return uiStage;
    }

    /**
     * Creates a simple colored Drawable (a 1x1 white pixel stretched to fit).
     * @param color The color to tint the Drawable.
     * @return The TextureRegionDrawable.
     */
    private Drawable getColoredDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(texture);
    }

    /**
     * Attempts to load a standard LibGDX skin or creates a basic fallback skin for dialogs,
     * using fonts provided by the UIFontFactory.
     */
    private Skin createGameSkin() {
        try {
            // Priority 1: Try loading the complete style guide from file
            return new Skin(Gdx.files.internal("data/uiskin.json"));
        } catch (Exception e) {
            // Priority 2: Fallback to a programmatic skin using the custom fonts
            Skin skin = new Skin();

            BitmapFont font = UIFontFactory.createMainHudStyle().font;

            font.getData().setScale(1.5f);
            skin.add("default-font", font);

            Drawable dialogBackground = getColoredDrawable(new Color(0.35f, 0.0f, 0.05f, 0.95f));

            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
            skin.add("default", labelStyle);

            BitmapFont titleFont = UIFontFactory.createLargeHudStyle().font;
            titleFont.getData().setScale(1.0f);
            WindowStyle dialogStyle = new Dialog.WindowStyle(titleFont, new Color(0, 0, 0, 0), dialogBackground);
            skin.add("default", dialogStyle);

            return skin;
        }
    }

    /**
     * Displays the game over or victory dialog when called by the GameView.
     * @param message The message (Win or Loss) to display.
     */
    public void showEndGameDialog(String message) {
        if (dialogShown) return;
        String title = "";

        Dialog dialog = new Dialog(title, this.skin, "default") {
            @Override
            protected void result(Object object) {
                exitGameUseCase.execute();
            }
        };

        float dialogWidth = Gdx.graphics.getWidth() * 0.8f;
        float dialogHeight = Gdx.graphics.getHeight() * 0.6f;

        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setPosition((Gdx.graphics.getWidth() - dialogWidth) / 2, (Gdx.graphics.getHeight() - dialogHeight) / 2);
        dialog.setMovable(false);

        dialog.getContentTable().clear();
        dialog.getButtonTable().clear();

        Label messageLabel = new Label(message, this.skin);
        messageLabel.setWrap(true);
        messageLabel.setFontScale(2.2f);
        messageLabel.setColor(Color.YELLOW);
        messageLabel.setAlignment(Align.center);

        dialog.getContentTable().add(messageLabel)
            .pad(40)
            .width(dialogWidth * 0.9f)
            .expand().fill()
            .row();

        Label instructionLabel = new Label("PRESS ESC TO EXIT", this.skin);
        instructionLabel.setFontScale(1.5f);
        instructionLabel.setColor(Color.RED);
        instructionLabel.setAlignment(Align.center);

        dialog.getButtonTable().add(instructionLabel)
            .pad(30)
            .expandX().fillX()
            .center();

        dialog.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    // FIX: Directly call the use case execute method instead of the protected dialog.result(null)
                    exitGameUseCase.execute();
                    return true; // Event consumed
                }
                return false;
            }
        });

        dialog.show(uiStage);
        dialogShown = true;
    }



    public void update(float deltaTime) {
        if (!dialogShown) {
            for (HudElement element : hudElements) {
                element.update(deltaTime);
            }
        }
        uiStage.act(deltaTime);
    }

    public void render() {
        uiStage.draw();
    }

    public void dispose() {
        uiStage.dispose();
        this.skin.dispose();
    }
}
