package com.lsfusion;

import com.lsfusion.lang.psi.indexes.ClassIndex;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;

import java.util.Collection;
import java.util.Collections;

public class LSFClassContributor extends LSFNameContributor {

    @Override
    protected Collection<LSFStringStubIndex> getIndices() {
        return Collections.singleton(ClassIndex.getInstance());
    }
}
