// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import javax.swing.Icon;

public interface LSFWindowStatement extends PsiElement {

  @Nullable
  LSFWindowCreateStatement getWindowCreateStatement();

  @Nullable
  LSFWindowHideStatement getWindowHideStatement();

  @Nullable Icon getIcon(int flags);

}
