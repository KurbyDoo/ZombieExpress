package infrastructure.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import domain.Block;

import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;

import java.util.HashMap;
import java.util.Map;

public class TexturedBlockMaterialRepository implements BlockMaterialRepository {
    private final Material atlasMaterial;
    private final Map<Short, Map<BlockFace, TextureRegion>> blockRegions = new HashMap<>();
    private final TextureRegion[][] splits;

    public enum BlockFace {
        TOP, BOTTOM, SIDE
    }

    public TexturedBlockMaterialRepository() {
        Texture texture = new Texture(Gdx.files.internal("minecraft_texture_atlas.png"));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.atlasMaterial = new Material();

        this.atlasMaterial.set(PBRTextureAttribute.createBaseColorTexture(texture));
        this.atlasMaterial.set(PBRFloatAttribute.createMetallic(0.0f));
        this.atlasMaterial.set(PBRFloatAttribute.createRoughness(1.0f));

        this.splits = TextureRegion.split(texture, 16, 16);

        // FETCH TEXTURE FROM MAP
//        mapBlock((short)1, getRegion(28, 21), getRegion(28, 5), getRegion(6, 17)); // FAKE GRASS (MOSS)
        mapBlock((short)1, getRegion(9, 17), getRegion(28, 5), getRegion(6, 17));
        mapBlock((short)2, getRegion(28, 5), getRegion(28, 5), getRegion(28, 5));
        mapBlock((short)3, getRegion(1, 0), getRegion(1, 0), getRegion(1, 0));
    }

    private TextureRegion getRegion(int col, int row) {
        if (row >= splits.length || col >= splits[0].length) {
            return splits[0][0];
        }
        return splits[row][col];
    }

    private void mapBlock(short id, TextureRegion top, TextureRegion bottom, TextureRegion side) {
        Map<BlockFace, TextureRegion> faces = new HashMap<>();
        faces.put(BlockFace.TOP, top);
        faces.put(BlockFace.BOTTOM, bottom);
        faces.put(BlockFace.SIDE, side);
        blockRegions.put(id, faces);
    }

    @Override
    public Material getMaterial(Block block) {
        return atlasMaterial;
    }

    public TextureRegion getTextureRegion(Block block, BlockFace face) {
        if (blockRegions.containsKey(block.getId())) {
            return blockRegions.get(block.getId()).getOrDefault(face, blockRegions.get(block.getId()).get(BlockFace.SIDE));
        }
        return splits[0][0];
    }
}
