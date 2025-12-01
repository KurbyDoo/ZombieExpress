/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Data Transfer Object (DTO): Simple data container for player session info.
 * - Value Object: Represents player session state.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages player session data only.
 * - [PASS] OCP: Simple data class, can be extended.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: No dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] JavaBean conventions for getters/setters.
 * - [WARN] Field 'heightScore' should be 'highScore' (typo).
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [WARN] Possible typo: 'heightScore' should probably be 'highScore'.
 */
package domain.player;

public class PlayerSession {
    private String uid;
    private String email;
    private int lastScore;
    private int heightScore;

    public PlayerSession() {
        this.uid = null;
        this.email = null;
        this.lastScore = 0;
        this.heightScore = 0;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLastScore() {
        return lastScore;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
    }

    public int getHeightScore() {
        return heightScore;
    }

    public void setHeightScore(int heightScore) {
        this.heightScore = heightScore;
    }

    public void clear() {
        this.uid = null;
        this.email = null;
        this.lastScore = 0;
        this.heightScore = 0;
    }
}
