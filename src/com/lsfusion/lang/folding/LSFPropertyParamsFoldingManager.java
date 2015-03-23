package com.lsfusion.lang.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.util.containers.ConcurrentWeakHashMap;
import com.lsfusion.lang.classes.ConcatenateClassSet;
import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LSFPropertyParamsFoldingManager {
    public static Map<Document, Integer> lineUnderChange = new ConcurrentWeakHashMap<Document, Integer>();
    
    private final Document document;
    private LSFPropertyStatement propertyStatement;
    
    public LSFPropertyParamsFoldingManager(ASTNode node, Document document) {
        this.document = document;
        propertyStatement = (LSFPropertyStatement) node.getPsi();
    }

    @NotNull
    public List<FoldingDescriptor> buildDescriptors(boolean implicit) {
        List<FoldingDescriptor> result = new ArrayList<FoldingDescriptor>();

        boolean printValueClass = true;
        boolean printParamClasses = true;
        
        if (implicit) {
            LSFExpressionUnfriendlyPD expressionUnfriendlyPD = propertyStatement.getExpressionUnfriendlyPD();
            if (expressionUnfriendlyPD != null) {
                LSFContextIndependentPD cipd = expressionUnfriendlyPD.getContextIndependentPD();
                if (cipd != null && cipd.getFilterPropertyDefinition() == null && cipd.getGroupPropertyDefinition() == null) {
                    printParamClasses = false;
                    printValueClass = false;
                }
            } else {
                printParamClasses = false;
            }
        }
        
        // в случае, если курсор находится в той же строке, что и знак '=', не добавляем фолдинг или не обновляем (если уже есть), чтобы избежать прыжков 
        boolean currentLine = rebuildIfNotInCurrentLine(result);

        if (!currentLine) {
            LSFEqualsSign equalsSign = propertyStatement.getEqualsSign();

            if (equalsSign != null) {
                String text = "";
                if (printParamClasses && !allClassesDefined(propertyStatement.getPropertyDeclaration())) {
                    List<LSFClassSet> paramClasses = propertyStatement.resolveParamClasses();
                    if (paramClasses != null && !paramClasses.isEmpty() && !BaseUtils.isAllNull(paramClasses)) {
                        text = "(" + BaseUtils.toString(getClassNames(paramClasses), ", ") + ") ";
                    }
                }

                if (printValueClass) {
                    String className = getClassName(propertyStatement.resolveValueClass());
                    if (className != null) {
                        text += "-> " + className + " ";
                    }
                }

                if (!text.isEmpty()) {
                    text += "=";
                    result.add(new LSFNamedFoldingDescriptor(equalsSign, text, true));
                }
            }
        }
        
        return result;
    }
    
    private boolean rebuildIfNotInCurrentLine(List<FoldingDescriptor> descriptors) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        if (editorFactory != null) {
            Editor[] editors = editorFactory.getEditors(document, propertyStatement.getProject());
            if (editors.length == 1) {
                Editor selectedTextEditor = editors[0];
                if (selectedTextEditor != null) {
                    int caretOffset = selectedTextEditor.getCaretModel().getOffset();
                    LSFEqualsSign equalsSign = propertyStatement.getEqualsSign();
                    if (equalsSign != null) {
                        int equalsOffset = equalsSign.getTextOffset();
                        int equalsLine = document.getLineNumber(equalsOffset);

                        if (caretOffset >= document.getLineStartOffset(equalsLine) && caretOffset <= document.getLineEndOffset(equalsLine)) {
                            lineUnderChange.put(document, equalsLine);
                            FoldRegion foldRegion = selectedTextEditor.getFoldingModel().getCollapsedRegionAtOffset(equalsOffset);
                            if (foldRegion != null) {
                                descriptors.add(new LSFNamedFoldingDescriptor(equalsSign, foldRegion.getPlaceholderText(), true));
                            }
                            return true;
                        }
                    }
                }
            }
        }    
        return false;
    }
    
    private List<String> getClassNames(List<LSFClassSet> classSets) {
        List<String> names = new ArrayList<String>();
        for (LSFClassSet classSet : classSets) {
            names.add(getClassName(classSet));
        }
        return names;
    }
    
    private String getClassName(LSFClassSet classSet) {
        if (classSet == null || classSet instanceof ConcatenateClassSet) {
            return null;
        } else if (classSet instanceof DataClass) {
            return ((DataClass) classSet).getName();
        } else {
            return classSet.toString();
        }  
    } 
    
    private boolean allClassesDefined(LSFPropertyDeclaration propertyDeclaration) {
        List<LSFParamDeclaration> paramDeclarations = propertyDeclaration.resolveParamDecls();
        if (paramDeclarations == null) {
            return false;
        }
        for (LSFParamDeclaration declaration : paramDeclarations) {
            if (declaration.getClassName() == null) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean rebuildFoldings(Document document, int newPosition) {
        Integer oldPosition = lineUnderChange.get(document);
        if (oldPosition != null && oldPosition != newPosition) {
            return true;
        }
        return false;
    } 
    
    public static void foldingsRebuilt(Document document) {
        lineUnderChange.remove(document);
    }
}
