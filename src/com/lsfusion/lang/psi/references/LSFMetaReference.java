package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFMetaCodeBody;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;

import java.util.List;

public interface LSFMetaReference extends LSFFullNameReference<LSFMetaDeclaration, LSFMetaDeclaration> {

    List<MetaTransaction.InToken> getUsageParams();

    long getVersion(); // необходимо для синхронизации изменений
    void setVersion(long version);
    void setAnotherFile(LSFFile file);
    
    void setInlinedBody(LSFMetaCodeBody parsed);
    void dropInlinedBody();

    boolean isResolveToVirt(LSFMetaDeclaration virtDecl);
}
