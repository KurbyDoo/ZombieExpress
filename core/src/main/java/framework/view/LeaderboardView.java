package framework.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import domain.player.PlayerData;
import interface_adapter.controllers.LeaderboardController;
import interface_adapter.view_models.LeaderboardViewModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LeaderboardView extends AbstractBaseAuthView implements PropertyChangeListener {

    private final LeaderboardController controller;
    private final LeaderboardViewModel viewModel;

    private Table scoreTable;

    public LeaderboardView(ViewManager viewManager,
                           LeaderboardController controller,
                           LeaderboardViewModel viewModel) {
        super(viewManager);
        this.controller = controller;
        this.viewModel = viewModel;

        this.viewModel.addPropertyChangeListener(this);
    }

    @Override
    protected void buildContent(Table root) {

        Label title = new Label("Leaderboard", skin);
        root.add(title).pad(15).row();

        scoreTable = new Table(skin);
        root.add(scoreTable).pad(20).row();

        // Start Game button
        TextButton startGameBtn = new TextButton("Start Game", skin);
        startGameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.startGame();
            }
        });
        root.add(startGameBtn).pad(15);

        controller.load(); // Load from Firebase
    }

    private void refreshLeaderboard() {
        scoreTable.clearChildren();

        if (viewModel.getLeaderboard() == null) {
            return;
        }

        int rank = 1;
        for (PlayerData p : viewModel.getLeaderboard()) {
            String row = rank + ". " + p.getEmail() + " - " + p.getHighScore();

            Label label = new Label(row, skin);
            scoreTable.add(label).left().padBottom(8).row();

            rank++;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("leaderboard".equals(evt.getPropertyName())) {
            refreshLeaderboard();
        }
    }
}
