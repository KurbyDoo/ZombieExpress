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


File structure (As of nov 14th):
```
java
├── domain
│   ├── entity
│   │   ├── BlockType.java
│   │   ├── Chunk.java
│   │   ├── FuelItem.java
│   │   ├── Player.java
│   │   ├── Weapon.java
│   │   └── World.java
│
├── usecases
│   ├── ports
│   │   ├── BlockMaterialRepository.java
│   │   ├── BlockRepository.java
│   ├── chunkgeneration
│   │   ├── ChunkGenerationInputBoundary.java
│   │   ├── ChunkGenerationInputData.java
│   │   └── ChunkGenerationInteractor.java
│   ├── chunkmeshgeneration
│   │   ├── ChunkMeshGenerationInputBoundary.java
│   │   ├── ChunkMeshGenerationInputData.java
│   │   ├── ChunkMeshGenerationInteractor.java
│   │   └── ChunkMeshGenerationOutputData.java
│   ├── login
│   │   ├── LoginDataAccessInterface.java
│   │   ├── LoginInputBoundary.java
│   │   ├── LoginInteractor.java
│   │   └── LoginOutputBoundary.java
│   └── playermovement
│       ├── PlayerMovementInputBoundary.java
│       ├── PlayerMovementInputData.java
│       └── PlayerMovementInteractor.java
│
├── interfaceadapters
│   ├── controllers
│   │   ├── CameraController.java
│   │   ├── FirstPersonCameraController.java
│   │   └── WorldGenerationController.java
│   ├── presenters
│   │   └── LoginPresenter.java
│   ├── viewmodels
│   │   └── LoginViewModel.java
│   └── input
│       └── GameInputAdapter.java
│
└── frameworksanddrivers
    ├── main
    │   └── Main.java
    ├── config
    │   └── GameConfig.java
    ├── dataaccess
    │   ├── InMemoryBlockRepository.java
    │   └── FirebaseAuthManager.java
    ├── gdx
    │   ├── view
    │   │   ├── GameView.java
    │   │   ├── LoginView.java
    │   │   ├── Viewable.java
    │   │   ├── ViewManager.java
    │   │   └── ViewCamera.java
    │   ├── rendering
    │   │   ├── LibGDXMaterialRepository.java
    │   │   ├── ChunkLoader.java
    │   │   ├── GameMeshBuilder.java
    │   │   └── ObjectRenderer.java
    │   └── noise
    │       └── PerlinNoise.java
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
