package com.lsfusion.lang.folding;

import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.lang.classes.ConcatenateClassSet;
import com.lsfusion.lang.classes.CustomClassSet;
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
    public static Map<Document, Integer> lineUnderChange = ContainerUtil.createConcurrentWeakMap();
    
    private final Document document;
    private LSFPropertyStatement propertyStatement;
    
    public LSFPropertyParamsFoldingManager(LSFPropertyStatement psi, Document document) {
        this.document = document;
        propertyStatement = psi;
    }

    @NotNull
    public List<FoldingDescriptor> buildDescriptors(boolean implicit) {
        List<FoldingDescriptor> result = new ArrayList<>();

        boolean printValueClass = true;
        boolean printParamClasses = true;
        
        if (implicit) {
            LSFPropertyCalcStatement pCalcStatement = propertyStatement.getPropertyCalcStatement();
            if(pCalcStatement != null) {
                LSFExpressionUnfriendlyPD expressionUnfriendlyPD = pCalcStatement.getExpressionUnfriendlyPD();
                if (expressionUnfriendlyPD != null) {
                    if (expressionUnfriendlyPD.getFilterPropertyDefinition() == null && expressionUnfriendlyPD.getGroupPropertyDefinition() == null) {
                        printParamClasses = false;
                        printValueClass = false;
                    }
                } else {
                    printParamClasses = false;
                }
            } else {
                LSFActionStatement actionStatement = propertyStatement.getActionStatement();
                if(actionStatement != null) {
                    printValueClass = false;

                    LSFActionUnfriendlyPD actionUFD = actionStatement.getActionUnfriendlyPD();
                    if (actionUFD != null) {
                        if (actionUFD.getEditFormActionPropertyDefinitionBody() == null) {
                            printParamClasses = false;
                        }
                    } else {
                        printParamClasses = false;
                    }
                }
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
                    result.add(new LSFNamedFoldingDescriptor(equalsSign, text));
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
                                descriptors.add(new LSFNamedFoldingDescriptor(equalsSign, foldRegion.getPlaceholderText()));
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
        List<String> names = new ArrayList<>();
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
        } else if (classSet instanceof CustomClassSet) {
            String classString = classSet.toString();
            return classString.contains(", ") ? "[" + classString + "]" : classString;
        } else {
            return classSet.toString();
        }  
    } 
    
    private boolean allClassesDefined(LSFPropertyDeclaration propertyDeclaration) {
        List<LSFParamDeclaration> paramDeclarations = propertyDeclaration.resolveParamDecls();
        return paramDeclarations != null && LSFPsiImplUtil.getClassNameRefs(paramDeclarations).allClassesDeclared();
    }
    
    public static boolean rebuildFoldings(Document document, int newPosition) {
        Integer oldPosition = lineUnderChange.get(document);
        return oldPosition != null && oldPosition != newPosition;
    } 
    
    public static void foldingsRebuilt(Editor editor) {
        Document document = editor.getDocument();
        int lineNumber = document.getLineNumber(editor.getCaretModel().getOffset());
        if (rebuildFoldings(document, lineNumber)) {
            lineUnderChange.remove(document);
        }
    }
    
    public static void updateFoldRegions(Editor editor) {
        Project project = editor.getProject();
        if (project != null) {
            Runnable runnable = CodeFoldingManager.getInstance(project).updateFoldRegionsAsync(editor, true);
            if (runnable != null) {
                runnable.run();
                foldingsRebuilt(editor);
            }
        }
    }
}
