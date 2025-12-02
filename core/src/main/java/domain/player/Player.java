package domain.player;

import domain.GamePosition;
import domain.AmmoType;
import domain.entities.Rideable;
import domain.items.Item;
import domain.items.ItemTypes;

public class Player {
    private final float ROTATION_SPEED = 0.2f;
    private final float MAX_HEALTH = 100;

    private final GamePosition startingPosition;
    private final GamePosition position;
    private final GamePosition direction;
    private final GamePosition up;

    private final Inventory inventory;
    private int currentSlot = 0;
    private int pistolAmmo = 250;
    private int rifleAmmo = 100;
    private int score = 0;
    private float currentHealth = MAX_HEALTH;

    private float totalTime = 0f;

    private Rideable currentRide;

    public Player(GamePosition startingPosition) {
        this.startingPosition = new GamePosition(startingPosition);
        this.position = new GamePosition(startingPosition);
        this.direction = new GamePosition(1, 0, 0);
        this.up = new GamePosition(GamePosition.Y);
        this.inventory = new Inventory();

//        this.currentRide = new Train(-1, this.position);

        inventory.addItem(ItemTypes.RUSTY_PISTOL);
        inventory.addItem(ItemTypes.COAL);
        inventory.addItem(ItemTypes.COAL);
    }

    /**
     * Updates the player's position based on a velocity vector.
     * @param velocity The direction of movement (should be normalized).
     */
    public void updatePosition(GamePosition velocity) {
        position.add(velocity);
    }

    /**
     * Updates the player's viewing direction based on mouse input.
     * The Y delta is now pre-inverted to be intuitive (mouse up = look up).
     * @param deltaX The change in mouse X position.
     * @param deltaY The change in mouse Y position.
     */
    public void updateRotation(float deltaX, float deltaY) {
        // Yaw (left/right) - Rotate around the world UP vector.
        direction.rotate(up, -deltaX * ROTATION_SPEED);

        float maxPitchRadians = (float) Math.toRadians(89.0f);
        float newPitchRadians = (float) Math.asin(direction.y) + (float) Math.toRadians(deltaY * ROTATION_SPEED);
        float clampedPitchRadians = Math.max(-maxPitchRadians, Math.min(maxPitchRadians, newPitchRadians));
        float actualRotationRadians = clampedPitchRadians - (float) Math.asin(direction.y);

        if (Math.abs(actualRotationRadians) > 0.0001f) {
            GamePosition pitchAxis = new GamePosition(direction).crs(up).nor();
            direction.rotate(pitchAxis, (float) Math.toDegrees(actualRotationRadians));
        }
    }

    public GamePosition getStartingPosition() {
        return new GamePosition(startingPosition);
    }

    public GamePosition getPosition() {
        return new GamePosition(position);
    }

    public GamePosition getDirection() {
        return new GamePosition(direction);
    }

    public GamePosition getUp() {
        return new GamePosition(up);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public void setCurrentRide(Rideable currentRide) {
        this.currentRide = currentRide;
    }

    public Rideable getCurrentRide() {
        return currentRide;
    }

    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    public int getPistolAmmo() {
        return pistolAmmo;
    }

    public int getRifleAmmo() {
        return rifleAmmo;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(float health){this.currentHealth = health;}

    public void takeDamage(int amount) {
        currentHealth -= amount;
    }

    public int getScore() {
        GamePosition start = getStartingPosition();
        GamePosition current = getPosition();

        float distance = Math.max(0, (int)(current.x - start.x));
        float time = getTotalTime();
        float score = (distance/time) * 100f;
        return (int)score;

    }

    public void resetScore() {
        this.score = 0;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public void addAmmo(AmmoType type, int amount) {
        switch (type) {
            case PISTOL:
                pistolAmmo += amount;
                break;

            case RIFLE:
                rifleAmmo += amount;
                break;
        }
    }

    public boolean consumeAmmo(AmmoType type) {
        switch (type) {
            case PISTOL:
                if (pistolAmmo <= 0) return false;
                pistolAmmo -= 1;
                return true;

            case RIFLE:
                if (rifleAmmo <= 0) return false;
                rifleAmmo -= 1;
                return true;
            default:
                return false;
        }
    }
    public void addTime(float delta){
        totalTime += delta;
    }

    public float getTotalTime() {
        return Math.max(totalTime, 0.1f);
    }

    /**
     * Set the current selected inventory slot.
     * @param index The index of the selected inventory slot.
     */
    public void setCurrentSlot(int index) {
        if (index >= 0 && index < inventory.getSize()) {
            this.currentSlot = index;
        }
    }

    /**
     * Picks up the specified item and adds it to the player's inventory.
     * Do nothing if inventory is full.
     *
     * @param item The item to be picked up.
     * @return
     */
    public boolean pickUp(Item item) {
        return inventory.addItem(item);
    }

    /**
     * Drops an item from the inventory slot that the player is holding.
     * Do nothing if slot is empty.
     */
    public void drop() {
        inventory.removeItem(currentSlot);
    }

    public void setPosition(GamePosition newPlayerPos) {
        position.set(newPlayerPos);
    }
}
