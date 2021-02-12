package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.properties.BundleNameEvaluator;
import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.lang.properties.PropertiesReferenceManager;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.lang.properties.references.PropertyReference;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.util.LSFFileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

import static com.lsfusion.lang.LSFElementGenerator.createLocalizedStringValueLiteral;

public class LSFLocalizedStringValueLiteralImpl extends ASTWrapperPsiElement implements LSFLocalizedStringValueLiteral {
    public LSFLocalizedStringValueLiteralImpl(@NotNull ASTNode node) {
        super(node);
        initPropertyFiles();
    }

    public void accept(@NotNull LSFVisitor visitor) {
        visitor.visitLocalizedStringValueLiteral(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
        else super.accept(visitor);
    }

    private synchronized void initPropertyFiles() {
        if (propertyFiles == null) {
            propertyFiles = new HashSet<>();
            Pattern pattern = Pattern.compile("[^/]*ResourceBundle\\.properties");
            GlobalSearchScope scope = LSFFileUtils.getModuleWithDependenciesScope(this);
            for (VirtualFile file : FileTypeIndex.getFiles(PropertiesFileType.INSTANCE, scope)) {
                if (pattern.matcher(file.getName()).matches()) {
                    propertyFiles.add(file);
                }
            }
        }
    }
    
    private static Set<VirtualFile> propertyFiles = null;
    
    @Override
    public PsiReference getReference() {
        PsiReference[] refs = getReferences();
        return refs.length == 0 ? null : refs[0];
    }

    private List<PsiReference> findAllRefs(String literal) {
        final String escapedCharacters = "\\'nrt{}";
        boolean insideKey = false;
        int startPos = 0;
        if (literal != null) {
            List<PsiReference> refs = new ArrayList<>();
            for (int i = 1; i+1 < literal.length(); ++i) {
                if (literal.charAt(i) == '\\') {
                    if (i+2 == literal.length()) {
                        break;
                    }
    
                    char nextCh = literal.charAt(i+1);
                    if (escapedCharacters.indexOf(nextCh) == -1 || insideKey && (nextCh == '\r' || nextCh == '\n' || nextCh == '\t')) {
                        break;
                    }
                    ++i;                    
                } else if (literal.charAt(i) == '{') {
                    if (insideKey) {
                        break;
                    } else {
                        insideKey = true;
                        startPos = i;
                    }
                } else if (literal.charAt(i) == '}') {
                    if (!insideKey || i - startPos == 1) {
                        break;
                    } else {
                        refs.add(new PropertyFileItemMultiReference(this, new TextRange(startPos, i+1)));
                        insideKey = false;
                    }
                } else if (insideKey && literal.charAt(i) == ' ') break; 
            }
            return refs;
        }
        return new ArrayList<>();
    }

    public static String getValue(PsiElement element, boolean needToBeLocalized) {
        String text = element.getText();
//        if(!needToBeLocalized)
        return removeEscapeSymbolsAndQuotes(text);
    }

    @Override
    public String getValue() {
        return getValue(this, needToBeLocalized());
    }

    @Override
    public boolean needToBeLocalized() {
        String text = getText();
        for (int i = 1; i + 1 < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch == '{' || ch == '}') {
                return true;
            } else if (ch == '\\') {
                ++i;
            }
        }
        return false;
    }
    
    private static String removeEscapeSymbolsAndQuotes(String s) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char cur = s.charAt(i);
            if (cur == '\\' && i+1 < s.length()) { // removing escape
                char next = s.charAt(i+1);
                if (next == '\\' || next == '{' || next == '}' || next == '\'') {
                    builder.append(next);
                    ++i;
                    continue;
                }
            }
            if(cur != '\'') // removing quote
                builder.append(cur);
        }
        return builder.toString();
    }
    
    @NotNull
    @Override 
    public PsiReference[] getReferences() {
        String literal = getText();
        List<PsiReference> refs = findAllRefs(literal);
        return refs.toArray(new PsiReference[0]);
    }
    
    public class PropertyFileItemMultiReference extends PsiPolyVariantReferenceBase {
        public PropertyFileItemMultiReference(PsiElement element, TextRange range) {
            super(element, range);
        }
        
        private class AllScopePropertyReference extends PropertyReference {
            public AllScopePropertyReference(@NotNull String key, @NotNull PsiElement element, @Nullable String bundleName, boolean soft, TextRange range) {
                super(key, element, bundleName, soft, range);
            }

            @Override    
            protected List<PropertiesFile> retrievePropertyFilesByBundleName(String resourceBundleName, PsiElement element) {
                // based on the com.intellij.lang.properties.references.I18nUtil.propertiesFilesByBundleName method
                if (resourceBundleName != null) {
                    PsiFile containingFile = element.getContainingFile();
                    PsiElement containingFileContext = InjectedLanguageManager.getInstance(containingFile.getProject()).getInjectionHost(containingFile);
                    if (containingFileContext != null) containingFile = containingFileContext.getContainingFile();

                    VirtualFile virtualFile = containingFile.getVirtualFile();
                    if (virtualFile == null) {
                        virtualFile = containingFile.getOriginalFile().getVirtualFile();
                    }

                    if (virtualFile != null) {
                        Project project = containingFile.getProject();
                        PropertiesReferenceManager refManager = PropertiesReferenceManager.getInstance(project);
                        return refManager.findPropertiesFiles(LSFFileUtils.getModuleWithDependenciesScope(getElement()), resourceBundleName, BundleNameEvaluator.DEFAULT);
                    }
                }
                return Collections.emptyList();
            }
        } 
        
        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            Set<ResolveResult> results = new HashSet<>();
            String key = getReferenceId();
            for (VirtualFile file : propertyFiles) {
                try {
                    PropertyReference ref = new AllScopePropertyReference(key, LSFLocalizedStringValueLiteralImpl.this, file.getNameWithoutExtension(), true, getRangeInElement());
                    ResolveResult[] result = ref.multiResolve(incompleteCode);
                    Collections.addAll(results, result);
                } catch (ProcessCanceledException ignored) {
                } 
            }
            return results.toArray(new ResolveResult[0]);
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            return getReferenceId();
        }

        private String getReferenceId() {
            return LSFLocalizedStringValueLiteralImpl.this.getText().substring(
                    getRangeInElement().getStartOffset() + 1, 
                    getRangeInElement().getEndOffset() - 1
            );
        }
        
        // do we need to override?
        @Override
        public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
            String newText = getText().substring(0, getRangeInElement().getStartOffset() + 1) + newElementName + getText().substring(getRangeInElement().getEndOffset() - 1); 
            LSFLocalizedStringValueLiteral newLiteral = createLocalizedStringValueLiteral(getProject(), newText);
            LSFLocalizedStringValueLiteralImpl.this.replace(newLiteral);
            return null;
        }
        
        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];    
        }
    }    
}
