package application.gateways;

import domain.Block;
import java.util.Collection;
import java.util.Optional;

// TODO: Is this class needed?
public interface BlockRepository {
    Optional<Block> findById(short id);
    Optional<Block> findByName(String name);
    Collection<Block> findAll();
}
