package com.lsfusion.usage;

import com.intellij.psi.PsiElement;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProviderEx;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFGroupDeclaration;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFMetaReference;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFUsageTypeProvider implements UsageTypeProviderEx {
//    private statement ::=
//      constraintStatement
//    |	groupStatement
//    |	overrideStatement // starts with ID
//    |	classStatement
//    |	followsStatement // starts with ID
//    |	writeWhenStatement // starts with ID
//    |	explicitInterfacePropertyStatement
//    |	eventStatement
//    |	showDepStatement
//    |	globalEventStatement
//    |	aspectStatement
//    |	tableStatement
//    |	loggableStatement
//    |	indexStatement
//    |	formStatement
//    |	designStatement
//    |	windowStatement
//    |	navigatorStatement
//    |	metaCodeStatement // ?
//    |	metaCodeDeclarationStatement // ?

    public static final UsageType CONSTRAINT_STATEMENT = new UsageType("CONSTRAINT statement");
    public static final UsageType GROUP_STATEMENT = new UsageType("GROUP statement");
    public static final UsageType OVERRIDE_STATEMENT = new UsageType("OVERRIDE statement");
    public static final UsageType CLASS_STATEMENT = new UsageType("CLASS statement");
    public static final UsageType FOLLOWS_STATEMENT = new UsageType("FOLLOWS statement");
    public static final UsageType WRITE_WHEN = new UsageType("WRITE WHEN statement");
    public static final UsageType PROPERTY_DECLARATION = new UsageType("Property declaration");
    public static final UsageType EVENT_STATEMENT = new UsageType("Event statement");
    public static final UsageType SHOWDEP_STATEMENT = new UsageType("SHOWDEP statement");
    public static final UsageType GLOBAL_EVENT_STATEMENT = new UsageType("Global event statement");
    public static final UsageType ASPECT_STATEMENT = new UsageType("Aspect statement");
    public static final UsageType TABLE_DECLARATION = new UsageType("TABLE declaration");
    public static final UsageType LOGGABLE_STATEMENT = new UsageType("LOGGABLE statement");
    public static final UsageType INDEX_STATEMENT = new UsageType("INDEX statement");
    public static final UsageType FORM_STATEMENT = new UsageType("Form statement");
    public static final UsageType DESIGN_STATEMENT = new UsageType("DESIGN statement");
    public static final UsageType WINDOW_STATEMENT = new UsageType("WINDOW statement");
    public static final UsageType NAVIGATOR_STATEMENT = new UsageType("NAVIGATOR statement");
    public static final UsageType METACODE_REFERENCE = new UsageType("META reference");
    public static final UsageType METACODE_DECLARATION = new UsageType("META declaration");
    public static final UsageType MODULE_STATEMENT= new UsageType("Module statement");
    
    @Nullable
    @Override
    public UsageType getUsageType(PsiElement element) {
        return getUsageType(element, UsageTarget.EMPTY_ARRAY);
    }
    
    @Nullable
    @Override
    public UsageType getUsageType(PsiElement element, @NotNull UsageTarget[] targets) {
        return getLsfUsageType(element);
    }

    @Nullable
    public static UsageType getLsfUsageType(PsiElement element) {
        element = LSFPsiUtils.getStatementParent(element);
        if (element == null) {
            return null;
        }
        
        if (element instanceof LSFClassExtend) {
            return CLASS_STATEMENT;
        } else  if (element instanceof LSFGroupDeclaration) {
            return GROUP_STATEMENT;
        } else if (element instanceof LSFOverrideStatement) {
            return OVERRIDE_STATEMENT;
        } else if (element instanceof LSFConstraintStatement) {
            return CONSTRAINT_STATEMENT;
        } else if (element instanceof LSFFollowsStatement) {
            return FOLLOWS_STATEMENT;
        } else if (element instanceof LSFWriteWhenStatement) {
            return WRITE_WHEN;
        } else if (element instanceof LSFExplicitInterfacePropertyStatement) {
            return PROPERTY_DECLARATION;
        } else if (element instanceof LSFEventStatement) {
            return EVENT_STATEMENT;
        } else if (element instanceof LSFShowDepStatement) {
            return SHOWDEP_STATEMENT;
        } else if (element instanceof LSFGlobalEventStatement) {
            return GLOBAL_EVENT_STATEMENT;
        } else if (element instanceof LSFAspectStatement) {
            return ASPECT_STATEMENT;
        } else if (element instanceof LSFTableDeclaration) {
            return TABLE_DECLARATION;
        } else if (element instanceof LSFLoggableStatement) {
            return LOGGABLE_STATEMENT;
        } else if (element instanceof LSFIndexStatement) {
            return INDEX_STATEMENT;
        } else if (element instanceof LSFFormExtend) {
            return FORM_STATEMENT;
        } else if (element instanceof LSFDesignStatement) {
            return DESIGN_STATEMENT;
        } else if (element instanceof LSFWindowStatement) {
            return WINDOW_STATEMENT;
        } else if (element instanceof LSFNavigatorStatement) {
            return NAVIGATOR_STATEMENT;
        } else if (element instanceof LSFMetaReference) {
            return METACODE_REFERENCE;
        } else if (element instanceof LSFMetaDeclaration) {
            return METACODE_DECLARATION;
        } else if (element instanceof LSFModuleDeclaration) {
            return MODULE_STATEMENT;
        }

        return null;
    }
}
