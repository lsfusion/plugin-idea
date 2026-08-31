package com.lsfusion;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;

/**
 * Lifetime of this plugin's project-scoped registrations. Disposed when the project closes and, crucially,
 * when the plugin is unloaded — anything parented here is detached instead of pinning the plugin's class
 * loader and forcing IDEA to restart in order to apply an update.
 */
@Service(Service.Level.PROJECT)
public final class LSFProjectDisposable implements Disposable {
    @Override
    public void dispose() {
    }
}
