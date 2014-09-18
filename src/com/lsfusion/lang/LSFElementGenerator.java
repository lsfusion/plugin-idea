package com.lsfusion.lang;

import com.intellij.formatting.FormatTextRanges;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiReference;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.codeStyle.CodeFormatterFacade;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFComponentDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.declarations.LSFWindowDeclaration;
import com.lsfusion.lang.psi.references.LSFClassReference;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import com.lsfusion.lang.psi.references.impl.LSFClassReferenceImpl;
import com.lsfusion.lang.psi.references.impl.LSFModuleReferenceImpl;
import com.lsfusion.lang.psi.references.impl.LSFNamespaceReferenceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LSFElementGenerator {

    public final static String genName = "mgen123";

    public static LSFStringLiteral createStringLiteralFromText(Project myProject, String text) {
        //assert, что text уже обрамлён кавычками -> "some text"
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; GROUP someDumbGroup " + text + ";");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFStringLiteral.class).iterator().next();
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

    @NotNull
    public static LSFClassName createClassNameFromText(Project myProject, String name) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; f(" + name + " t)=t;");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFClassName.class).iterator().next();
    }

    @NotNull
    public static LSFFormPropertyObject createMappedPropertyFromText(Project myProject, String name) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; DESIGN x { ADD PROPERTY(" + name + ") {}; }");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFFormPropertyObject.class).iterator().next();
    }

    @NotNull
    public static LSFExplicitPropClassUsage createExplicitClassUsageFromText(Project myProject, String classes) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + genName + "; f()=g[" + classes + "]();");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFExplicitPropClassUsage.class).iterator().next();
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

    public static LSFMetaCodeBody createMetaBodyFromText(final LSFFile file, final String text) {
        final StringBuilder tabbedText = new StringBuilder();
        tabbedText.append("MODULE " + genName + "; @dummy() {");
        for (char symbol : text.toCharArray()) {
            tabbedText.append(symbol);
        }
        tabbedText.append("};");

//        return ApplicationManager.getApplication().runReadAction(new Computable<LSFMetaCodeBody>() {
//            @Override
//            public LSFMetaCodeBody compute() {
        boolean old = ApplicationImpl.setExceptionalThreadWithReadAccessFlag(true);

        Project project = file.getProject();
        final PsiFile dummyFile = createDummyFile(project, tabbedText.toString());
//                format(myProject, dummyFile);
        Collection<LSFMetaCodeBody> childrenOfType = PsiTreeUtil.findChildrenOfType(dummyFile, LSFMetaCodeBody.class);

        if (childrenOfType.isEmpty()) {
            ApplicationImpl.setExceptionalThreadWithReadAccessFlag(old);
            return null;
        }
        LSFMetaCodeBody body = childrenOfType.iterator().next();

        List<LSFMetaCodeStatement> recMetaStatements = ((LSFMetaCodeBody) body).getMetaCodeStatementList();

        ApplicationImpl.setExceptionalThreadWithReadAccessFlag(old);

        MetaChangeDetector.syncUsageProcessing(file, recMetaStatements);

        return body;
//            }
//        });
    }

    public static LSFAnyTokens createMetaCodeFromText(final Project project, final String text) {
        final PsiFile dummyFile = createDummyFile(project, "MODULE " + genName + "; META dummy() " + text + " END");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFAnyTokens.class).iterator().next();
    }

    private static List<? extends LSFPropertyDrawDeclaration> builtInFormProps = null;

    public static List<? extends LSFPropertyDrawDeclaration> getBuiltInFormProps(final Project project) {
        if (builtInFormProps == null) {
            final PsiFile dummyFile = createDummyFile(project, "MODULE lsFusionRulezzz; REQUIRE System; FORM defaultForm PROPERTIES () formPrint,formEdit,formXls,formRefresh,formApply,formCancel,formOk,formClose,formDrop;");
            builtInFormProps = PsiTreeUtil.findChildrenOfType(dummyFile, LSFFormPropertiesNamesDeclList.class).iterator().next().getFormPropertyDrawNameDeclList();
        }
        return builtInFormProps;
    }

    private static List<? extends LSFComponentDeclaration> builtInFormComponents = null;

    public static List<? extends LSFComponentDeclaration> getBuiltInFormComponents(Project project) {
        if (builtInFormComponents == null) {
            builtInFormComponents = createFormComponents(project, Arrays.asList("main", "functions.box", "leftControls", "rightControls", "nogroup.panel", "nogroup.panel.props"));
        }
        return builtInFormComponents;
    }
    
    public static List<? extends LSFComponentDeclaration> createFormComponents(Project project, List<String> componentsNames) {
        String text = "MODULE lsFusionT; REQUIRE System; FORM defaultForm PROPERTIES () formPrint,formEdit,formXls,formRefresh,formApply,formCancel,formOk,formClose,formDrop;" +
                "DESIGN defaultForm FROM DEFAULT {";
        for (String name : componentsNames) {
            text += "NEW " + name + ";";
        }
        text += "}";
        final PsiFile dummyFile = createDummyFile(project, text);
        return new ArrayList<LSFComponentDeclaration>(PsiTreeUtil.findChildrenOfType(dummyFile, LSFComponentDeclaration.class));    
    }

    private static Collection<? extends LSFWindowDeclaration> builtInWindows = null;

    public static Collection<? extends LSFWindowDeclaration> getBuiltInWindows(final Project project) {
        if (builtInWindows == null) {
            final PsiFile dummyFile = createDummyFile(project,
                    "MODULE System;" +
                            "WINDOW PANEL log 'log';" +
                            "WINDOW PANEL status 'status';" +
                            "WINDOW PANEL forms 'forms';");
            builtInWindows = PsiTreeUtil.findChildrenOfType(dummyFile, LSFWindowDeclaration.class);
        }
        return builtInWindows;
    }

    public static void format(Project project, PsiElement element) {
        final CodeFormatterFacade codeFormatter = new CodeFormatterFacade(CodeStyleSettingsManager.getSettings(project));
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
            public void setFullNameRef(String name, MetaTransaction transaction) {
                throw new UnsupportedOperationException();
            }

            @Override
            public LSFFile getLSFFile() {
                return file;
            }
        };
    }

}
