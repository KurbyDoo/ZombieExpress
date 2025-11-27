package presentation.view.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import data_access.EntityStorage;
import domain.entities.Entity;
import domain.entities.IdToEntityStorage;
import domain.entities.Rideable;
import domain.entities.Train;
import domain.player.Player;

import static presentation.view.hud.UIAssetFactory.*;

/**
 * Vertical fuel bar HUD, shown above the ammo counter.
 * Depends on a FuelProvider-like object that can report fuel values.
 */
public class FuelHudElement implements HudElement {

    private final EntityStorage entityStorage;
    private final Image fuelBar;

    public FuelHudElement(Stage stage, Label.LabelStyle style, EntityStorage entityStorage) {
        this.entityStorage = entityStorage;

        Table table = new Table();
        table.setFillParent(true);
        table.bottom().left().padLeft(10f).padBottom(185);

        float barWidth  = 50;
        float barHeight = 260;
        Drawable backgroundFuel = createBarDrawable(new Color(0.1f, 0.1f, 0.1f, 1), (int) barWidth, (int) barHeight);
        Drawable orangeFuel  = createBarDrawable(Color.ORANGE, (int) barWidth, (int) barHeight);
        Drawable fuelBorder = createBorderDrawable((int) barWidth, (int) barHeight);

        Image fuelBackground = new Image(backgroundFuel);
        fuelBar = new Image(orangeFuel);
        Image fuelBorderImage = new Image(fuelBorder);

        Stack stack = new Stack();
        stack.add(fuelBackground);
        stack.add(fuelBar);
        stack.add(fuelBorderImage);

        Label fuelLabel = new Label("Fuel", style);
        fuelLabel.setAlignment(Align.center);

        table.add(fuelLabel).padBottom(4).width(barWidth);
        table.row();
        table.add(stack).width(barWidth).height(barHeight);

        stage.addActor(table);
    }

    @Override
    public void update(float deltaTime) {
        Train train = null;
        for (int id : entityStorage.getAllIds()) {
            Entity e = entityStorage.getEntityByID(id);
            if (!(e instanceof Train)) continue;
            train = (Train) e;
        }

        if (train == null) {
            fuelBar.setScaleY(0);
            return;
        }

        int current = train.getCurrentFuel();
        int max = train.getMaxFuel();

        float ratio = (current / (float) max);
        if (ratio < 0) ratio = 0;
        if (ratio > 1) ratio = 1;

        // scale the bar vertically
        fuelBar.setScaleY(ratio);
    }
}
