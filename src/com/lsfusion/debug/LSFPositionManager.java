/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lsfusion.debug;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.PositionManagerEx;
import com.intellij.debugger.engine.evaluation.EvaluationContext;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.lsfusion.debug.DebugUtils.DELEGATES_HOLDER_CLASS_FQN_PREFIX;

public class LSFPositionManager extends PositionManagerEx {

    private final DebugProcess debugProcess;

    public LSFPositionManager(DebugProcess debugProcess) {
        this.debugProcess = debugProcess;
    }

    @Nullable
    @Override
    public SourcePosition getSourcePosition(@Nullable Location location) throws NoDataException {
        if (location != null) {
            ReferenceType referenceType = location.declaringType();
            String typeName = referenceType.name();
            if (typeName.startsWith(DELEGATES_HOLDER_CLASS_FQN_PREFIX)) {
                Project project = debugProcess.getProject();
                String moduleName = typeName.substring(DELEGATES_HOLDER_CLASS_FQN_PREFIX.length());
                String methodName = location.method().name();
                int ind1 = methodName.indexOf('_');
                int ind2 = methodName.indexOf('_', ind1 + 1);
                if(ind2 < 0)
                    ind2 = methodName.length();
                
                int line = -1;
                int posInLine = -1;
                try {
                    line = Integer.parseInt(methodName.substring(ind1 + 1, ind2));
                    if(ind2 < methodName.length())
                        posInLine = Integer.parseInt(methodName.substring(ind2 + 1));
                } catch (Exception ignored) {
                    System.out.println("Can't parse line number in: " + methodName);
                }
                
                if (line < 0) {
                    return null;
                }

                Collection<LSFModuleDeclaration> some = DumbService.getInstance(project).runReadActionInSmartMode(() -> LSFGlobalResolver.findModules(moduleName, project, debugProcess.getSearchScope()));
                if (some.isEmpty()) {
                    return null;
                }

                final LSFFile file = some.iterator().next().getLSFFile();
                if(posInLine < 0) {
                    return SourcePosition.createFromLine(file, line);
                }
                
                final Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
                if (document == null) {
                    return SourcePosition.createFromLine(file, line);
                }
                
                SourcePosition fromOffset = SourcePosition.createFromOffset(file, document.getLineStartOffset(line) + posInLine);
                if(fromOffset.getLine() != line)
                    return SourcePosition.createFromLine(file, line);
                
                return fromOffset;
            }
        }
        throw NoDataException.INSTANCE;
    }

    @NotNull
    @Override
    public List<Location> locationsOfLine(@NotNull final ReferenceType type, @NotNull final SourcePosition position) throws NoDataException {

        if(!(position.getFile() instanceof LSFFile))
            throw NoDataException.INSTANCE;
            
        try {
            return Collections.singletonList(type.methodsByName("action_" + position.getLine()).get(0).allLineLocations().iterator().next());
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    @NotNull
    @Override
    public List<ReferenceType> getAllClasses(@NotNull SourcePosition position) throws NoDataException {
        final PsiFile psiFile = position.getFile();
        if (psiFile instanceof LSFFile) {
            return ApplicationManager.getApplication().runReadAction((Computable<List<ReferenceType>>) () -> {
                LSFFile lsfFile = (LSFFile) psiFile;
                String moduleName = lsfFile.getModuleDeclaration().getGlobalName();
                return debugProcess.getVirtualMachineProxy().classesByName(DELEGATES_HOLDER_CLASS_FQN_PREFIX + moduleName);
            });
        }
        throw NoDataException.INSTANCE;
    }

    @Nullable
    @Override
    public ClassPrepareRequest createPrepareRequest(@NotNull final ClassPrepareRequestor requestor, @NotNull SourcePosition position) throws NoDataException {
        final PsiFile psiFile = position.getFile();
        if (psiFile instanceof LSFFile) {
            return ApplicationManager.getApplication().runReadAction((Computable<ClassPrepareRequest>) () -> {
                LSFFile lsfFile = (LSFFile) psiFile;
                String moduleName = lsfFile.getModuleDeclaration().getGlobalName();
                return debugProcess.getRequestsManager().createClassPrepareRequest(requestor, DELEGATES_HOLDER_CLASS_FQN_PREFIX + moduleName);
            });
        }
        throw NoDataException.INSTANCE;
    }

    @Nullable
    @Override
    public XStackFrame createStackFrame(@NotNull StackFrameProxyImpl frame, @NotNull DebugProcessImpl debugProcess, @NotNull final Location location) {
        final SourcePosition position = ApplicationManager.getApplication().runReadAction((Computable<SourcePosition>) () -> {
            try {
                return getSourcePosition(location);
            } catch (NoDataException e) {
                return null;
            }
        });
        
        if (position != null) {
            XSourcePositionImpl xpos = ApplicationManager.getApplication().runReadAction(
                    (Computable<XSourcePositionImpl>) () -> XSourcePositionImpl.createByOffset(position.getFile().getVirtualFile(), position.getOffset()));
            if (xpos != null) {
                return new LSFStackFrame(debugProcess.getProject(), frame, debugProcess, xpos);
            }
        }
        return null;
    }

    @Override
    public ThreeState evaluateCondition(@NotNull EvaluationContext context, @NotNull StackFrameProxyImpl frame, @NotNull Location location, @NotNull String expression) {
        return ThreeState.UNSURE;
    }
}