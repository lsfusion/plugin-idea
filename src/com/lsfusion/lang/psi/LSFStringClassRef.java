package com.lsfusion.lang.psi;

import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFActionOrGlobalPropDeclarationImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// для stub'ов чтобы избегать ненужного parse'инга
public class LSFStringClassRef {

    public final String namespace; // nullable
    public final String name; // not null
    public final boolean isBuiltIn;

    public LSFStringClassRef(DataClass dataClass) {
        this(dataClass.getName());
    }

    public LSFStringClassRef(@NotNull String builtInName) {
        this(null, true, builtInName);
    }

    public LSFStringClassRef(String namespace, @NotNull String name) {
        this(namespace, false, name);
    }

    public LSFStringClassRef(String namespace, boolean isBuiltIn, String name) {
        this.namespace = namespace;
        this.isBuiltIn = isBuiltIn;
        this.name = name;
    }

    public void serialize(@NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(namespace);
        dataStream.writeBoolean(isBuiltIn);
        dataStream.writeName(name);
    }

    public static LSFStringClassRef deserialize(@NotNull StubInputStream dataStream) throws IOException {
        StringRef namespace = dataStream.readName();
        boolean isBuiltIn = dataStream.readBoolean();
        StringRef name = dataStream.readName();
        return new LSFStringClassRef(namespace != null ? namespace.getString() : null, isBuiltIn, name != null ? name.getString() : null);
    }

    public LSFClassDeclaration resolveDecl(LSFFile file) {
        assert !isBuiltIn;
        return LSFElementGenerator.createClassRefFromText(name, namespace, file).resolveDecl();
    }

    public LSFClassSet resolve(LSFFile file) {
        if(isBuiltIn)
            return LSFPsiImplUtil.resolveDataClass(name);
        LSFClassDeclaration classDecl = resolveDecl(file);
        return classDecl != null ? new CustomClassSet(classDecl) : null;
    }

    public static List<LSFClassSet> resolve(List<LSFStringClassRef> classes, LSFFile file) {
        List<LSFClassSet> result = new ArrayList<>();
        for(LSFStringClassRef classRef : classes)
            result.add(classRef != null ? classRef.resolve(file) : null);
        return result;
    }

    public static List<LSFClassDeclaration> resolveDecls(List<LSFStringClassRef> classes, LSFFile file) {
        List<LSFClassDeclaration> result = new ArrayList<>();
        for(LSFStringClassRef classRef : classes) {
            LSFClassDeclaration decl = classRef.resolveDecl(file);
            if(decl != null)
                result.add(decl);
        }
        return result;
    }

    public static String getParamPresentableText(List<LSFStringClassRef> classes) {
        List<String> result = new ArrayList<>();
        for(LSFStringClassRef classRef : classes) {
            result.add(classRef != null ? classRef.name : null);
        }
        return LSFActionOrGlobalPropDeclarationImpl.getParamPresentableText(result);
    }
}
