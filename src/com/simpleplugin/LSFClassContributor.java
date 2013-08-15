package com.simpleplugin;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.simpleplugin.psi.stubs.types.indexes.ClassIndex;

import java.util.Collection;
import java.util.Collections;

public class LSFClassContributor extends LSFNameContributor {

    @Override
    protected Collection<StringStubIndexExtension> getIndices() {
        return Collections.<StringStubIndexExtension>singleton(ClassIndex.getInstance());
    }
}
