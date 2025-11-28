# SOLID Principle and Clean Architecture Violations

This document identifies SOLID Principle and Clean Architecture violations in the ZombieExpress codebase, along with brief remediation suggestions for each.

---

## Clean Architecture Violations

### 1. Domain Layer Depends on LibGDX Framework (`com.badlogic.gdx.math.Vector3`)

**Files Affected:**
- `domain/Chunk.java` - imports `com.badlogic.gdx.math.Vector3`
- `domain/World.java` - imports `com.badlogic.gdx.math.Vector3`
- `domain/Structure.java` - imports `com.badlogic.gdx.math.Vector3`
- `domain/entities/Entity.java` - imports `com.badlogic.gdx.math.Vector3`
- `domain/entities/Zombie.java` - imports `com.badlogic.gdx.math.Vector3`
- `domain/entities/Train.java` - imports `com.badlogic.gdx.math.Vector3`
- `domain/entities/WorldPickup.java` - imports `com.badlogic.gdx.math.Vector3`
- `domain/player/Player.java` - imports `com.badlogic.gdx.math.Vector3`

**Violation:** According to Clean Architecture Chapter 11, the Domain layer must be strictly independent and should NEVER import framework-specific classes like `com.badlogic.gdx.*`.

**Remediation:** Create a framework-agnostic `Position3D` or `Vector3D` class in the domain layer that holds x, y, z coordinates. Use Interface Adapters to convert between the domain `Vector3D` and LibGDX's `Vector3` at the boundaries.

---

### 2. Domain Layer Depends on Use Case Layer

**File Affected:**
- `domain/entities/EntityFactory.java`

**Violation:** The `EntityFactory` class in the Domain layer imports:
- `application.use_cases.generate_entity.GenerateEntityStrategy`
- `application.use_cases.generate_entity.GenerateEntityInputData`
- `data_access.EntityStorage`

This violates the Dependency Rule - Domain should NOT depend on Use Cases or Data Access layers.

**Remediation:** Move `EntityFactory` to the Use Case layer or create an abstract factory interface in the Domain layer with the concrete implementation in the Use Case layer.

---

### 3. Domain Layer Depends on Data Access Layer

**File Affected:**
- `domain/entities/IdToEntityStorage.java`

**Violation:** The `IdToEntityStorage` class in the Domain layer implements `data_access.EntityStorage` interface, creating an improper dependency from Domain to Data Access.

**Remediation:** Move `IdToEntityStorage` to the `data_access` package where it belongs as an implementation of the `EntityStorage` interface.

---

### 4. Use Case Layer Depends on Infrastructure Layer

**File Affected:**
- `application/use_cases/generate_chunk/GenerateChunkInteractor.java`

**Violation:** The `GenerateChunkInteractor` imports `infrastructure.noise.PerlinNoise`, which is an infrastructure concern. Use Cases should not depend on Infrastructure.

**Remediation:** Create a `NoiseGenerator` interface (port) in the Use Case layer and have `PerlinNoise` implement it in the Infrastructure layer. Inject the interface into the interactor.

---

### 5. Use Case Layer Depends on Framework (`com.badlogic.gdx`)

**Files Affected:**
- `application/use_cases/generate_entity/GenerateEntityInputData.java` - imports `com.badlogic.gdx.math.Vector3`
- `application/use_cases/generate_chunk/GenerateChunkInteractor.java` - imports `com.badlogic.gdx.math.Vector3`
- `application/use_cases/player_movement/PlayerMovementInteractor.java` - imports `com.badlogic.gdx.math.Vector3`
- `application/use_cases/pickup/PickupInteractor.java` - imports `com.badlogic.gdx.math.Vector3`
- `application/use_cases/update_entity/ZombieBehaviour.java` - imports `com.badlogic.gdx.math.Vector3`
- `application/use_cases/update_entity/EntityBehaviourSystem.java` - imports `com.badlogic.gdx.math.Vector3`
- `application/use_cases/ports/PhysicsControlPort.java` - imports `com.badlogic.gdx.math.Vector3`

**Violation:** Use Cases should contain pure business logic and should not depend on framework-specific classes.

**Remediation:** Create domain-level position/vector abstractions and use them throughout the Use Case layer. Interface Adapters should handle the conversion between domain types and LibGDX types.

---

### 6. Data Access Layer Accesses Concrete Use Case Classes

**File Affected:**
- `data_access/EntityStorage.java` (Interface is fine, but placement may be incorrect)

**Violation:** The `EntityStorage` interface should be defined in the Use Case layer as a port (Data Access Interface), not in the Data Access layer.

**Remediation:** Move `EntityStorage` interface to `application/use_cases/ports/` package to properly define it as a port that the Use Case layer depends on, with implementations in Data Access.

---

## SOLID Principle Violations

### 7. Single Responsibility Principle (SRP) - GameView Class

**File Affected:**
- `presentation/view/GameView.java`

**Violation:** The `GameView` class has too many responsibilities:
- Initializing player and camera
- Setting up input adapters
- Creating repositories
- Initializing entity systems
- Managing physics/collision
- Creating mesh factories
- Managing world sync
- Managing pickups
- Creating and managing HUD
- Rendering everything

**Remediation:** Extract responsibilities into separate classes:
- Create a `GameInitializer` class for setup logic
- Create a `PickupManager` class for pickup-related operations
- Use dependency injection to provide pre-configured components

---

### 8. Single Responsibility Principle (SRP) - Player Class

**File Affected:**
- `domain/player/Player.java`

