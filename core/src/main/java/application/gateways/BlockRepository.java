package application.gateways;

import domain.world.Block;
import java.util.Collection;
import java.util.Optional;

public interface BlockRepository {
    Optional<Block> findById(short id);

    Optional<Block> findByName(String name);

    Collection<Block> findAll();
}
