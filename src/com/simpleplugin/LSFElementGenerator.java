package com.simpleplugin;

import com.intellij.formatting.FormatTextRanges;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiReference;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.codeStyle.CodeFormatterFacade;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.io.StringRef;
import com.simpleplugin.parser.GeneratedParserUtilBase;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.references.LSFModuleReference;
import com.simpleplugin.psi.references.LSFNamespaceReference;
import com.simpleplugin.psi.references.impl.LSFModuleReferenceImpl;
import com.simpleplugin.psi.references.impl.LSFNamespaceReferenceImpl;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LSFElementGenerator {

    @Nullable
    public static LSFId createIdentifierFromText(Project myProject, String name) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + name + ";");
        for(LSFId child : PsiTreeUtil.findChildrenOfType(dummyFile, LSFId.class))
            if(!child.getText().equals("dummy"))
                return child;
        return null;
    }

    public static PsiFile createDummyFile(Project myProject, String text) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(myProject);
        final String name = "dummy." + LSFFileType.INSTANCE.getDefaultExtension();
        final LightVirtualFile virtualFile = new LightVirtualFile(name, LSFFileType.INSTANCE, text);
        final PsiFile psiFile = ((PsiFileFactoryImpl)factory).trySetupPsiForFile(virtualFile, LSFLanguage.INSTANCE, false, true);
        assert psiFile != null;
        return psiFile;
    }

    private static boolean isWhitespace(char it) {
        return it==' ' || it=='\t';
    }

    public static LSFMetaCodeBody createMetaBodyFromText(final LSFFile file, final String text, String whitespace) {
        final String tab = whitespace.substring(whitespace.lastIndexOf('\n') +1);

        final StringBuilder tabbedText = new StringBuilder();
        tabbedText.append("MODULE x; @dummy() {");
        for (char symbol : text.toCharArray()) {
            tabbedText.append(symbol);
            if (symbol == '\n')
                tabbedText.append(tab);
        }
        tabbedText.append("};");

//        return ApplicationManager.getApplication().runReadAction(new Computable<LSFMetaCodeBody>() {
//            @Override
//            public LSFMetaCodeBody compute() {
                boolean old = ApplicationImpl.setExceptionalThreadWithReadAccessFlag(true);

                Project project = file.getProject();
                final PsiFile dummyFile = createDummyFile(project,  tabbedText.toString());
//                format(myProject, dummyFile);
                Collection<LSFMetaCodeBody> childrenOfType = PsiTreeUtil.findChildrenOfType(dummyFile, LSFMetaCodeBody.class);
        
                if(childrenOfType.isEmpty()) {
                    ApplicationImpl.setExceptionalThreadWithReadAccessFlag(old);
                    return null;
                }
                LSFMetaCodeBody body = childrenOfType.iterator().next();

                List<LSFMetaCodeStatement> recMetaStatements = ((LSFMetaCodeBody)body).getStatements().getMetaCodeStatementList(); 

                ApplicationImpl.setExceptionalThreadWithReadAccessFlag(old);

                MetaChangeDetector.syncUsageProcessing(file, recMetaStatements);
        
                return body;
//            }
//        });
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
}
