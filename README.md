# GDX Test

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.


File structure (As of nov 30th):
```
src
└── java
    ├── domain
    │   ├── entities
    │   │   ├── Entity.java
    │   │   ├── EntityFactory.java
    │   │   ├── EntityType.java
    │   │   ├── PickupEntity.java
    │   │   ├── Rideable.java
    │   │   ├── Train.java
    │   │   └── Zombie.java
    │   ├── items
    │   │   ├── FuelItem.java
    │   │   ├── Item.java
    │   │   ├── ItemTypes.java
    │   │   ├── MeleeWeapon.java
    │   │   ├── RangedWeapon.java
    │   │   └── Weapon.java
    │   ├── player
    │   │   ├── Inventory.java
    │   │   ├── InventorySlot.java
    │   │   ├── Player.java
    │   │   └── PlayerSession.java
    │   └── world
    │       ├── AmmoType.java
    │       ├── Block.java
    │       ├── Chunk.java
    │       ├── GamePosition.java
    │       ├── Structure.java
    │       └── World.java
    ├── application
    │   ├── account_features
    │   │   ├── login
    │   │   │   ├── LoginDataAccessInterface.java
    │   │   │   ├── LoginInputBoundary.java
    │   │   │   ├── LoginInteractor.java
    │   │   │   ├── LoginOutputBoundary.java
    │   │   │   └── LoginOutputData.java
    │   │   ├── player_data
    │   │   │   ├── LoadPlayerDataInteractor.java
    │   │   │   ├── PlayerDataAccessInterface.java
    │   │   │   └── SavePlayerDataInteractor.java
    │   │   └── register
    │   │       ├── RegisterInputBoundary.java
    │   │       ├── RegisterInteractor.java
    │   │       ├── RegisterOutputBoundary.java
    │   │       ├── RegisterOutputData.java
    │   │       └── RegisterUserDataAccessInterface.java
    │   ├── game_features
    │   │   ├── dismount_entity
    │   │   │   ├── DismountEntityInputBoundary.java
    │   │   │   ├── DismountEntityInputData.java
    │   │   │   ├── DismountEntityInteractor.java
    │   │   │   └── DismountEntityOutputData.java
    │   │   ├── exit_game
    │   │   │   └── ExitGameUseCase.java
    │   │   ├── generate_chunk
    │   │   │   ├── noise
    │   │   │   │   └── PerlinNoise.java
    │   │   │   ├── GenerateChunkInputBoundary.java
    │   │   │   ├── GenerateChunkInputData.java
    │   │   │   ├── GenerateChunkInteractor.java
    │   │   │   └── GenerateChunkOutputData.java
    │   │   ├── generate_entity
    │   │   │   ├── pickup
    │   │   │   │   ├── GeneratePickupInputData.java
    │   │   │   │   └── GeneratePickupStrategy.java
    │   │   │   ├── train
    │   │   │   │   ├── GenerateTrainInputData.java
    │   │   │   │   └── GenerateTrainStrategy.java
    │   │   │   ├── zombie
    │   │   │   │   ├── GenerateZombieInputData.java
    │   │   │   │   └── GenerateZombieStrategy.java
    │   │   │   ├── GenerateEntityInputData.java
    │   │   │   └── GenerateEntityStrategy.java
    │   │   ├── mount_entity
    │   │   │   ├── MountEntityInputBoundary.java
    │   │   │   ├── MountEntityInputData.java
    │   │   │   ├── MountEntityInteractor.java
    │   │   │   └── MountEntityOutputData.java
    │   │   ├── pickup
    │   │   │   └── PickupInteractor.java
    │   │   ├── player_movement
    │   │   │   ├── PlayerMovementInputBoundary.java
    │   │   │   ├── PlayerMovementInputData.java
    │   │   │   └── PlayerMovementInteractor.java
    │   │   ├── populate_chunk
    │   │   │   ├── PopulateChunkInputBoundary.java
    │   │   │   ├── PopulateChunkInputData.java
    │   │   │   └── PopulateChunkInteractor.java
    │   │   ├── render_radius
    │   │   │   ├── RenderRadiusManagerInputBoundary.java
    │   │   │   ├── RenderRadiusManagerInputData.java
    │   │   │   ├── RenderRadiusManagerInteractor.java
    │   │   │   └── RenderRadiusOutputData.java
    │   │   ├── update_entity
    │   │   │   ├── BulletBehaviour.java
    │   │   │   ├── EntityBehaviour.java
    │   │   │   ├── EntityBehaviourInputData.java
    │   │   │   ├── EntityBehaviourSystem.java
    │   │   │   ├── TrainBehaviour.java
    │   │   │   └── ZombieBehaviour.java
    │   │   ├── update_world
    │   │   │   ├── UpdateWorldInputBoundary.java
    │   │   │   ├── UpdateWorldInputData.java
    │   │   │   ├── UpdateWorldInteractor.java
    │   │   │   └── UpdateWorldOutputData.java
    │   │   └── win_condition
    │   │       ├── WinConditionInputBoundary.java
    │   │       ├── WinConditionInteractor.java
    │   │       └── WinConditionOutputData.java
    │   ├── gateways
    │   │   ├── BlockRepository.java
    │   │   └── EntityStorage.java
    │   └── ports
    │       ├── ApplicationLifecyclePort.java
    │       └── PhysicsControlPort.java
    ├── interface_adapter
    │   ├── controllers
    │   │   ├── CameraController.java
    │   │   ├── FirstPersonCameraController.java
    │   │   ├── GameSimulationController.java
    │   │   ├── LoginController.java
    │   │   ├── PickupController.java
    │   │   ├── RegisterController.java
    │   │   └── WorldSyncController.java
    │   ├── input
    │   │   ├── GameInputAdapter.java
    │   │   ├── InventoryInputAdapter.java
    │   │   ├── LibGDXLifecycleAdapter.java
    │   │   └── PickUpInputAdapter.java
    │   ├── physics
    │   │   └── BulletPhysicsAdapter.java
    │   ├── presenters
    │   │   ├── LoginPresenter.java
    │   │   └── RegisterPresenter.java
    │   └── view_models
    │       ├── LoginViewModel.java
    │       └── RegisterViewModel.java
    └── framework
        ├── app
        │   ├── AppInitializer.java
        │   └── Main.java
        ├── data_access
        │   ├── firebase
        │   │   ├── FirebaseInitializer.java
        │   │   ├── FirebaseLoginRegisterDataAccess.java
        │   │   └── FirebasePlayerDataAccess.java
        │   ├── mock_logic
        │   │   ├── login
        │   │   │   ├── MockfirebaseAuthManager.java
        │   │   │   └── MockLoginRegisterDataAccess.java
        │   │   └── player
        │   │       └── MockPlayerDataAccess.java
        │   ├── IdToEntityStorage.java
        │   └── InMemoryBlockRepository.java
        ├── physics
        │   ├── CollisionHandler.java
        │   ├── GameMesh.java
        │   ├── HitBox.java
        │   ├── MeshMotionState.java
        │   └── ObjectContactListener.java
        ├── rendering
        │   ├── strategies
        │   │   ├── GenerateMeshInputData.java
        │   │   ├── GenerateMeshStrategy.java
        │   │   ├── GeneratePickupMeshStrategy.java
        │   │   ├── GenerateTrainMeshStrategy.java
        │   │   └── GenerateZombieMeshStrategy.java
        │   ├── BlockMaterialRepository.java
        │   ├── ChunkMeshData.java
        │   ├── ChunkMeshGenerator.java
        │   ├── ChunkRenderer.java
        │   ├── EntityMeshSynchronizer.java
        │   ├── EntityRenderer.java
        │   ├── IdToMeshStorage.java
        │   ├── MeshFactory.java
        │   ├── MeshStorage.java
        │   ├── ObjectRenderer.java
        │   └── TexturedBlockMaterialRepository.java
        └── view
            ├── assets
            │   ├── ItemImageFactory.java
            │   ├── UIAssetFactory.java
            │   └── UIFontFactory.java
            ├── hud
            │   ├── AmmoHudElement.java
            │   ├── DistanceHudElement.java
            │   ├── FuelHudElement.java
            │   ├── GameHUD.java
            │   ├── HealthHudElement.java
            │   ├── HeldItemHudElement.java
            │   ├── HotbarHudElement.java
            │   ├── HudElement.java
            │   ├── PickupPromptHudElement.java
            │   └── TimeHudElement.java
            ├── GameScreen.java
            ├── GameView.java
            ├── LoginView.java
            ├── RegisterView.java
            ├── ViewCamera.java
            ├── ViewFactory.java
            ├── ViewManager.java
            ├── ViewType.java
            └── Viewable.java
```

## Firebase Authentication

The login/register system uses Firebase.
To implement these two functions, we use two different keys, because register and login use different Firebase APIs

### 1. Admin SDK Key (Register + Firestore)
We create user using Firebase Admin SDK, and we load/save player data from Firestore
So everyone must download their own "serviceAccountKey.json"

Step:
1. go to Firebase -> Project Setting -> Service Account
2. Click “Generate new private key”
3. Download the JSON file
4. Store in `core/src/main/resources/serviceAccountKey.json`
   **plz put it in git.ignore(do not commit this file to GitHub)**

after that the Admin SDK will automatically initialize using:
```
FirebaseInitializer.init()
```

### 2. Web API key(Login via REST API)
User login uses Firebase's REST endpoint

Step:
1. Firebase -> Project Setting -> General
2. Copy the "Web API Key"
3. IntelliJ->Run->Edit Configurations->Environment Variables
4. set FIREBASE_WEB_API_KEY=**your_key_here**

---
After above set up steps, the things below should work correctly:
- register a new user (Admin SDK)
- log in an existing user (REST API)
- load/save player scores from Firestore
