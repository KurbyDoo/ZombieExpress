package framework.view.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import domain.items.Item;
import java.util.HashMap;
import java.util.Map;

public class ItemIconFactory {

    private static final Map<String, Drawable> image = new HashMap<>();

    static {
        Texture coalTexture = new Texture(Gdx.files.internal("models/items/fuels/coal/icons/coal.png"));
        Texture woodLogTexture = new Texture(Gdx.files.internal("models/items/fuels/wood_log/icons/wood_log.png"));
        Texture oilBarrelTexture =
            new Texture(Gdx.files.internal("models/items/fuels/oil_barrel/icons/oil_barrel.png"));
        Texture rustyPistolTexture =
            new Texture(Gdx.files.internal("models/items/weapons/pistols/icons/rusty_pistol.png"));
        Texture metalPistolTexture =
            new Texture(Gdx.files.internal("models/items/weapons/pistols/icons/combat_pistol.png"));
        Texture goldPistolTexture =
            new Texture(Gdx.files.internal("models/items/weapons/pistols/icons/golden_pistol.png"));
        Texture rainbowPistolTexture =
            new Texture(Gdx.files.internal("models/items/weapons/pistols/icons/rainbow_pistol.png"));
        Texture blackRifleTexture =
            new Texture(Gdx.files.internal("models/items/weapons/rifles/icons/tactical_rifle.png"));
        Texture goldRifleTexture =
            new Texture(Gdx.files.internal("models/items/weapons/rifles/icons/golden_rifle.png"));
        Texture redRifleTexture =
            new Texture(Gdx.files.internal("models/items/weapons/rifles/icons/zombie_obliterator_rifle.png"));
        image.put("Coal", new TextureRegionDrawable(new TextureRegion(coalTexture)));
        image.put("Wood Log", new TextureRegionDrawable(new TextureRegion(woodLogTexture)));
        image.put("Oil Barrel", new TextureRegionDrawable(new TextureRegion(oilBarrelTexture)));
        image.put("Rusty Pistol", new TextureRegionDrawable(new TextureRegion(rustyPistolTexture)));
        image.put("Combat Pistol", new TextureRegionDrawable(new TextureRegion(metalPistolTexture)));
        image.put("Golden Pistol", new TextureRegionDrawable(new TextureRegion(goldPistolTexture)));
        image.put("Rainbow Pistol", new TextureRegionDrawable(new TextureRegion(rainbowPistolTexture)));
        image.put("Tactical Rifle", new TextureRegionDrawable(new TextureRegion(blackRifleTexture)));
        image.put("Golden Rifle", new TextureRegionDrawable(new TextureRegion(goldRifleTexture)));
        image.put("Zombie Obliterator Rifle", new TextureRegionDrawable(new TextureRegion(redRifleTexture)));
    }

    private ItemIconFactory() {
    }

    public static Drawable getIconForItem(Item item) {
        if (item == null) return null;
        return image.get(item.getName());
    }
}
