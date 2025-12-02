package framework.view.hud;

import static framework.view.hud.UIAssetFactory.createBarDrawable;
import static framework.view.hud.UIAssetFactory.createBorderDrawable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import domain.player.Player;

public class HealthHudElement implements HudElement {

    private final Player player;
    private final Image healthBar;

    public HealthHudElement(Stage stage, Player player) {
        this.player = player;

        Table healthTable = new Table();
        healthTable.setFillParent(true);
        healthTable.top().right().padTop(10).padRight(10);

        float barWidth = 260;
        float barHeight = 30;
        Drawable redHealth = createBarDrawable(new Color(0.3f, 0, 0, 1),
            (int) barWidth, (int) barHeight);
        Drawable greenHealth = createBarDrawable(Color.GREEN,
            (int) barWidth, (int) barHeight);
        Drawable healthBorder = createBorderDrawable(
            (int) barWidth, (int) barHeight);

        Image healthBackground = new Image(redHealth);
        healthBar = new Image(greenHealth);
        Image healthBorderImage = new Image(healthBorder);

        Stack stack = new Stack();
        stack.add(healthBackground);
        stack.add(healthBar);
        stack.add(healthBorderImage);

        healthTable.add(stack)
            .width(barWidth)
            .height(barHeight);

        stage.addActor(healthTable);
    }

    @Override
    public void update(float deltaTime) {
        float current = player.getCurrentHealth();
        float max = player.getMaxHealth();
        float ratio = (max > 0) ? (current / (float) max) : 0;
        if (ratio < 0) ratio = 0;
        if (ratio > 1) ratio = 1;

        healthBar.setScaleX(ratio);
    }
}
