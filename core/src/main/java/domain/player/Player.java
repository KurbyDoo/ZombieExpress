package domain.player;

import domain.GamePosition;
import domain.AmmoType;
import domain.entities.Rideable;
import domain.items.Item;
import domain.AmmoType.*;
import domain.items.ItemTypes;
import domain.items.ItemTypes.*;

public class Player {
    private final GamePosition startingPosition;
    private final GamePosition position;
    private final GamePosition direction;
    private final GamePosition up;

    private final float movementSpeed = 25.0f;
    private final float sprintSpeed = movementSpeed * 5;
    private final float rotationSpeed = 0.2f;

    private final Inventory inventory;
    private int currentSlot = 0;
    private int pistolAmmo = 0;
    private int rifleAmmo = 0;
    private int score = 0;

    private final int maxHealth = 100;
    private int currentHealth = maxHealth;
    private float passiveHealTimer = 0;

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
        addAmmo(AmmoType.PISTOL, 250);
        addAmmo(AmmoType.RIFLE, 100);
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
        direction.rotate(up, -deltaX * rotationSpeed);

        float maxPitchRadians = (float) Math.toRadians(89.0f);
        float newPitchRadians = (float) Math.asin(direction.y) + (float) Math.toRadians(deltaY * rotationSpeed);
        float clampedPitchRadians = Math.max(-maxPitchRadians, Math.min(maxPitchRadians, newPitchRadians));
        float actualRotationRadians = clampedPitchRadians - (float) Math.asin(direction.y);

        if (Math.abs(actualRotationRadians) > 0.0001f) {
            GamePosition pitchAxis = new GamePosition(direction).crs(up).nor();
            direction.rotate(pitchAxis, (float) Math.toDegrees(actualRotationRadians));
        }
    }

    public void updatePassiveHealing(float deltaTime) {
        if (isDead() || currentHealth >= maxHealth) return;
        passiveHealTimer += deltaTime;
        float healInterval = 1; // heal every 1 second
        int healAmount = 2;        // heal 2 HP each interval

        if (passiveHealTimer >= healInterval) {
            int intervals = (int) (passiveHealTimer / healInterval);
            passiveHealTimer -= intervals * healInterval;

            heal(healAmount * intervals);
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

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getPistolAmmo() {
        return pistolAmmo;
    }

    public int getRifleAmmo() {
        return rifleAmmo;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void takeDamage(int amount) {
        currentHealth -= amount;
    }

    public int getScore() {
        GamePosition start = getStartingPosition();
        GamePosition current = getPosition();

        int distance = Math.max(0, (int)(current.x - start.x));

        return distance * 10;
    }

    public void resetScore() {
        this.score = 0;
    }


    public void heal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public float getMovementSpeed() {
        return movementSpeed;
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

    public boolean consumeAmmo(AmmoType type, int amount) {
        if (amount <= 0) return false;

        switch (type) {
            case PISTOL:
                if (pistolAmmo < amount) return false;
                pistolAmmo -= amount;
                return true;

            case RIFLE:
                if (rifleAmmo < amount) return false;
                rifleAmmo -= amount;
                return true;
            default:
                return false;
        }
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
