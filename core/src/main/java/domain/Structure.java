/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Entity Pattern: Represents a structure in the game world.
 * - Composition Pattern: Contains lists of spawn positions.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [CRITICAL VIOLATION] Imports com.badlogic.gdx.math.Vector3 in domain layer.
 *   Domain entities MUST NOT depend on framework-specific types.
 *   Should use GamePosition instead of Vector3.
 *
 * RECOMMENDED FIX:
 *   Replace Vector3 with GamePosition throughout:
 *   - private GamePosition position;
 *   - private List<GamePosition> entitySpawnPositions;
 *   - private List<GamePosition> spawnPositions;
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages structure positioning data.
 * - [WARN] OCP: Class is incomplete (TODO comment indicates unfinished design).
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [FAIL] DIP: Depends on concrete LibGDX Vector3 class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [WARN] Constructor parameters shadow instance fields incorrectly.
 *   Line 15-16: 'entitySpawnPositions = entitySpawnPositions' assigns parameter to itself.
 * - [WARN] Missing getters/setters for private fields (class is unusable).
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Self-assignment bug on lines 15-16.
 * - [WARN] Unused import java.util.List if not properly used.
 * - [MINOR] Missing class-level Javadoc.
 */
package domain;

import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class Structure {
    // TODO: What does the structure object hold?
    private Vector3 position;
    private List<Vector3> entitySpawnPositions;
    private List<Vector3> spawnPositions;

    public Structure(Vector3 position, List<Vector3> entitySpawnPositions, List<Vector3> spawnPositions) {
        this.position = position;
        entitySpawnPositions = entitySpawnPositions;
        spawnPositions = spawnPositions;
    }
}
