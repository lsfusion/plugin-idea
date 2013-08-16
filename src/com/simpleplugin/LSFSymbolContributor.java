package com.simpleplugin;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.simpleplugin.psi.stubs.types.indexes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LSFSymbolContributor extends LSFNameContributor {

    @Override
    protected Collection<StringStubIndexExtension> getIndices() {
        List<StringStubIndexExtension> indices = new ArrayList<StringStubIndexExtension>();
        indices.add(ClassIndex.getInstance());
        indices.add(ModuleIndex.getInstance());
        indices.add(ExplicitNamespaceIndex.getInstance());
        indices.add(MetaIndex.getInstance());
        indices.add(PropIndex.getInstance());
        indices.add(FormIndex.getInstance());
        indices.add(GroupIndex.getInstance());
        indices.add(TableIndex.getInstance());
        indices.add(WindowIndex.getInstance());
        indices.add(NavigatorElementIndex.getInstance());
        return indices;
    }
}
