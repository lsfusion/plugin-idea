package com.lsfusion.lang.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.actions.folding.PropertyFoldingManager;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.LSFTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// CustomFoldingBuilder adds the '//region' / '// <editor-fold>' comment regions on top of the language ones
public class LSFFoldingBuilder extends CustomFoldingBuilder {

    @Override
    protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root, @NotNull Document document, boolean quick) {
        Project project = root.getProject();
        boolean propFoldNone = PropertyFoldingManager.isNone(project);
        boolean propFoldImplicit = PropertyFoldingManager.isImplicit(project);

        buildFolding(root.getNode(), descriptors, document, propFoldNone, propFoldImplicit);
    }

    private static void buildFolding(ASTNode node, List<FoldingDescriptor> list, Document document, boolean propFoldNone, boolean propFoldImplicit) {
        IElementType elementType = node.getElementType();
        boolean fullFolding = elementType == LSFTypes.META_CODE_BODY
                              || elementType == LSFTypes.NAVIGATOR_ELEMENT_STATEMENT_BODY
                              || elementType == LSFTypes.LIST_ACTION_PROPERTY_DEFINITION_BODY
                              || elementType == LSFTypes.COMPONENT_BODY;
        if (fullFolding && node.getTextRange().getLength() > 1) {
            final TextRange range = node.getTextRange();
            list.add(new FoldingDescriptor(node, range));
        }

        if (elementType == LSFTypes.FORM_STATEMENT) {
            ASTNode firstChild = node.getFirstChildNode();
            IElementType firstChildType = firstChild.getElementType();
            if (firstChildType == LSFTypes.FORM_DECL || firstChildType == LSFTypes.EXTENDING_FORM_DECLARATION) {
                final TextRange nodeRange = node.getTextRange();
                TextRange range = new TextRange(nodeRange.getStartOffset() + firstChild.getTextLength(), nodeRange.getEndOffset());
                if (range.getLength() > 2) {
                    list.add(new FoldingDescriptor(node, range));
                }
            }
        }

        if (!propFoldNone && elementType == LSFTypes.PROPERTY_STATEMENT) {
            LSFPropertyParamsFoldingManager propFoldingManager = new LSFPropertyParamsFoldingManager((LSFPropertyStatement) node.getPsi(), document);
            list.addAll(propFoldingManager.buildDescriptors(propFoldImplicit));
        }

        for (ASTNode child : node.getChildren(null)) {
            buildFolding(child, list, document, propFoldNone, propFoldImplicit);
        }
    }

    // CustomFoldingBuilder declares itself dumb-aware, but the inferred property classes fold resolves through the
    // stub indexes, so the builder keeps waiting for indexing as it always did
    @Override
    public boolean isDumbAware() {
        return false;
    }

    @Nullable
    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
        return "{...}";
    }

    // the inferred property classes are always shown folded (that is the point of that region); the rest follows
    // Settings > Editor > General > Code Folding
    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
        IElementType elementType = node.getElementType();
        if (elementType == LSFTypes.EQUALS_SIGN) {
            return true;
        }
        LSFCodeFoldingSettings settings = LSFCodeFoldingSettings.getInstance();
        if (elementType == LSFTypes.META_CODE_BODY) {
            return settings.COLLAPSE_META_BODIES;
        } else if (elementType == LSFTypes.LIST_ACTION_PROPERTY_DEFINITION_BODY) {
            return settings.COLLAPSE_ACTION_BODIES;
        } else if (elementType == LSFTypes.FORM_STATEMENT) {
            return settings.COLLAPSE_FORM_BODIES;
        } else if (elementType == LSFTypes.COMPONENT_BODY) {
            return settings.COLLAPSE_DESIGN_COMPONENTS;
        } else if (elementType == LSFTypes.NAVIGATOR_ELEMENT_STATEMENT_BODY) {
            return settings.COLLAPSE_NAVIGATOR;
        }
        return false;
    }
}
