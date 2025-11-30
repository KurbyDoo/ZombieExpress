package io.github.testlibgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.bullet.Bullet;
import presentation.view.ViewManager;
import presentation.view.ViewType;

public class Main extends Game {

    private final ViewManager viewManager;

    public Main(ViewManager vm) {
        this.viewManager = vm;
    }

    @Override
    public void create() {
        Bullet.init();
        viewManager.setGame(this);
        viewManager.switchTo(ViewType.LOGIN);
    }
}
