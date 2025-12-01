package framework.view.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class UIFontFactory {

    private UIFontFactory() {
    }

    public static Label.LabelStyle createMainHudStyle() {
        BitmapFont font = createFont(32);
        return new Label.LabelStyle(font, Color.WHITE);
    }

    public static Label.LabelStyle createLargeHudStyle() {
        BitmapFont font = createFont(40);
        return new Label.LabelStyle(font, Color.WHITE);
    }

    private static BitmapFont createFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = Color.WHITE;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;

        BitmapFont font = generator.generateFont(param);
        generator.dispose();
        return font;
    }
}
