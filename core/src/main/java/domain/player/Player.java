package domain.player;

import com.badlogic.gdx.math.Vector3;
import domain.items.Item;
import static domain.entities.AmmoType.*;

public class Player {
    private final Vector3 startingPosition;
    private final Vector3 position;
    private final Vector3 direction;
    private final Vector3 up;

    private final float movementSpeed = 1.0f;
    private final float sprintSpeed = movementSpeed * 50;
    private final float rotationSpeed = 0.2f;

    private final Inventory inventory;
    private int currentSlot = 0;
    private int pistolAmmo = 0;
    private int rifleAmmo = 0;

    private final int maxHealth = 100;
    private int currentHealth = maxHealth;
    private float passiveHealTimer = 0;

    public Player(Vector3 startingPosition) {
        this.startingPosition = new Vector3(startingPosition);
        this.position = new Vector3(startingPosition);
        this.direction = new Vector3(1, 0, 0);
        this.up = new Vector3(Vector3.Y);
        this.inventory = new Inventory();

//        inventory.addItem(BASEBALL_BAT);
//        inventory.addItem(RUSTY_PISTOL);
//        inventory.addItem(COAL, 2);
        addAmmo(PISTOL, 10);
    }

    /**
     * Updates the player's position based on a velocity vector.
     * @param velocity The direction of movement (should be normalized).
     * @param deltaTime The time since the last frame.
     * @param isSprinting Whether the player is sprinting.
     */
    public void updatePosition(Vector3 velocity, float deltaTime, boolean isSprinting) {
        float speed = isSprinting ? sprintSpeed : movementSpeed;
        position.add(velocity.nor().scl(speed * deltaTime));
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
            Vector3 pitchAxis = new Vector3(direction).crs(up).nor();
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

    public Vector3 getStartingPosition() {
        return new Vector3(startingPosition);
    }

    public Vector3 getPosition() {
        return new Vector3(position);
    }

    public Vector3 getDirection() {
        return new Vector3(direction);
    }

    public Vector3 getUp() {
        return new Vector3(up);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getCurrentSlot() {
        return currentSlot;
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

    public void heal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
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
     * @param item The item to be picked up.
     */
    public void pickUp(Item item) {
        inventory.addItem(item);
    }

    /**
     * Drops an item from the inventory slot that the player is holding.
     * Do nothing if slot is empty.
     */
    public void drop() {
        inventory.removeItem(currentSlot);
    }
}
