package com.lsfusion.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class InstallExternalCallsProtocolAction extends AnAction {

    //scheduled for removal in version 2021.3
    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Custom protocol Group", NotificationDisplayType.BALLOON, true);

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            List<String> modulesPaths = new ArrayList<>();
            for (Module module : ModuleManager.getInstance(project).getModules()) {
                VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
                for (VirtualFile contentRoot : contentRoots) {
                    Path path = Paths.get(contentRoot.getPath(), "src/main/lsfusion");
                    if (Files.exists(path))
                        modulesPaths.add("\"" + path + "\"");
                }
            }

            String customProtocolPath = Paths.get(PathManager.getPluginsPath(), "lsfusion-idea-plugin", "custom-protocol").toString();
            try {
                int exitCode = -1;
                if (SystemUtils.IS_OS_LINUX) {
                    //copy .sh file to project root
                    File linuxExecFile = new File(customProtocolPath, "linux-exec.sh");
                    copyFile(linuxExecFile);
                    fillExecFile(linuxExecFile.toPath(), modulesPaths, true);

                    String linuxExecPath = linuxExecFile.getPath();
                    exitCode = Runtime.getRuntime().exec("chmod +x " + linuxExecPath).waitFor(); //make file executable

                    if (exitCode == 0)
                        exitCode = registerLinuxProtocol(linuxExecPath);
                } else if (SystemUtils.IS_OS_WINDOWS) {
                    File windowsExecFile = new File(customProtocolPath, "windows-exec.bat");
                    copyFile(windowsExecFile);
                    fillExecFile(windowsExecFile.toPath(), modulesPaths, false);

                    File windowsSetupFile = new File(customProtocolPath, "windows-setup.reg");
                    copyFile(windowsSetupFile);
                    Path windowsSetupFilePath = windowsSetupFile.toPath();
                    String windowsExecPath = windowsExecFile.getPath().replaceAll("\\\\", "\\\\\\\\");
                    Files.write(windowsSetupFilePath, Files.readString(windowsSetupFilePath)
                            .replace("$1$", "\"\\\"" + windowsExecPath + "\\\" \\\"%1\\\"\"").getBytes());
                    exitCode = Runtime.getRuntime().exec("cmd /c " + windowsSetupFilePath).waitFor();
                }

                sendNotification(project, exitCode, exitCode == 0 ? "Successfully installed lsfusion-protocol" : "Error code " + exitCode);
            } catch (IOException | InterruptedException e) {
                sendNotification(project, -1, e.getMessage());
            }
        }
    }

    private void sendNotification(Project project, int exitCode, String message) {
        NOTIFICATION_GROUP.createNotification(message, exitCode == 0 ? NotificationType.INFORMATION : NotificationType.ERROR).notify(project);
    }

    private int registerLinuxProtocol(String scriptPath) throws IOException, InterruptedException {
        //create .desktop file and register new custom protocol
        String desctopFileContent =
                "[Desktop Entry]\n" +
                        "Name=lsfusion-protocol\n" +
                        "Exec=" + scriptPath + " %u\n" +
                        "Type=Application\n" +
                        "Terminal=false\n" +
                        "MimeType=x-scheme-handler/lsfusion-protocol;";

        String applicationsPath = System.getProperty("user.home") + "/.local/share/applications";
        Path desktopFilePath = Paths.get(applicationsPath + "/lsfusion-protocol.desktop");

        if (!Files.exists(desktopFilePath))
            Files.createFile(desktopFilePath);

        String fileContent = Files.readString(desktopFilePath);
        if (!fileContent.contains(desctopFileContent))
            Files.write(desktopFilePath, desctopFileContent.getBytes());

        return Runtime.getRuntime().exec("update-desktop-database " + applicationsPath).waitFor();
    }

    private void copyFile(File file) throws IOException {
        FileUtils.copyInputStreamToFile(getClass().getClassLoader().getResourceAsStream("custom-protocol/" + file.getName()), file);
    }

    private void fillExecFile(Path filePath, List<String> modulesPaths, boolean isOsLinux) throws IOException {
        String fileContent = Files.readString(filePath);
        String ideaBinPath = PathManager.getBinPath();

        String ideaRunnableReplacement = isOsLinux ?
                ideaBinPath + "/idea.sh" :
                "\"" + ideaBinPath.replaceAll("\\\\", "/") + ((Files.exists(Paths.get(ideaBinPath, "idea64.exe")) ? "/idea64.exe" : "/idea.exe")) + "\"";

        String projectModulesReplacement = isOsLinux ?
                "(" + StringUtils.join(modulesPaths, " ") + ")" :
                StringUtils.join(modulesPaths, " ").replaceAll("\\\\", "/");

        fileContent = fileContent.replaceAll("IDEA_RUNNABLE=.*", "IDEA_RUNNABLE=" + ideaRunnableReplacement);
        fileContent = fileContent.replaceAll("PROJECT_MODULES=.*", "PROJECT_MODULES=" + projectModulesReplacement);
        Files.write(filePath, fileContent.getBytes());
    }
}
