/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Value Object Pattern: Immutable object representing a block type with identity based on ID.
 * - Part of Flyweight Pattern: Used with InMemoryBlockRepository to share Block instances.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers (use cases, interface adapters, frameworks).
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity with no side effects.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - represents block data only.
 * - [PASS] OCP: Open for extension via subclassing if needed.
 * - [N/A] LSP: No inheritance hierarchy.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No dependencies to invert.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Fields are private and final (immutable).
 * - [PASS] Getters follow JavaBean naming convention.
 * - [MINOR] Missing Javadoc documentation for public methods.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Single-line methods on lines 17-20 could be formatted with braces on separate lines.
 * - [MINOR] Missing class-level Javadoc comment.
 */
package domain;

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

    public short getId() { return id; }
    public String getName() { return name; }
    public boolean isSolid() { return isSolid; }
    public boolean isTransparent() { return isTransparent; }

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
