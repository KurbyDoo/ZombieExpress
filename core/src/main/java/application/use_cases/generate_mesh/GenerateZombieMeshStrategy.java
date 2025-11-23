package application.use_cases.generate_mesh;

import com.badlogic.gdx.Gdx;
import domain.entities.Entity;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import physics.GameMesh;
import physics.HitBox;

public class GenerateZombieMeshStrategy implements GenerateMeshStrategy {
    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/model.gltf"));

    @Override
    public GameMesh execute(GenerateMeshInputData inputData) {
        Scene zombieScene = new Scene(zombieAsset.scene);
        Entity entity = inputData.getEntity();
        zombieScene.modelInstance.transform.setToTranslation(entity.getPosition());

        HitBox hitBox = new HitBox(String.valueOf(inputData.getId()), HitBox.ShapeTypes.BOX, 1, 1, 1);
        GameMesh mesh = hitBox.construct();
//        mesh.setScene(zombieScene);
        return mesh;
    }
}
