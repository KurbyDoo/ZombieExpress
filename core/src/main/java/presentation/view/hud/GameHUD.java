package presentation.view.hud;

import com.badlogic.gdx.Gdx;
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

            // Get the font from your factory to ensure consistency
            BitmapFont font = UIFontFactory.createMainHudStyle().font;

            // Note: Dispose is handled by the calling font factory, so we just add the reference
            skin.add("default-font", font);

            // 1. Create a background drawable for the window/dialog
            Drawable dialogBackground = getColoredDrawable(new Color(0.1f, 0.1f, 0.1f, 0.9f)); // Dark translucent background

            // 2. Add Label Style (used for dialog body text)
            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.RED);
            skin.add("default", labelStyle);

            // 3. Add TextButton Style
            TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.font = font;
            textButtonStyle.fontColor = Color.WHITE;
            textButtonStyle.up = getColoredDrawable(new Color(0.2f, 0.4f, 0.7f, 1f)); // Blue background
            textButtonStyle.down = getColoredDrawable(new Color(0.1f, 0.2f, 0.4f, 1f)); // Darker blue on press
            textButtonStyle.over = getColoredDrawable(new Color(0.3f, 0.5f, 0.8f, 1f)); // Lighter blue on hover
            skin.add("default", textButtonStyle);

            // 4. Add Dialog/Window Style (using the required 3-arg constructor)
            // (Title Font, Title Color, Background Drawable)
            Dialog.WindowStyle dialogStyle = new Dialog.WindowStyle(font, Color.WHITE, dialogBackground); // <<< FIXED CONSTRUCTOR
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

        // We use the stored skin field directly in the Dialog constructor

        // Determine the title based on the message content (Win/Loss)
        String title = message.toLowerCase().contains("conquered") ?
            "VICTORY ACHIEVED" :
            "GAME OVER";

        Dialog dialog = new Dialog(title, this.skin, "default") { // <<< CORRECT USAGE OF this.skin
            // Override result to ensure the exit use case is called when the button is pressed
            @Override
            protected void result(Object object) {
                // Delegate the shutdown request to the Use Case (Clean Architecture)
                exitGameUseCase.execute();
            }
        };

        // Sets the requested text
        dialog.text(message).pad(20).center();

        TextButton exitButton = new TextButton("Exit Game", this.skin); // <<< CORRECT USAGE OF this.skin

        // Add the button; clicking it calls the result(Object) method above.
        dialog.button(exitButton, true);

        // Show the dialog, making it modal and capturing input
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
