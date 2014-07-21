package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import org.jetbrains.annotations.NotNull;

//по-хорошему здесь нужно использовать хитрый индекс, где ключ - массив строк, но тогда get() иногда возвращает PsiElement'ы другого типа 
//public class TableClassesIndex extends StringArrayStubIndexExtension<LSFTableDeclaration> {
public class TableClassesIndex extends StringStubIndexExtension<LSFTableDeclaration> {

    private static final TableClassesIndex ourInstance = new TableClassesIndex();

    public static TableClassesIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFTableDeclaration> getKey() {
        return LSFIndexKeys.TABLE_CLASSES;
    }
}

