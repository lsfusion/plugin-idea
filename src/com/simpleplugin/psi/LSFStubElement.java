package com.simpleplugin.psi;

import com.intellij.psi.stubs.StubElement;

public interface LSFStubElement<T extends LSFElement> extends StubElement<T> {

    public boolean isCorrect();

}
