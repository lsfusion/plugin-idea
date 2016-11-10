package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.lang.properties.psi.impl.PropertyImpl;
import com.intellij.lang.properties.references.PropertyReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.IncorrectOperationException;
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
    
    private synchronized void initPropertyFiles() {
        if (propertyFiles == null) {
            propertyFiles = new HashSet<>();
            Pattern pattern = Pattern.compile("[^/]*ResourceBundle\\.properties");
            GlobalSearchScope scope = ProjectScope.getProjectScope(getProject());
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
                        refs.add(new PropertyFileItemMultiReference(startPos, i+1));
                        insideKey = false;
                    }
                } else if (insideKey && literal.charAt(i) == ' ') break; 
            }
            return refs;
        }
        return new ArrayList<>();
    }

    @Override
    public String getValue() {
        String text = getText();
        if (needToBeLocalized()) {
            return text.substring(1, text.length() - 1);
        } else {
            return removeEscapeSymbols(text.substring(1, text.length() - 1));
        }
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
    
    private static String removeEscapeSymbols(String s) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char cur = s.charAt(i);
            if (cur == '\\' && i+1 < s.length()) {
                char next = s.charAt(i+1);
                if (next == '\\' || next == '{' || next == '}') {
                    builder.append(next);
                    ++i;
                    continue;
                }
            }
            builder.append(cur);
        }
        return builder.toString();
    }
    
    @NotNull
    @Override 
    public PsiReference[] getReferences() {
        String literal = getText();
        List<PsiReference> refs = findAllRefs(literal);
        return refs.toArray(new PsiReference[refs.size()]);
    }
    
    public class PropertyFileItemMultiReference implements PsiPolyVariantReference {
        private TextRange range;
        
        public PropertyFileItemMultiReference(int start, int end) {
            this.range = new TextRange(start, end);
        }
        
        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            List<ResolveResult> results = new ArrayList<>();
            String key = getReferenceId();
            for (VirtualFile file : propertyFiles) {
                PropertyReference ref = new PropertyReference(key, LSFLocalizedStringValueLiteralImpl.this, file.getNameWithoutExtension(), true, range);
                ResolveResult[] result = ref.multiResolve(false);
                Collections.addAll(results, result);
            }
            return results.toArray(new ResolveResult[results.size()]);
        }

        @Override
        public PsiElement getElement() {
            return LSFLocalizedStringValueLiteralImpl.this;
        }

        @Override
        public TextRange getRangeInElement() {
            return range;    
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            ResolveResult[] res = multiResolve(false);
            if (res.length == 1) {
                return res[0].getElement();
            } else {
                return null;
            }
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            return getReferenceId();
        }

        private String getReferenceId() {
            return LSFLocalizedStringValueLiteralImpl.this.getText().substring(range.getStartOffset() + 1, range.getEndOffset() - 1);
        }
        
        @Override
        public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
            String newText = getText().substring(0, range.getStartOffset() + 1) + newElementName + getText().substring(range.getEndOffset() - 1); 
            LSFLocalizedStringValueLiteral newLiteral = createLocalizedStringValueLiteral(getProject(), newText);
            LSFLocalizedStringValueLiteralImpl.this.replace(newLiteral);
            return null;
        }

        @Override
        public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
            return null;
        }

        @Override
        public boolean isReferenceTo(PsiElement element) {
            return (element instanceof PropertyImpl && getReferenceId().equals(((PropertyImpl) element).getKey()));
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];
        }

        @Override
        public boolean isSoft() {
            return false;
        }
    }    
}
