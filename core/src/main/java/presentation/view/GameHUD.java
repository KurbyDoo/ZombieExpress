package presentation.view;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import domain.entities.Player;

public class GameHUD {

    private final Stage uiStage;

    private final HudElement[] hudElements;

    public GameHUD(Player player) {
        this.uiStage = new Stage(new ScreenViewport());

        BitmapFont uiFont = new BitmapFont();
        Label.LabelStyle style = new Label.LabelStyle(uiFont, Color.WHITE);

        hudElements = new HudElement[] {
            new TimeHudElement(uiStage, style),
            new DistanceHudElement(uiStage, style, player),
            new HealthHudElement(uiStage, player),
            new AmmoHudElement(uiStage, style, player),
            new HotbarHudElement(uiStage, style, player)
        };
    }

    public void update(float deltaTime) {
        for (HudElement element : hudElements) {
            element.update(deltaTime);
        }
        uiStage.act(deltaTime);
    }

    public void render() {
        uiStage.draw();
    }

    public void dispose() {
        uiStage.dispose();
    }
}
