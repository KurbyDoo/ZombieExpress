package interface_adapter.input;

import application.ports.ApplicationLifecyclePort;
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
