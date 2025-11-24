package presentation.view.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import domain.items.Item;

import java.util.HashMap;
import java.util.Map;

public class ItemImageFactory {

    private static final Map<String, Drawable> image = new HashMap<>();

    static {
        Texture coalTex = new Texture(Gdx.files.internal("models/items/fuels/coal/coal.png"));
        Texture woodLogTex = new Texture(Gdx.files.internal("models/items/fuels/wood_log/wood_log.png"));
        Texture oilBarrelTex = new Texture(Gdx.files.internal("models/items/fuels/oil_barrel/oil_barrel.png"));
        image.put("Coal", new TextureRegionDrawable(new TextureRegion(coalTex)));
        image.put("Wood Log", new TextureRegionDrawable(new TextureRegion(woodLogTex)));
        image.put("Oil Barrel", new TextureRegionDrawable(new TextureRegion(oilBarrelTex)));
    }

    private ItemImageFactory() {}

    public static Drawable getImageForItem(Item item) {
        if (item == null) return null;
        return image.get(item.getName());
    }
}
