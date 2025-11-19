package presentation.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import domain.entities.Player;

import static presentation.view.UIAssetFactory.createBarDrawable;
import static presentation.view.UIAssetFactory.createBorderDrawable;

public class HealthHudElement implements HudElement {

    private final Player player;
    private final Image healthBar;

    private static final float HEALTH_BAR_WIDTH  = 260;
    private static final float HEALTH_BAR_HEIGHT = 30;

    public HealthHudElement(Stage stage, Player player) {
        this.player = player;

        Table healthTable = new Table();
        healthTable.setFillParent(true);
        healthTable.top().right().padTop(10).padRight(10);

        Drawable redHealth = createBarDrawable(new Color(0.3f, 0, 0, 1),
            (int) HEALTH_BAR_WIDTH, (int) HEALTH_BAR_HEIGHT);
        Drawable greenHealth = createBarDrawable(Color.GREEN,
            (int) HEALTH_BAR_WIDTH, (int) HEALTH_BAR_HEIGHT);
        Drawable healthBorder = createBorderDrawable(
            (int) HEALTH_BAR_WIDTH, (int) HEALTH_BAR_HEIGHT);

        Image healthBackground = new Image(redHealth);
        healthBar = new Image(greenHealth);
        Image healthBorderImage = new Image(healthBorder);

        Stack stack = new Stack();
        stack.add(healthBackground);
        stack.add(healthBar);
        stack.add(healthBorderImage);

        healthTable.add(stack)
            .width(HEALTH_BAR_WIDTH)
            .height(HEALTH_BAR_HEIGHT);

        stage.addActor(healthTable);
    }

    @Override
    public void update(float deltaTime) {
        int current = player.getCurrentHealth();
        int max = player.getMaxHealth();
        float ratio = (max > 0) ? (current / (float) max) : 0;
        if (ratio < 0) ratio = 0;
        if (ratio > 1) ratio = 1;

        healthBar.setScaleX(ratio);
    }
}
