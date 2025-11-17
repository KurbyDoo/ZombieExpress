package domain.entities;

import net.mgsx.gltf.scene3d.scene.Scene;

public class ZombieModel {
    private Zombie zombie;
    private Scene zombieScene;

    public ZombieModel(Zombie zombie, Scene zombieScene) {
        this.zombie = zombie;
        this.zombieScene = zombieScene;
    }

    public void setZombieLogic(Zombie zombie) {}

    public void setZombieScene(Scene zombieScene) {}

    public Zombie getZombieLogic() {
        return zombie;
    }

    public Scene getZombieScene() {
        return zombieScene;
    }
}
