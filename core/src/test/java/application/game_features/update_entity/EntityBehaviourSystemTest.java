package application.game_features.update_entity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import application.gateways.EntityStorage;
import domain.entities.Entity;
import domain.entities.EntityType;
import domain.world.Chunk;
import domain.world.GamePosition;
import domain.world.World;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class EntityBehaviourSystemTest {

    private final int ENTITY_ID = 1;
    private EntityBehaviourSystem system;
    private AutoCloseable closeable;
    @Mock
    private EntityStorage storage;
    @Mock
    private World world;
    @Mock
    private EntityBehaviour mockStrategy;
    @Mock
    private Entity mockEntity;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Setup basic Entity Mocks
        when(storage.getEntityByID(ENTITY_ID)).thenReturn(mockEntity);
        when(mockEntity.getPosition()).thenReturn(new GamePosition(0, 0, 0));
        when(mockEntity.getType()).thenReturn(EntityType.ZOMBIE); // Assume ZOMBIE for general test
    }

    private void assertNotNull(Object o) {
        if (o == null) throw new AssertionError("Object should not be null");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Nested
    @DisplayName("Initialization and Factory")
    class FactoryTests {
        @Test
        @DisplayName("Factory should register behaviours and build system")
        void shouldBuildSystem() {
            EntityBehaviourSystem builtSystem = new EntityBehaviourSystem.EntityBehaviourSystemFactory(storage, world)
                .register(EntityType.ZOMBIE, mockStrategy)
                .build();

            assertNotNull(builtSystem);
        }
    }

    @Nested
    @DisplayName("Update Loop")
    class UpdateLoopTests {

        @BeforeEach
        void setupSystem() {
            Map<EntityType, EntityBehaviour> behaviors = new HashMap<>();
            behaviors.put(EntityType.ZOMBIE, mockStrategy);
            system = new EntityBehaviourSystem(behaviors, storage, world);
        }

        @Test
        @DisplayName("Should execute strategy if entity type is registered")
        void shouldExecuteStrategy() {
            List<Integer> activeEntities = Collections.singletonList(ENTITY_ID);

            system.update(activeEntities, 1f);

            verify(mockStrategy, times(1)).execute(any());
        }

        @Test
        @DisplayName("Should ignore entities with no registered strategy")
        void shouldIgnoreUnknownTypes() {
            when(mockEntity.getType()).thenReturn(EntityType.CHUNK); // Not registered in map
            List<Integer> activeEntities = Collections.singletonList(ENTITY_ID);

            system.update(activeEntities, 1f);

            verify(mockStrategy, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("Chunk Management (Cache Logic)")
    class ChunkManagementTests {
        private AutoCloseable nestedCloseable;

        @Mock
        private Chunk chunkA;
        @Mock
        private Chunk chunkB;
        @Mock
        private Set<Integer> chunkAEntities;
        @Mock
        private Set<Integer> chunkBEntities;

        @BeforeEach
        void setupSystem() {
            nestedCloseable = MockitoAnnotations.openMocks(this);
            system = new EntityBehaviourSystem(new HashMap<>(), storage, world);

            // Setup Chunk sets
            when(chunkA.getEntityIds()).thenReturn(chunkAEntities);
            when(chunkB.getEntityIds()).thenReturn(chunkBEntities);
        }

        @Test
        @DisplayName("Should move entity ID from old chunk to new chunk when position changes")
        void shouldUpdateChunksOnMovement() {
            GamePosition startPos = new GamePosition(0, 0, 0);
            GamePosition endPos = new GamePosition(100, 0, 0);

            when(mockEntity.getPosition()).thenReturn(startPos);

            system.updateCache(Collections.singletonList(ENTITY_ID));

            when(mockEntity.getPosition()).thenReturn(endPos);

            when(world.getChunkFromWorldPos(refEq(startPos))).thenReturn(chunkA);
            when(world.getChunkFromWorldPos(refEq(endPos))).thenReturn(chunkB);

            system.unloadCache(Collections.singletonList(ENTITY_ID));

            verify(chunkAEntities).remove(ENTITY_ID);
            verify(chunkBEntities).add(ENTITY_ID);
        }

        @Test
        @DisplayName("Should do nothing if entity remains in the same chunk")
        void shouldNotChangeChunksIfSame() {
            GamePosition startPos = new GamePosition(0, 0, 0);
            when(mockEntity.getPosition()).thenReturn(startPos);

            system.updateCache(Collections.singletonList(ENTITY_ID));

            when(world.getChunkFromWorldPos(any())).thenReturn(chunkA);

            system.unloadCache(Collections.singletonList(ENTITY_ID));

            verify(chunkAEntities, never()).remove(any());
            verify(chunkBEntities, never()).add(any());
        }

        @AfterEach
        void tearDown() throws Exception {
            nestedCloseable.close();
        }
    }
}
