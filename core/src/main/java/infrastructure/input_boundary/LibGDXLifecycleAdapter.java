/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Infrastructure)
 *
 * DESIGN PATTERNS:
 * - Adapter Pattern: Implements ApplicationLifecyclePort interface.
 * - Adapts LibGDX lifecycle to use case abstraction.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] This is the correct layer for LibGDX code.
 * - [PASS] Implements port interface from use cases layer.
 * - [PASS] Framework dependencies are appropriate for this layer.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - lifecycle adaptation.
 * - [PASS] DIP: Implements ApplicationLifecyclePort abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package infrastructure.input_boundary;

import application.game_use_cases.ports.ApplicationLifecyclePort;
import com.badlogic.gdx.Gdx;

/**
 * Infrastructure Adapter that implements the ApplicationLifecyclePort for the LibGDX framework.
 */
public class LibGDXLifecycleAdapter implements ApplicationLifecyclePort {
    @Override
    public void closeApplication() {
        Gdx.app.exit();
    }
}
