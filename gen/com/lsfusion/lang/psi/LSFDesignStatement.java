// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.context.FormContext;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.extend.DesignStubElement;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import javax.swing.Icon;

public interface LSFDesignStatement extends LSFDesign, FormContext, StubBasedPsiElement<DesignStubElement> {

  @Nullable
  LSFComponentBody getComponentBody();

  @NotNull
  LSFDesignHeader getDesignHeader();

  @Nullable Icon getIcon(int flags);

  @Nullable LSFFormDeclaration resolveFormDecl();

  String getDocumentation(PsiElement child);

}
