package presentation.view;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TimeHudElement implements HudElement {

    private final Label timeLabel;
    private float elapsedTime = 0;

    public TimeHudElement(Stage stage, Label.LabelStyle style) {
        timeLabel = new Label("", style);
        timeLabel.setFontScale(2);

        Table table = new Table();
        table.setFillParent(true);
        table.top().left().padTop(10).padLeft(10);
        table.add(timeLabel);

        stage.addActor(table);
    }

    @Override
    public void update(float deltaTime) {
        elapsedTime += deltaTime;

        int totalSeconds = (int) elapsedTime;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        timeLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }
}
