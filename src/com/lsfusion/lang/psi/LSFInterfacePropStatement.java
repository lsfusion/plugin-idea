package com.lsfusion.lang.psi;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;

import javax.swing.*;
import java.util.List;

// класс для того чтобы использовать Stub'ы и не парсить лишний раз
public interface LSFInterfacePropStatement extends PsiElement {

    List<LSFClassSet> resolveParamClasses();

    String getName();

    LSFFile getLSFFile();

    String getParamPresentableText();

    String getValuePresentableText();

    PsiElement getLookupObject(); // пока не совсем понятно зачем
    
    boolean isAction();

//    LSFPropertyStatement getPropertyStatement(); // вызывать только если парсинг не критичен

    Icon getIcon();
}
