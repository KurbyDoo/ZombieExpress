/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Data Pattern: Encapsulates chunk generation output.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds generated chunk.
 * - [PASS] Immutable data class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Constructor has package-private visibility - should be public or documented.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Constructor visibility should match class visibility.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.generate_chunk;

import domain.Chunk;

public class GenerateChunkOutputData {
    private final Chunk newChunk;
    GenerateChunkOutputData(Chunk chunk) {
        newChunk = chunk;
    }

    public Chunk getChunk() {
        return newChunk;
    }
}
