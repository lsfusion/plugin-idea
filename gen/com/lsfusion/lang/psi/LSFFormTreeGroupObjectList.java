// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFFormTreeGroupObjectList extends LSFDocumentation {

  @NotNull
  List<LSFFormTreeGroupObjectDeclaration> getFormTreeGroupObjectDeclarationList();

  @Nullable
  LSFFormTreeGroupObjectOptions getFormTreeGroupObjectOptions();

  @Nullable
  LSFTreeGroupDeclaration getTreeGroupDeclaration();

  String getDocumentation(PsiElement child);

}
