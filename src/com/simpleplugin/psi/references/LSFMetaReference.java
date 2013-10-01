package com.simpleplugin.psi.references;

import com.intellij.lang.ASTNode;
import com.simpleplugin.meta.MetaTransaction;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFMetaCodeBody;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;

import java.util.List;

public interface LSFMetaReference extends LSFFullNameReference<LSFMetaDeclaration, LSFMetaDeclaration> {

    List<MetaTransaction.InToken> getUsageParams();

    long getVersion(); // необходимо для синхронизации изменений
    void setVersion(long version);
    void setAnotherFile(LSFFile file);
    
    String getPreceedingTab();
    void setInlinedBody(LSFMetaCodeBody parsed);
    void dropInlinedBody();

    boolean isResolveToVirt(LSFMetaDeclaration virtDecl);
}
