package presentation.view.hud;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import data_access.EntityStorage;
import domain.entities.IdToEntityStorage;
import domain.entities.Train;
import domain.player.Player;
import presentation.controllers.PickupController;

public class GameHUD {

    private final Stage uiStage;
    private final HudElement[] hudElements;

    public GameHUD(Player player, EntityStorage entityStorage, PickupController pickupController) {
        this.uiStage = new Stage(new ScreenViewport());

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
