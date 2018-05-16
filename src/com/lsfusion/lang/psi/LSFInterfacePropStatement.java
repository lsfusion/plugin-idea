package com.lsfusion.lang.psi;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;

import javax.swing.*;
import java.util.List;

// класс для того чтобы использовать Light версии statement'ов (то есть statement'ов где информацию можно достать без resolve'га, соответственно parse'инг не нужен)
// !!!! соответственно все имплементации по возможности должны все из stub'ов доставать !!!!! 
public interface LSFInterfacePropStatement extends PsiElement {

    List<LSFClassSet> resolveParamClasses();
    
    boolean isNoParams();

    String getName();

    LSFFile getLSFFile();

    String getParamPresentableText();

    String getValuePresentableText();

    PsiElement getLookupObject(); // пока не совсем понятно зачем
    
    boolean isAction();

//    LSFPropertyStatement getPropertyStatement(); // вызывать только если парсинг не критичен

    Icon getIcon();
}
