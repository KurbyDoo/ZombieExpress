/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/Views)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package presentation.view;

public interface Viewable {
    void createView();
    void renderView();
    void disposeView();
}