**Violation:** The `Player` class handles multiple responsibilities:
- Position and movement (`updatePosition`, `updateRotation`)
- Health management (`takeDamage`, `heal`, `isDead`, `updatePassiveHealing`)
- Ammo management (`addAmmo`, `consumeAmmo`, `pistolAmmo`, `rifleAmmo`)
- Inventory management (`pickUp`, `drop`, `currentSlot`)

**Remediation:** Extract separate classes:
- `PlayerHealth` for health-related logic
- `PlayerAmmo` or `AmmoInventory` for ammunition management
- Keep `Player` as a composition of these components

---

### 9. Single Responsibility Principle (SRP) - EntityBehaviourSystem

**File Affected:**
- `application/use_cases/update_entity/EntityBehaviourSystem.java`

**Violation:** This class handles:
- Behavior registration/initialization
- Entity position caching
- Behavior updates
- Chunk entity tracking/unloading

**Remediation:** Separate caching logic into a dedicated `EntityPositionCache` class. Consider using a separate `BehaviorRegistry` for registering behaviors.

---

### 10. Open/Closed Principle (OCP) - Player Ammo Management

**File Affected:**
- `domain/player/Player.java`

**Violation:** The `addAmmo` and `consumeAmmo` methods use switch statements on `AmmoType`. Adding a new ammo type requires modifying the `Player` class.

```java
// From domain/player/Player.java
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
```

**Remediation:** Use a `Map<AmmoType, Integer>` to store ammo counts, making the class open for extension (new ammo types) but closed for modification.

---

### 11. Open/Closed Principle (OCP) - Block Height Determination

**File Affected:**
- `application/use_cases/generate_chunk/GenerateChunkInteractor.java`

**Violation:** The `getBlockByHeight` method uses if-else chains to determine block types. Adding new terrain types requires modifying this method.

**Remediation:** Use the Strategy pattern with a `TerrainGenerationStrategy` interface, or use configuration-driven block selection.

---

### 12. Liskov Substitution Principle (LSP) - Entity setID Method

**File Affected:**
- `domain/entities/Entity.java`

**Violation:** The `setID` method has an empty implementation, which could lead to unexpected behavior for callers.

```java
// From domain/entities/Entity.java
public class Entity {
    protected Integer id;
    // ... other fields
    
    public void setID(Integer id) {} // Empty implementation - does nothing!
}
```

**Remediation:** Either implement the method properly or throw `UnsupportedOperationException` if ID should be immutable. Consider removing the method if IDs are set only in the constructor.

---

### 13. Interface Segregation Principle (ISP) - BlockRepository

**File Affected:**
- `application/use_cases/ports/BlockRepository.java`

**Violation:** Clients that only need to find blocks by name are forced to depend on `findById` and `findAll` methods they may not use.

**Remediation:** Consider splitting into smaller interfaces like `BlockReader` (for lookups) and `BlockLister` (for getting all blocks) if clients have different needs.

---

### 14. Dependency Inversion Principle (DIP) - GameView Creates Concrete Dependencies

**File Affected:**
- `presentation/view/GameView.java`

**Violation:** `GameView` directly instantiates concrete classes instead of depending on abstractions:
- `new InMemoryBlockRepository()`
- `new TexturedBlockMaterialRepository()`
- `new CollisionHandler()`
- `new IdToEntityStorage()`
- `new GenerateChunkInteractor(...)`

**Remediation:** Use dependency injection - pass interfaces through the constructor or use a dependency injection container/factory.

---

### 15. Dependency Inversion Principle (DIP) - EntityBehaviourSystem Directly Creates Behaviors

**File Affected:**
- `application/use_cases/update_entity/EntityBehaviourSystem.java`

**Violation:** The `initializeBehaviors()` method directly instantiates `ZombieBehaviour` and `BulletBehaviour`.

**Remediation:** Accept a `Map<EntityType, EntityBehaviour>` through the constructor (dependency injection) or use a factory/registry that's injected.

---

### 16. Dependency Inversion Principle (DIP) - LoginPresenter Calls Interactor

**File Affected:**
- `interface_adapter/login/LoginPresenter.java`

**Violation:** The `LoginPresenter` depends on and calls `LoadPlayerDataInteractor` directly. Presenters should only format output data, not trigger additional use cases.

**Remediation:** Move the player data loading logic to the `LoginInteractor` or create a composite use case that handles both login and data loading.

---

### 17. Dependency Inversion Principle (DIP) - BehaviourContext Uses Public Fields

**File Affected:**
- `application/use_cases/update_entity/BehaviourContext.java`

**Violation:** While not strictly a DIP violation, using public fields (`public final PhysicsControlPort physics`) instead of interfaces with proper encapsulation can lead to tight coupling.

**Remediation:** Use getter methods to access fields, allowing for easier substitution and testing.

---

## Summary

| Category | Count |
|----------|-------|
| Clean Architecture Violations | 6 |
| SRP Violations | 3 |
| OCP Violations | 2 |
| LSP Violations | 1 |
| ISP Violations | 1 |
| DIP Violations | 4 |
| **Total** | **17** |

### Priority Recommendations

1. **High Priority:** Remove LibGDX dependencies from Domain layer by creating framework-agnostic value objects
2. **High Priority:** Move `EntityFactory` and `IdToEntityStorage` to appropriate layers
3. **Medium Priority:** Apply dependency injection in `GameView` to improve testability
4. **Medium Priority:** Refactor `Player` class to follow SRP
5. **Low Priority:** Create noise generator interface for terrain generation
