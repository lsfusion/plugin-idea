package com.lsfusion.actions;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
                    fillExecFile(linuxExecFile.toPath(), modulesPaths);

                    String linuxExecPath = linuxExecFile.getPath();
                    exitCode = Runtime.getRuntime().exec(new String[] {"chmod", "+x", linuxExecPath}).waitFor(); //make file executable

                    if (exitCode == 0)
                        exitCode = registerLinuxProtocol(linuxExecPath);
                } else if (SystemUtils.IS_OS_MAC) {
                    File macExecFile = new File(customProtocolPath, "macos-exec.sh");
                    copyFile(macExecFile);
                    fillExecFile(macExecFile.toPath(), modulesPaths);

                    String macExecPath = macExecFile.getPath();
                    exitCode = Runtime.getRuntime().exec(new String[] {"chmod", "+x", macExecPath}).waitFor(); //make file executable

                    if (exitCode == 0)
                        exitCode = registerMacProtocol(customProtocolPath, macExecPath);
                } else if (SystemUtils.IS_OS_WINDOWS) {
                    File windowsExecFile = new File(customProtocolPath, "windows-exec.bat");
                    copyFile(windowsExecFile);
                    fillExecFile(windowsExecFile.toPath(), modulesPaths);

                    File windowsSetupFile = new File(customProtocolPath, "windows-setup.reg");
                    copyFile(windowsSetupFile);
                    Path windowsSetupFilePath = windowsSetupFile.toPath();
                    String windowsExecPath = windowsExecFile.getPath().replaceAll("\\\\", "\\\\\\\\");
                    Files.write(windowsSetupFilePath, Files.readString(windowsSetupFilePath)
                            .replace("$1$", "\"\\\"" + windowsExecPath + "\\\" \\\"%1\\\"\"").getBytes());
                    exitCode = Runtime.getRuntime().exec(new String[]{"cmd", "/c", windowsSetupFilePath.toString()}).waitFor();
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
                        "Exec=\"" + scriptPath + "\" %u\n" +
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

    private int registerMacProtocol(String customProtocolPath, String execScriptPath) throws IOException, InterruptedException {
        //macOS delivers custom-scheme URLs via the "open location" Apple Event, which a bare shell
        //script cannot receive, so wrap the exec script in an AppleScript .app bundle.
        File setupFile = new File(customProtocolPath, "macos-setup.applescript");
        copyFile(setupFile);
        Path setupFilePath = setupFile.toPath();
        Files.write(setupFilePath, Files.readString(setupFilePath).replace("$1$", execScriptPath).getBytes());

        File appFile = new File(customProtocolPath, "lsfusion-protocol.app");
        if (appFile.exists())
            FileUtils.deleteDirectory(appFile);

        int exitCode = Runtime.getRuntime().exec(new String[]{"osacompile", "-o", appFile.getPath(), setupFile.getPath()}).waitFor();
        if (exitCode != 0)
            return exitCode;

        //declare the lsfusion-protocol scheme in the bundle's Info.plist and hide it from the Dock
        String infoPlistPath = new File(appFile, "Contents/Info.plist").getPath();
        exitCode = Runtime.getRuntime().exec(new String[]{
                "/usr/libexec/PlistBuddy",
                "-c", "Add :CFBundleURLTypes array",
                "-c", "Add :CFBundleURLTypes:0 dict",
                "-c", "Add :CFBundleURLTypes:0:CFBundleURLName string com.lsfusion.protocol",
                "-c", "Add :CFBundleURLTypes:0:CFBundleURLSchemes array",
                "-c", "Add :CFBundleURLTypes:0:CFBundleURLSchemes:0 string lsfusion-protocol",
                "-c", "Add :LSUIElement bool true",
                infoPlistPath
        }).waitFor();
        if (exitCode != 0)
            return exitCode;

        //register the bundle with Launch Services so the scheme resolves to it
        String lsregister = "/System/Library/Frameworks/CoreServices.framework/Frameworks/LaunchServices.framework/Support/lsregister";
        return Runtime.getRuntime().exec(new String[]{lsregister, "-f", appFile.getPath()}).waitFor();
    }

    private void copyFile(File file) throws IOException {
        FileUtils.copyInputStreamToFile(getClass().getClassLoader().getResourceAsStream("custom-protocol/" + file.getName()), file);
    }

    private void fillExecFile(Path filePath, List<String> modulesPaths) throws IOException {
        String fileContent = Files.readString(filePath);
        String ideaBinPath = PathManager.getBinPath();

        String ideaRunnableReplacement;
        if (SystemUtils.IS_OS_LINUX) {
            Path linuxPath = Paths.get(ideaBinPath, "idea.sh");
            if (!Files.exists(linuxPath)) {
                throw new IOException("Could not find idea.sh in " + ideaBinPath);
            }
            ideaRunnableReplacement = ideaBinPath + "/idea.sh";
        } else if (SystemUtils.IS_OS_MAC) {
            //on macOS the home path is "<IDE>.app/Contents"; the command-line launcher lives in Contents/MacOS
            Path macOsDir = Paths.get(PathManager.getHomePath(), "MacOS");
            Path launcher = macOsDir.resolve("idea");
            if (!Files.exists(launcher)) {
                launcher = findMacLauncher(macOsDir);
            }
            //the path contains spaces ("IntelliJ IDEA.app"), so it must stay quoted in the script
            ideaRunnableReplacement = "\"" + launcher + "\"";
        } else {
            String exeName;
            if (Files.exists(Paths.get(ideaBinPath, "idea64.exe"))) {
                exeName = "idea64.exe";
            } else if (Files.exists(Paths.get(ideaBinPath, "openide64.exe"))) {
                exeName = "openide64.exe";
            } else {
                throw new IOException("Could not find idea executable file in " + ideaBinPath);
            }
            ideaRunnableReplacement = "\"" + ideaBinPath.replaceAll("\\\\", "/") + "/" + exeName + "\"";
        }

        String projectModulesReplacement = SystemUtils.IS_OS_WINDOWS ?
                StringUtils.join(modulesPaths, " ").replaceAll("\\\\", "/") :
                "(" + StringUtils.join(modulesPaths, " ") + ")";

        fileContent = fileContent.replaceAll("IDEA_RUNNABLE=.*", "IDEA_RUNNABLE=" + ideaRunnableReplacement);
        fileContent = fileContent.replaceAll("PROJECT_MODULES=.*", "PROJECT_MODULES=" + projectModulesReplacement);
        Files.write(filePath, fileContent.getBytes());
    }

    private Path findMacLauncher(Path macOsDir) throws IOException {
        try (Stream<Path> files = Files.list(macOsDir)) {
            return files.filter(Files::isExecutable).findFirst()
                    .orElseThrow(() -> new IOException("Could not find idea executable in " + macOsDir));
        }
    }
}
