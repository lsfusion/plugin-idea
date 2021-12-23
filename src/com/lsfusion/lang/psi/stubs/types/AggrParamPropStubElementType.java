package com.lsfusion.lang.psi.stubs.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFAggrParamGlobalPropDeclaration;
import com.lsfusion.lang.psi.impl.LSFAggrParamPropDeclareImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.AggrParamPropStubElement;
import com.lsfusion.lang.psi.stubs.impl.AggrParamPropStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AggrParamPropStubElementType extends PropStubElementType<AggrParamPropStubElement, LSFAggrParamGlobalPropDeclaration> {

    public AggrParamPropStubElementType() {
        super("AGGRPARAM_PROP");
    }

    @Override
    public LSFAggrParamGlobalPropDeclaration createPsi(@NotNull AggrParamPropStubElement stub) {
        return new LSFAggrParamPropDeclareImpl(stub, this);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement psi = node.getPsi();
        if(((LSFAggrParamGlobalPropDeclaration)psi).getAggrPropertyDefinition() != null)
            return true;
        return false;
    }

    @NotNull
    @Override
    public AggrParamPropStubElement createStub(@NotNull LSFAggrParamGlobalPropDeclaration psi, StubElement parentStub) {
        return new AggrParamPropStubImpl(parentStub, psi);
    }

    @Override
    @NotNull
    public AggrParamPropStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new AggrParamPropStubImpl(dataStream, parentStub, this);
    }

    @Override
    public void indexStub(@NotNull AggrParamPropStubElement stub, @NotNull IndexSink sink) {
        super.indexStub(stub, sink);

        LSFExplicitClasses paramExplicitClasses = stub.getParamExplicitClasses();
        if(paramExplicitClasses != null) {
            Set<String> set = new HashSet<>(); // избегаем повторного добавления при многократном вхождении класса
            for (String paramClass : paramExplicitClasses.getIndexedClasses()) {
                if (paramClass != null && set.add(paramClass))
                    sink.occurrence(LSFIndexKeys.EXPLICIT_INTERFACE_PROP, paramClass); // Psi должен наследовать от ExplicitInterfaceProp
            }
        }
        
        Set<String> valueClasses = stub.getParamExplicitValues();
        if(valueClasses != null) {
            for (String valueClass : valueClasses) {
                if (valueClass != null) {
                    sink.occurrence(LSFIndexKeys.EXPLICIT_VALUE, valueClass); // Psi должен наследоваться от ExplicitValueProp
                }
            }
        }
    }
}
