package com.lsfusion.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;

public class RegisterCustomProtocolAction extends AnAction {

    //scheduled for removal in version 2021.3
    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Custom protocol Group", NotificationDisplayType.BALLOON, true);

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            String basePath = project.getBasePath();
            try {
                int exitCode = -1;
                //linux
                if (SystemUtils.IS_OS_LINUX) {
                    //copy .sh file to project root
                    copyFile(basePath, "linux-exec.sh");
                    String linuxScriptPath = new File(basePath, "linux-exec.sh").getPath();
                    exitCode = Runtime.getRuntime().exec("chmod +x " + linuxScriptPath).waitFor(); //make file executable

                    if (exitCode == 0)
                        exitCode = registerLinuxProtocol(linuxScriptPath);
                } else if (SystemUtils.IS_OS_WINDOWS) {
                    //windows
                    copyFile(basePath, "windows-exec.bat");
                    String windowsExecPath = new File(basePath, "windows-exec.bat").getPath();

                    copyFile(basePath, "windows-setup.bat");
                    exitCode = Runtime.getRuntime().exec("cmd /c " + "\"\"" + basePath + "/windows-setup.bat" + "\" " + "\"" + windowsExecPath + "\"\"").waitFor();
                }

                sendNotification(project, exitCode, "Successfully registred lsfusion-protocol");
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

    private void copyFile(String basePath, String fileName) throws IOException {
        InputStream initialStream = getClass().getClassLoader().getResourceAsStream("custom-protocol/" + fileName);
        FileUtils.copyInputStreamToFile(initialStream, new File(basePath, fileName));
    }
}
