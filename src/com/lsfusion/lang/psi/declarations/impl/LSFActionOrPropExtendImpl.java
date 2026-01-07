package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.impl.LSFExtendImpl;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.psi.stubs.extend.ExtendStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

// Промежуточный базовый класс для extend-действий/свойств
public abstract class LSFActionOrPropExtendImpl<This extends LSFActionOrPropExtend<This, Stub>, Stub extends ExtendStubElement<This, Stub>>
        extends LSFExtendImpl<This, Stub> implements LSFActionOrPropExtend<This, Stub> {

    protected LSFActionOrPropExtendImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFActionOrPropExtendImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Nullable
    public LSFId getNameIdentifier() {
        return null;
    }

    @Override
    public java.util.Set<LSFDeclaration> resolveDuplicates() {
        return Collections.emptySet();
    }

    // Объединено с утилитным классом extend.impl.LSFActionOrPropExtendImpl: переносим статические методы сюда
    public static List<LSFActionOrPropReference<?, ?>> processActionOrGlobalPropImplementationsSearch(LSFActionOrGlobalPropDeclaration<?, ?> decl) {
        return (List) LSFExtendImpl.processImplementationsSearch(decl);
    }

    @Override
    protected List<java.util.function.Function<This, Collection<? extends LSFDeclaration>>> getDuplicateProcessors() {
        return Collections.emptyList();
    }
}
