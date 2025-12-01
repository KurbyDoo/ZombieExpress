package framework.view.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import domain.player.InventorySlot;
import domain.player.Player;
import framework.view.assets.UIAssetFactory;

public class HotbarHudElement implements HudElement {

    private final Player player;
    private final Container<Label>[] hotbarSlots;
    private final Label[] hotbarLabels;
    private final Drawable slotNormalDrawable;
    private final Drawable slotSelectedDrawable;

    @SuppressWarnings("unchecked")
    public HotbarHudElement(Stage stage, Label.LabelStyle style, Player player) {
        this.player = player;

        slotNormalDrawable = UIAssetFactory.createSlotDrawable(Color.LIGHT_GRAY);
        slotSelectedDrawable = UIAssetFactory.createSlotDrawable(Color.WHITE);

        Table hotbarTable = new Table();
        int HOTBAR_SIZE = 10;
        int SLOT_SIZE = 100;

        hotbarSlots = new Container[HOTBAR_SIZE];
        hotbarLabels = new Label[HOTBAR_SIZE];

        for (int i = 0; i < HOTBAR_SIZE; i++) {
            Label label = new Label("", style);
            label.setAlignment(Align.center);

            Container<Label> slotContainer = new Container<>(label);
            slotContainer.width(SLOT_SIZE).height(SLOT_SIZE);
            slotContainer.background(slotNormalDrawable);

            hotbarTable.add(slotContainer);
            hotbarSlots[i] = slotContainer;
            hotbarLabels[i] = label;
        }

        hotbarTable.pack();
        float worldWidth = stage.getViewport().getWorldWidth();
        float x = (worldWidth - hotbarTable.getWidth()) / 2;
        float y = 0;
        hotbarTable.setPosition(x, y);

        stage.addActor(hotbarTable);
    }

    @Override
    public void update(float deltaTime) {
        int selected = player.getCurrentSlot();

        for (int i = 0; i < hotbarSlots.length; i++) {
            if (i == selected) {
                hotbarSlots[i].background(slotSelectedDrawable);
            } else {
                hotbarSlots[i].background(slotNormalDrawable);
            }

            InventorySlot slot = player.getInventory().getSlot(i);

            if (slot == null || slot.isEmpty()) {
                hotbarLabels[i].setText("");
            } else {
                String baseName = slot.getItem().getName();
                String labelText;
                if (slot.getItem().isStackable()) {
                    labelText = baseName + " x" + slot.getQuantity();
                } else {
                    labelText = baseName;
                }
                hotbarLabels[i].setText(breakIntoLinesByWords(labelText));
            }
        }
    }

    private String breakIntoLinesByWords(String text) {
        if (text == null || text.isEmpty()) return "";
        String[] words = text.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            sb.append(words[i]);
            if (i < words.length - 1) sb.append("\n");
        }
        return sb.toString();
    }
}
