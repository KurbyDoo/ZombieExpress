package infrastructure.input_boundary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import domain.entities.Player;

public class UIInputAdapter {
    private final Player player;

    public UIInputAdapter(Player player) {
        this.player = player;
    }

    public void pollInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            player.setCurrentSlot(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            player.setCurrentSlot(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            player.setCurrentSlot(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            player.setCurrentSlot(3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            player.setCurrentSlot(4);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            player.setCurrentSlot(5);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            player.setCurrentSlot(6);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            player.setCurrentSlot(7);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            player.setCurrentSlot(8);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            player.setCurrentSlot(9);
        }
    }
}
