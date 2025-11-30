package presentation.view.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import application.game_use_cases.exit_game.ExitGameUseCase;
import interface_adapter.game.EntityStorage;
import domain.player.Player;
import presentation.controllers.PickupController;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;

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
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        drawable.tint(color);
        return drawable;
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

            Drawable dialogBackground = getColoredDrawable(new Color(0.3f, 0.05f, 0.05f, 0.95f)); // Dark red, almost black

            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
            skin.add("default", labelStyle);

            TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.font = font;
            textButtonStyle.fontColor = Color.BLACK; // Black text on bright button

            Color buttonColor = new Color(0.5f, 0.8f, 0.0f, 1f); // Eerie Yellow-Green
            Color downColor = new Color(0.4f, 0.6f, 0.0f, 1f); // Darker Green
            Color overColor = new Color(0.6f, 0.9f, 0.1f, 1f); // Lighter Green/Yellow

            TextureRegionDrawable upDrawable = (TextureRegionDrawable) getColoredDrawable(buttonColor);
            upDrawable.setPadding(15, 15, 15, 15); // Increased button padding

            textButtonStyle.up = upDrawable;
            textButtonStyle.down = getColoredDrawable(downColor);
            textButtonStyle.over = getColoredDrawable(overColor);

            skin.add("default", textButtonStyle);

            BitmapFont titleFont = UIFontFactory.createLargeHudStyle().font;
            titleFont.getData().setScale(1.5f); // Scale title font as well
            WindowStyle dialogStyle = new Dialog.WindowStyle(titleFont, Color.YELLOW, dialogBackground);
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
        String title = message.toLowerCase().contains("conquered") ?
            "!!! VICTORY ACHIEVED !!!" :
            "GAME OVER - FAILED";

        Dialog dialog = new Dialog(title, this.skin, "default") {
            @Override
            protected void result(Object object) {
                exitGameUseCase.execute();
            }
        };

        float dialogWidth = Gdx.graphics.getWidth() * 0.7f;
        float dialogHeight = Gdx.graphics.getHeight() * 0.5f;

        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setPosition((Gdx.graphics.getWidth() - dialogWidth) / 2, (Gdx.graphics.getHeight() - dialogHeight) / 2);
        dialog.setMovable(false);

        dialog.getContentTable().clear();
        dialog.getButtonTable().clear();

        Label messageLabel = new Label(message, this.skin);
        messageLabel.setFontScale(2.0f);
        messageLabel.setColor(Color.YELLOW);
        messageLabel.setAlignment(Align.center);

        dialog.getContentTable().add(messageLabel)
            .padTop(60).padBottom(80)
            .expandX().fillX()
            .row();

        TextButton exitButton = new TextButton("Exit Game", this.skin);

        dialog.getButtonTable().add(exitButton)
            .pad(30)
            .minWidth(300).minHeight(80);

        dialog.button(exitButton, true);

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
