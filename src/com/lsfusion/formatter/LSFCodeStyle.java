package com.lsfusion.formatter;

import com.intellij.formatting.Spacing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFEqualityPE;
import com.lsfusion.lang.psi.LSFTypeMult;
import com.lsfusion.lang.psi.LSFTypes;

// Spacings of one formatting run, resolved from the code style settings of the file being formatted.
// Line breaks are always kept: the blocks separate statements only by the line breaks already in the source.
public class LSFCodeStyle {
    private final CommonCodeStyleSettings common;
    private final LSFCodeStyleSettings custom;

    private final Spacing defaultInDeclarations;
    private final Spacing defaultInCode;
    private final Spacing space;
    private final Spacing noSpace;

    public LSFCodeStyle(CodeStyleSettings settings) {
        common = settings.getCommonSettings(LSFLanguage.INSTANCE);
        custom = settings.getCustomSettings(LSFCodeStyleSettings.class);

        defaultInDeclarations = Spacing.createSpacing(0, 1, 0, true, common.KEEP_BLANK_LINES_IN_DECLARATIONS);
        defaultInCode = Spacing.createSpacing(0, 1, 0, true, common.KEEP_BLANK_LINES_IN_CODE);
        space = Spacing.createSpacing(1, 1, 0, true, common.KEEP_BLANK_LINES_IN_CODE);
        noSpace = Spacing.createSpacing(0, 0, 0, true, common.KEEP_BLANK_LINES_IN_CODE);
    }

    // 0..1 spaces, existing line breaks and blank lines kept up to the limit
    public Spacing defaultSpacing(boolean declarations) {
        return declarations ? defaultInDeclarations : defaultInCode;
    }

    public Spacing aroundOperator(PsiElement operator) {
        return exactly(isSpaceAround(operator));
    }

    public Spacing afterComma() {
        return exactly(common.SPACE_AFTER_COMMA);
    }

    public Spacing beforeComma() {
        return exactly(common.SPACE_BEFORE_COMMA);
    }

    public Spacing betweenModuleHeaderStatements() {
        return blankLines(custom.BLANK_LINES_IN_MODULE_HEADER, true);
    }

    public Spacing beforeObjects() {
        return blankLines(custom.BLANK_LINES_BEFORE_OBJECTS, false);
    }

    private Spacing exactly(boolean space) {
        return space ? this.space : noSpace;
    }

    // at least the given number of blank lines before the element
    private Spacing blankLines(int count, boolean declarations) {
        int keep = declarations ? common.KEEP_BLANK_LINES_IN_DECLARATIONS : common.KEEP_BLANK_LINES_IN_CODE;
        return Spacing.createSpacing(0, 1, count + 1, true, Math.max(keep, count));
    }

    private boolean isSpaceAround(PsiElement operator) {
        if (operator instanceof LSFTypeMult) {
            return common.SPACE_AROUND_MULTIPLICATIVE_OPERATORS;
        }
        IElementType type = ((LeafPsiElement) operator).getElementType();
        if (type == LSFTypes.EQUALS) {
            // '=' compares in an expression and defines everywhere else (property, OBJECTS, parameters)
            return operator.getParent() instanceof LSFEqualityPE ? common.SPACE_AROUND_EQUALITY_OPERATORS : common.SPACE_AROUND_ASSIGNMENT_OPERATORS;
        } else if (type == LSFTypes.ARROW || type == LSFTypes.PLUSEQ) {
            return common.SPACE_AROUND_ASSIGNMENT_OPERATORS;
        } else if (type == LSFTypes.EQ_OPERAND) {
            return common.SPACE_AROUND_EQUALITY_OPERATORS;
        } else if (type == LSFTypes.LESS || type == LSFTypes.GREATER || type == LSFTypes.LESS_EQUALS || type == LSFTypes.GREATER_EQUALS) {
            return common.SPACE_AROUND_RELATIONAL_OPERATORS;
        } else {
            return common.SPACE_AROUND_ADDITIVE_OPERATORS; // PLUS, MINUS
        }
    }
}
