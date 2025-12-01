package framework.view.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UIAssetFactory {

    private UIAssetFactory() {}

    public static Drawable createSlotDrawable(Color borderColor) {
        int size = 16;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();

        pixmap.setColor(borderColor);
        int thickness = 1;
        pixmap.fillRectangle(0, 0, size, thickness);
        pixmap.fillRectangle(0, size - thickness, size, thickness);
        pixmap.fillRectangle(0, 0, thickness, size);
        pixmap.fillRectangle(size - thickness, 0, thickness, size);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public static Drawable createBarDrawable(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public static Drawable createBorderDrawable(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0));
        pixmap.fill();

        pixmap.setColor(Color.BLACK);
        int thickness = 3;
        pixmap.fillRectangle(0, height - thickness, width, thickness);      // top
        pixmap.fillRectangle(0, 0, width, thickness);                        // bottom
        pixmap.fillRectangle(0, 0, thickness, height);                       // left
        pixmap.fillRectangle(width - thickness, 0, thickness, height);       // right

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
