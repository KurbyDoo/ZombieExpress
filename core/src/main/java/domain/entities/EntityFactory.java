/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Factory Pattern with Builder: Creates entities using registered strategies.
 * - Builder Pattern: EntityFactoryBuilder provides fluent configuration.
 * - Strategy Pattern: Uses GenerateEntityStrategy for polymorphic entity creation.
 * - Registry Pattern: Maps EntityType to creation strategies.
 *
 * ORCHESTRATOR: EntityFactory is the orchestrator for entity creation in the game.
 * It delegates to specific GenerateEntityStrategy implementations based on EntityType.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [WARN] Domain class imports from application layer (use cases):
 *   - application.game_use_cases.generate_entity.GenerateEntityStrategy
 *   - application.game_use_cases.generate_entity.GenerateEntityInputData
 *   This violates the dependency rule - domain should NOT depend on use cases.
 *   The Strategy interface should be defined in the domain layer.
 *
 * RECOMMENDED FIX:
 *   Move GenerateEntityStrategy interface to domain.entities package.
 *   Move GenerateEntityInputData to domain package as a pure data class.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Factory's single job is entity creation.
 * - [PASS] OCP: Open for new entity types via strategy registration.
 * - [N/A] LSP: No inheritance hierarchy.
 * - [N/A] ISP: No interfaces implemented by this class.
 * - [WARN] DIP: Depends on abstractions (GenerateEntityStrategy) - GOOD, but
 *   the abstraction is in the wrong layer.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class names follow PascalCase.
 * - [WARN] Static mutable field 'idCounter' is not thread-safe.
 * - [PASS] Builder follows standard builder pattern conventions.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Static mutable state (idCounter) could cause issues.
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [PASS] Inner class properly named and structured.
 */
package domain.entities;

import application.game_use_cases.generate_entity.GenerateEntityStrategy;
import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.repositories.EntityStorage;

import java.util.HashMap;
import java.util.Map;

public class EntityFactory {
    private static int idCounter;
    private final Map<EntityType, GenerateEntityStrategy> registry;
    private final EntityStorage storage;

    private EntityFactory(Map<EntityType, GenerateEntityStrategy> registry, EntityStorage storage) {
        idCounter = 0;
        this.registry = registry;
        this.storage = storage;
    }

    public int create(GenerateEntityInputData inputData) {
        // TODO: Should the entity hold id? yes right?
        idCounter++;
        inputData.setId(idCounter);
        Entity e = registry.get(inputData.getType()).execute(inputData);
        storage.setIDEntityPair(e.getID(), e);

        return idCounter;
    }

    public static class EntityFactoryBuilder {
        private final Map<EntityType, GenerateEntityStrategy> registry = new HashMap<>();
        private final EntityStorage storage;

        public EntityFactoryBuilder(EntityStorage storage) {
            this.storage = storage;
        }

        public EntityFactoryBuilder register(EntityType type, GenerateEntityStrategy strategy) {
            registry.put(type, strategy);
            return this;
        }

        public EntityFactory build() {
            return new EntityFactory(registry, storage);
        }
    }
}
