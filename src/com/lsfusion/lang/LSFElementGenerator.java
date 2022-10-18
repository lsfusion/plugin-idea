package com.lsfusion.lang;

import com.intellij.formatting.FormatTextRanges;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.codeStyle.CodeFormatterFacade;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.references.LSFClassReference;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import com.lsfusion.lang.psi.references.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class LSFElementGenerator {
    
    public final static String genName = "mgen123";
    
    public static LSFStringLiteral createStringLiteralFromText(Project myProject, String text) {
        //assert, что text уже обрамлён кавычками -> "some text"
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; FORM someForm FILTERGROUP id FILTER '' func() " + text + ";");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFStringLiteral.class).iterator().next();
    }

    public static PsiElement createFormFromText(Project myProject, String text, Class cls) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; " + text + ";");
        return (PsiElement) PsiTreeUtil.findChildrenOfType(dummyFile, cls).iterator().next();
    }

    public static LSFLocalizedStringValueLiteral createLocalizedStringValueLiteral(Project myProject, String text) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; GROUP someDumbGroup " + text + ";");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFLocalizedStringValueLiteral.class).iterator().next();
    }

    public static LSFMetacodeStringValueLiteral createMetacodeStringValueLiteral(Project myProject, String text) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; META someMetaName(a) END; @someMetaName(" + text + ");");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFMetacodeStringValueLiteral.class).iterator().next();
    }
    
    @Nullable
    public static LSFId createIdentifierFromText(Project myProject, String name) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + name + ";");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFId.class).iterator().next();
    }

    public static LSFCompoundID createCompoundIDFromText(Project myProject, String name) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; f()=" + name + "();");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFCompoundID.class).iterator().next();
    }

    public static PsiComment createPsiCommentFromText(Project myProject, String text) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; //" + text + ";");
        return PsiTreeUtil.findChildrenOfType(dummyFile, PsiComment.class).iterator().next();
    }

    @NotNull
    public static LSFClassName createClassNameFromText(Project myProject, String name) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; f(" + name + " t)=t;");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFClassName.class).iterator().next();
    }

    @NotNull
    public static LSFFormPropertyObject createMappedPropertyFromText(Project myProject, String name) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; DESIGN x { MOVE PROPERTY(" + name + ") {}; }");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFFormPropertyObject.class).iterator().next();
    }

    @NotNull
    public static LSFExplicitPropClassUsage createExplicitClassUsageFromText(Project myProject, String classes) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; f()=g[" + classes + "]();");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFExplicitPropClassUsage.class).iterator().next();
    }

    @NotNull
    public static LSFPropertyDeclParams createPropertyDeclParams(Project myProject, String params) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; f"+params + "=1;");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFPropertyDeclParams.class).iterator().next();
    }

    public static LSFColorLiteral createColorLiteralFromText(Project myProject, String text) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; someProperty = " + text + ";");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFColorLiteral.class).iterator().next();
    }

    public static PsiFile createDummyFile(Project myProject, String text) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(myProject);
        final String name = "internal." + LSFFileType.INSTANCE.getDefaultExtension();
        final LightVirtualFile virtualFile = new LightVirtualFile(name, LSFFileType.INSTANCE, text);
        final PsiFile psiFile = ((PsiFileFactoryImpl) factory).trySetupPsiForFile(virtualFile, LSFLanguage.INSTANCE, false, true);
        assert psiFile != null;
        return psiFile;
    }

    private static boolean isWhitespace(char it) {
        return it == ' ' || it == '\t';
    }

    public static LSFMetaCodeBody createMetaBodyFromText(final LSFFile file, final String text, List<LSFMetaDeclaration> recursionGuard, Set<String> metaDecls) {
        final StringBuilder tabbedText = new StringBuilder();
        tabbedText.append("MODULE " + genName + "; @dummy() {");
        for (char symbol : text.toCharArray()) {
            tabbedText.append(symbol);
        }
        tabbedText.append("};");

        Project project = file.getProject();
        final PsiFile dummyFile = createDummyFile(project, tabbedText.toString());
        LSFMetaCodeBody body = PsiTreeUtil.findChildOfType(dummyFile, LSFMetaCodeBody.class);

        if (body == null) {
            return null;
        }

        List<LSFMetaCodeStatement> recMetaStatements = new ArrayList<>();
        for (LSFLazyMetaStatement metaDeclStatement : body.getLazyMetaStatementList())
            recMetaStatements.addAll(metaDeclStatement.getMetaCodeStatementList());

        MetaChangeDetector.syncUsageProcessing(file, null, null, true, recMetaStatements, recursionGuard, metaDecls);

        return body;
    }

    public static LSFMetaCodeDeclBody createMetaCodeFromText(final Project project, final String text) {
        final PsiFile dummyFile = createDummyFile(project, "MODULE " + genName + "; META dummy(dummy) " + text + " END");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFMetaCodeDeclBody.class).iterator().next();
    }

    private static LSFExprParamDeclaration rowParamDecl = null;
    public static LSFExprParamDeclaration createRowParam(Project project) {
        final PsiFile dummyFile = createDummyFile(project, "MODULE " + genName + "; f(INTEGER row)=1");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFExprParamDeclaration.class).iterator().next();
    }
    
    public static LSFExprParamDeclaration getRowParam(Project project) {
        if(rowParamDecl == null) {
            rowParamDecl = createRowParam(project);
        }
        return rowParamDecl;
    }

    private static List<? extends LSFPropertyDrawDeclaration> builtInFormProps = null;

    public static List<? extends LSFPropertyDrawDeclaration> getBuiltInFormProps(final Project project) {
        if (builtInFormProps == null || builtInFormProps.iterator().next().getProject().isDisposed()) {
            final PsiFile dummyFile = createDummyFile(project, "MODULE lsFusionRulezzz; REQUIRE System; FORM defaultForm PROPERTIES () formEdit,formRefresh,formApply,formCancel,formOk,formClose,formDrop;");
//            final PsiFile dummyFile = createDummyFile(project, "MODULE lsFusionRulezzz; REQUIRE System; FORM defaultForm PROPERTIES () formPrint,formEdit,formXls,formRefresh,formApply,formCancel,formOk,formClose,formDrop;");
            builtInFormProps = PsiTreeUtil.findChildrenOfType(dummyFile, LSFFormPropertiesNamesDeclList.class).iterator().next().getFormPropertyDrawNameDeclList();
        }
        return builtInFormProps;
    }

    private static LSFClassReference staticObjectClassRef = null;
    
    public static LSFClassReference getStaticObjectClassRef(Project project) {
//        return createClassRefFromText("StaticObject", "System", file);
        if (staticObjectClassRef == null || staticObjectClassRef.getProject().isDisposed()) {
            final PsiFile dummyFile = createDummyFile(project, "MODULE lsFusionRulezzz; REQUIRE System; CLASS A : StaticObject;");
            staticObjectClassRef = PsiTreeUtil.findChildrenOfType(dummyFile, LSFClassReference.class).iterator().next();
        }
        return staticObjectClassRef;
    }

    private static Collection<LSFWindowDeclaration> builtInWindows = null;

    public static Collection<LSFWindowDeclaration> getBuiltInWindows(final Project project) {
        if (builtInWindows == null || builtInWindows.iterator().next().getProject().isDisposed()) {
            final PsiFile dummyFile = createDummyFile(project,
                    "MODULE System;" +
                            "WINDOW log 'log' PANEL;" +
                            "WINDOW status 'status' PANEL;" +
                            "WINDOW forms 'forms' PANEL;");
            builtInWindows = PsiTreeUtil.findChildrenOfType(dummyFile, LSFWindowDeclaration.class);
        }
        return builtInWindows;
    }

    public static void format(Project project, PsiElement element) {
        final CodeFormatterFacade codeFormatter = new CodeFormatterFacade(CodeStyleSettingsManager.getSettings(project), LSFLanguage.INSTANCE);
        codeFormatter.processText(element.getContainingFile(), new FormatTextRanges(element.getTextRange(), true), false);
    }

    private final static ASTNode dummy = new CompositePsiElement(LSFTypes.MODULE_USAGE) {

        @Override
        public PsiReference[] getReferences() {
            return PsiReference.EMPTY_ARRAY;
        }

        @Override
        public boolean canNavigateToSource() {
            return false;
        }

        @Override
        public boolean canNavigate() {
            return false;
        }

        @NotNull
        @Override
        public Language getLanguage() {
            return getParent().getLanguage();
        }
    };

    public static LSFModuleReference createModuleRefFromText(final StringRef name, final LSFFile file) {
        return new LSFModuleReferenceImpl(dummy) {
            public LSFId getSimpleName() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getNameRef() {
                return StringRef.toString(name);
            }

            @Override
            public LSFFile getLSFFile() {
                return file;
            }
        };
    }

    public static LSFNamespaceReference createNamespaceRefFromText(final StringRef name, final LSFFile file) {
        return new LSFNamespaceReferenceImpl(dummy) {
            public LSFId getSimpleName() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getNameRef() {
                return StringRef.toString(name);
            }

            @Override
            public LSFFile getLSFFile() {
                return file;
            }
        };
    }

    public static LSFClassReference createClassRefFromText(final String name, final String fname, final LSFFile file) {
        return new LSFClassReferenceImpl(dummy) {
            public LSFCompoundID getCompoundID() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getNameRef() {
                return name;
            }

            @Override
            public String getFullNameRef() {
                return fname;
            }

            @Override
            public Integer getOffsetRef() {
                return null;
            }

            @Override
            public void setFullNameRef(String name, MetaTransaction transaction) {
                throw new UnsupportedOperationException();
            }

            @Override
            public LSFFile getLSFFile() {
                return file;
            }
        };
    }

    public static LSFFile createProjectLSFFile(LSFFile file) {
        final Project project = file.getProject();
        return new LSFFile(file.getViewProvider()) {
            @Override
            public LSFModuleDeclaration getModuleDeclaration() {
                return null;
            }

            @Override
            public GlobalSearchScope getRequireScope() {
                return GlobalSearchScope.allScope(project);
            }
        };
    }

    public static LSFPropReferenceImpl createImplementPropRefFromText(String name, LSFFile file, List<LSFClassSet> classes) {
        return createPropRefFromText(name, null, file, null, classes, true, true);
    }
    
    public static LSFPropReferenceImpl createPropRefFromText(final String name, final String fname, final LSFFile file, final List<LSFClassSet> explicitClasses, final List<LSFClassSet> usageContext, final boolean isImplement, final boolean onlyNotEquals) {
        return new LSFPropReferenceImpl(dummy) {
            @Override
            protected LSFExplicitPropClassUsage getExplicitPropClassUsage() {
                throw new UnsupportedOperationException();
            }

            public LSFCompoundID getCompoundID() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getNameRef() {
                return name;
            }

            @Override
            public String getFullNameRef() {
                return fname;
            }

            @Override
            public Integer getOffsetRef() {
                return null;
            }

            @Override
            public void setFullNameRef(String name, MetaTransaction transaction) {
                throw new UnsupportedOperationException();
            }

            @Nullable
            @Override
            public List<LSFClassSet> getExplicitClasses() {
                return explicitClasses;
            }

            @Nullable
            @Override
            protected List<LSFClassSet> getUsageContext() {
                return usageContext;
            }

            @Override
            public boolean isImplement() {
                return isImplement;
            }

            @Override
            public boolean onlyNotEquals() {
                return onlyNotEquals;
            }

            @Override
            public LSFFile getLSFFile() {
                return file;
            }
        };
    }

    public static LSFMetaReferenceImpl createMetaRefFromText(final String name, final String fname, final LSFFile file, final int paramCount) {
        return new LSFMetaReferenceImpl(dummy) {

            @Override
            public LSFMetaCodeBody getMetaCodeBody() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected LSFMetaCodeStatementHeader getMetaCodeStatementHeader() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected boolean isInline() {
                return false;
            }

            @Override
            public int getParamCount() {
                return paramCount;
            }

            public LSFCompoundID getCompoundID() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getNameRef() {
                return name;
            }

            @Override
            public String getFullNameRef() {
                return fname;
            }

            @Override
            public Integer getOffsetRef() {
                return null;
            }

            @Override
            public void setFullNameRef(String name, MetaTransaction transaction) {
                throw new UnsupportedOperationException();
            }

            @Override
            public LSFFile getLSFFile() {
                return file;
            }
        };
    }

//    @NotNull
//    public static LSFPredefinedFormPropertyName createWindowType(Project myProject, String name) {
//        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; f()=ACTION { FORM xxx " + name + ";};");
//        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFWindowTypeLiteral.class).iterator().next();
//    }
//
}
