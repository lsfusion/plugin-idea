package com.lsfusion;

import com.intellij.debugger.SourcePosition;
import com.intellij.execution.filters.ConsoleDependentFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LSFConsoleFilterProvider extends ConsoleDependentFilterProvider {

    @Override
    public Filter @NotNull [] getDefaultFilters(@NotNull ConsoleView consoleView, @NotNull Project project, @NotNull GlobalSearchScope globalSearchScope) {
        return new Filter[]{new LSFFilter(consoleView, project)};
    }

    private static class LSFFilter implements Filter {

        ConsoleView consoleView;
        Project project;

        public LSFFilter(ConsoleView consoleView, Project project) {
            this.consoleView = consoleView;
            this.project = project;
        }

        @Nullable
        @Override
        public Result applyFilter(@NotNull String line, int entireLength) {
            Matcher m = Pattern.compile("[a-zA-Z][a-zA-Z_0-9]*:\\d+:\\d+").matcher(line);
            if (m.find()) {
                String link = m.group();
                int highlightStart = entireLength - line.length() + line.indexOf(link);
                return new Filter.Result(highlightStart, highlightStart + link.length(), project -> {
                    if (!project.isDisposed()) {
                        String[] address = link.split(":");
                        String moduleName = address[0];
                        int lineNum = Integer.parseInt(address[1]) - 1;
                        int posNum = Integer.parseInt(address[2]);
                        LSFModuleDeclaration moduleDecl = LSFGlobalResolver.findModules(moduleName, (GlobalSearchScope) ReflectionUtils.getPrivateFieldValue(consoleView, "mySearchScope")).findFirst();
                        if (moduleDecl != null) {
                            LSFFile lsfFile = moduleDecl.getLSFFile();
                            final Document document = PsiDocumentManager.getInstance(project).getDocument(lsfFile);
                            if (document != null) {
                                PsiElement element = lsfFile.findElementAt(SourcePosition.createFromOffset(lsfFile, document.getLineStartOffset(lineNum) + posNum).getOffset());
                                if (element != null) {
                                    ((Navigatable) element).navigate(true);
                                }
                            }
                        }
                    }
                });

            } else {
                return null;
            }
        }
    }
}
