package data_access;

import application.game_use_cases.ports.BlockRepository;
import domain.Block;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Adapter class following the flyweight factory pattern
public class InMemoryBlockRepository implements BlockRepository {
    private final Map<Short, Block> blocksById = new HashMap<>();
    private final Map<String, Block> blocksByName = new HashMap<>();

    // TODO: We will update this repository to read from json
    public InMemoryBlockRepository() {
        register(new Block((short) 0, "AIR", false, true));
        register(new Block((short) 1, "GRASS", true, false));
        register(new Block((short) 2, "DIRT", true, false));
        register(new Block((short) 3, "STONE", true, false));
    }

    private void register(Block block) {
        blocksById.put(block.getId(), block);
        blocksByName.put(block.getName(), block);
    }

    @Override
    public Optional<Block> findById(short id) {
        return Optional.ofNullable(blocksById.get(id));
    }

    @Override
    public Optional<Block> findByName(String name) {
        return Optional.ofNullable(blocksByName.get(name.toUpperCase()));
    }

    @Override
    public Collection<Block> findAll() {
        return blocksById.values();
    }
}
