package com.lsfusion.psi.references;

import com.lsfusion.meta.MetaTransaction;
import com.lsfusion.psi.LSFFile;
import com.lsfusion.psi.LSFMetaCodeBody;
import com.lsfusion.psi.declarations.LSFMetaDeclaration;

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
