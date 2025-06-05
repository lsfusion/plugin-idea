package com.lsfusion.dependencies;

import com.intellij.openapi.Disposable;

import javax.swing.*;

public abstract class DiagramView extends JPanel implements Disposable {
    public abstract void redraw();
}
