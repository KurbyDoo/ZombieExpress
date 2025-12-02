package framework.app;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.Bullet;
import framework.view.ViewManager;
import framework.view.ViewType;

public class Main extends Game {

    private final ViewManager viewManager;

    public Main(ViewManager vm) {
        this.viewManager = vm;
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_NONE);
        Bullet.init();
        viewManager.setGame(this);
        viewManager.switchTo(ViewType.LOGIN);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (viewManager != null) {
            viewManager.handleManualExit();
        }
    }
}
