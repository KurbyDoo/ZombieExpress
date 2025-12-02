package domain.world;

public class Block {
    private final short id;
    private final String name;

    private final boolean isSolid;
    private final boolean isTransparent;

    public Block(short id, String name, boolean isSolid, boolean isTransparent) {
        this.id = id;
        this.name = name;
        this.isSolid = isSolid;
        this.isTransparent = isTransparent;
    }

    public short getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public boolean isTransparent() {
        return isTransparent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return id == block.id;
    }

    @Override
    public int hashCode() {
        return Short.hashCode(id);
    }
}
