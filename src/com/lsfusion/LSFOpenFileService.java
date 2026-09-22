package com.lsfusion;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.AppIcon;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.RestService;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// "Open in IDE" for the declaration links in the lsFusion clients' tooltips, served by the IDE's built-in web server:
//   GET http://localhost:63342/api/lsfusion-open?path=stock/Stock.lsf&line=148&column=5
// The clients know a module file only by its path relative to the source root (src/main/lsfusion) and a 1-based
// position. The file is looked up by name in every open project and must end with that relative path. The
// built-in server only listens on localhost; a page from another host gets through after the user confirms it once
// (isOriginAllowed). The clients try the ports 63342-63352 (the built-in server's default and the ones the next IDE
// instances take - the port must stay at its default) in turn until one answers 200, so 200 means "the file is here
// and the editor has been asked to open it" and nothing else.
public class LSFOpenFileService extends RestService {
    @Override
    protected @NotNull String getServiceName() {
        return "lsfusion-open"; // not "lsfusion/open": the MCP service owns /api/lsfusion and everything under it
    }

    @Override
    protected boolean isMethodSupported(@NotNull HttpMethod method) {
        return HttpMethod.GET.equals(method);
    }

    // a web client is usually served from another host: instead of the default silent 404 for a non-local origin,
    // let RestService ask the user once whether that host may open files (the answer is remembered)
    @Override
    protected @NotNull OriginCheckResult isOriginAllowed(@NotNull HttpRequest request) {
        OriginCheckResult result = super.isOriginAllowed(request);
        return result == OriginCheckResult.FORBID ? OriginCheckResult.ASK_CONFIRMATION : result;
    }

    @Override
    public @Nullable String execute(@NotNull QueryStringDecoder urlDecoder, @NotNull FullHttpRequest request, @NotNull ChannelHandlerContext context) {
        String path = getStringParameter("path", urlDecoder);
        if (path == null || path.isBlank()) {
            // spelled out rather than returned as a message, so that every exit of this method is an explicit status
            sendStatus(HttpResponseStatus.BAD_REQUEST, HttpUtil.isKeepAlive(request), context.channel());
            return null;
        }
        // the server keeps module paths as /stock/Stock.lsf, relative to the source root
        path = path.replace('\\', '/').replaceAll("^/+", "");
        int line = getIntParameter("line", urlDecoder);
        int column = getIntParameter("column", urlDecoder);

        String name = path.substring(path.lastIndexOf('/') + 1);
        String suffix = "/" + path;
        // the request does not say which project it is about: with the same module in several open projects (two
        // branches of one application) the one the user worked in last is the best guess
        List<Project> projects = new ArrayList<>(Arrays.asList(ProjectManager.getInstance().getOpenProjects()));
        Project lastFocused = getLastFocusedOrOpenedProject();
        if (lastFocused != null && projects.remove(lastFocused)) {
            projects.add(0, lastFocused);
        }
        List<Project> smart = new ArrayList<>(projects);
        smart.removeIf(DumbService::isDumb);
        boolean indexing = smart.size() != projects.size();
        try {
            if (open(smart, name, suffix, line, column)) {
                sendOk(request, context);
                return null;
            }
        } catch (IndexNotReadyException e) {
            indexing = true; // a project went dumb between the check and the lookup
        }
        if (!indexing) {
            // not here - the same answer as from an IDE without the plugin, so the client goes on to the next one
            sendStatus(HttpResponseStatus.NOT_FOUND, HttpUtil.isKeepAlive(request), context.channel());
            return null;
        }
        // the file may be in a project whose index is not there to ask yet: waiting for it here would hold a web
        // server thread for minutes, and 200 would stop the client from trying the other IDEs, so 503 - the user
        // clicks again once the indexing is over
        sendStatus(HttpResponseStatus.SERVICE_UNAVAILABLE, HttpUtil.isKeepAlive(request), context.channel());
        return null;
    }

    private static boolean open(List<Project> projects, String name, String suffix, int line, int column) {
        Pair<Project, VirtualFile> found = findFile(projects, name, suffix);
        if (found == null) {
            return false;
        }
        Project project = found.first;
        VirtualFile file = found.second;
        ApplicationManager.getApplication().invokeLater(() -> {
            if (!project.isDisposed()) {
                new OpenFileDescriptor(project, file, Math.max(line - 1, 0), Math.max(column - 1, 0)).navigate(true);
                JFrame frame = WindowManager.getInstance().getFrame(project);
                if (frame != null) {
                    AppIcon.getInstance().requestFocus(frame);
                }
            }
        });
        return true;
    }

    // The project's own sources beat a copy in a library (a module inside the platform jar is still worth opening
    // when the project has no source for it), so no project may settle for the latter before all of them were asked
    // for the former. Only files whose path ends with the requested one count: a mere namesake elsewhere would be
    // opened - and, with 200, claimed - instead of the right file in another IDE.
    private static @Nullable Pair<Project, VirtualFile> findFile(List<Project> projects, String name, String suffix) {
        for (boolean sourcesOnly : new boolean[]{true, false}) {
            for (Project project : projects) {
                if (project.isDisposed()) {
                    continue;
                }
                VirtualFile file = ReadAction.compute(() -> {
                    GlobalSearchScope scope = sourcesOnly ? GlobalSearchScope.projectScope(project) : GlobalSearchScope.allScope(project);
                    for (VirtualFile candidate : FilenameIndex.getVirtualFilesByName(name, scope)) {
                        if (candidate.getPath().endsWith(suffix)) {
                            return candidate;
                        }
                    }
                    return null;
                });
                if (file != null) {
                    return Pair.create(project, file);
                }
            }
        }
        return null;
    }
}
