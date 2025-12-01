/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Boundary Pattern: Defines contract for chunk generation use case.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines chunk generation contract.
 * - [PASS] ISP: Focused interface with single method.
 * - [PASS] DIP: Higher-level modules depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing interface-level Javadoc.
 */
package application.game_use_cases.generate_chunk;

public interface GenerateChunkInputBoundary {
    GenerateChunkOutputData execute(GenerateChunkInputData inputData);
}
