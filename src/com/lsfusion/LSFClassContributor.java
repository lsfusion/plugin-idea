package com.lsfusion;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.lsfusion.lang.psi.indexes.ClassIndex;

import java.util.Collection;
import java.util.Collections;

public class LSFClassContributor extends LSFNameContributor {

    @Override
    protected Collection<StringStubIndexExtension> getIndices() {
        return Collections.<StringStubIndexExtension>singleton(ClassIndex.getInstance());
    }
}
