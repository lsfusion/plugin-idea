// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LSFScriptStatement extends PsiElement {

  @Nullable
  LSFAspectStatement getAspectStatement();

  @Nullable
  LSFClassStatement getClassStatement();

  @Nullable
  LSFConstraintStatement getConstraintStatement();

  @Nullable
  LSFDesignStatement getDesignStatement();

  @Nullable
  LSFEmptyStatement getEmptyStatement();

  @Nullable
  LSFEventStatement getEventStatement();

  @Nullable
  LSFExplicitInterfaceActStatement getExplicitInterfaceActStatement();

  @Nullable
  LSFExplicitInterfacePropertyStatement getExplicitInterfacePropertyStatement();

  @Nullable
  LSFFollowsStatement getFollowsStatement();

  @Nullable
  LSFFormStatement getFormStatement();

  @Nullable
  LSFGlobalEventStatement getGlobalEventStatement();

  @Nullable
  LSFGroupStatement getGroupStatement();

  @Nullable
  LSFIndexStatement getIndexStatement();

  @Nullable
  LSFInternalStatement getInternalStatement();

  @Nullable
  LSFLoggableStatement getLoggableStatement();

  @Nullable
  LSFMetaCodeDeclarationStatement getMetaCodeDeclarationStatement();

  @Nullable
  LSFMetaCodeStatement getMetaCodeStatement();

  @Nullable
  LSFNavigatorStatement getNavigatorStatement();

  @Nullable
  LSFOverrideActionStatement getOverrideActionStatement();

  @Nullable
  LSFOverridePropertyStatement getOverridePropertyStatement();

  @Nullable
  LSFShowDepStatement getShowDepStatement();

  @Nullable
  LSFStubStatement getStubStatement();

  @Nullable
  LSFTableStatement getTableStatement();

  @Nullable
  LSFWindowStatement getWindowStatement();

  @Nullable
  LSFWriteWhenStatement getWriteWhenStatement();

}
