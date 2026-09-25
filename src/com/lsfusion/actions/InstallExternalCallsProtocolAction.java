package com.lsfusion.actions;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Registers lsfusion-protocol:// with the OS for the lsFusion clients before 7.0, whose tooltips still link to the
// declarations through that scheme. The handler it installs is a small script that forwards the link to the
// plugin's /api/lsfusion-open on the IDE's built-in web server (see LSFOpenFileService), so nothing about the
// project or the IDE is baked into it and it is done once per machine. The 7.0+ clients call the endpoint directly.
public class InstallExternalCallsProtocolAction extends AnAction {
    private static final NotificationGroup NOTIFICATION_GROUP =
            NotificationGroupManager.getInstance().getNotificationGroup("lsFusion");

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            String customProtocolPath = Paths.get(PathManager.getPluginsPath(), "lsfusion-idea-plugin", "custom-protocol").toString();
            try {
                int exitCode = -1;
                if (SystemUtils.IS_OS_LINUX) {
                    File execFile = copyFile(customProtocolPath, "lsfusion-open.sh");
                    exitCode = Runtime.getRuntime().exec(new String[]{"chmod", "+x", execFile.getPath()}).waitFor();
                    if (exitCode == 0)
                        exitCode = registerLinuxProtocol(execFile.getPath());
                } else if (SystemUtils.IS_OS_MAC) {
                    File execFile = copyFile(customProtocolPath, "lsfusion-open.sh");
                    exitCode = Runtime.getRuntime().exec(new String[]{"chmod", "+x", execFile.getPath()}).waitFor();
                    if (exitCode == 0)
                        exitCode = registerMacProtocol(customProtocolPath, execFile.getPath());
                } else if (SystemUtils.IS_OS_WINDOWS) {
                    File execFile = copyFile(customProtocolPath, "lsfusion-open.ps1");
                    exitCode = registerWindowsProtocol(customProtocolPath, execFile.getPath());
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

    private int registerWindowsProtocol(String customProtocolPath, String execScriptPath) throws IOException, InterruptedException {
        File setupFile = copyFile(customProtocolPath, "windows-setup.reg");
        Path setupFilePath = setupFile.toPath();
        String systemRoot = System.getenv("SystemRoot");
        String powershell = Paths.get(systemRoot != null ? systemRoot : "C:\\Windows", "System32", "WindowsPowerShell", "v1.0", "powershell.exe").toString();
        // a .reg string value: backslashes and quotes escaped
        String command = "\"" + powershell + "\" -NoProfile -ExecutionPolicy Bypass -WindowStyle Hidden -File \"" + execScriptPath + "\" \"%1\"";
        String regValue = "\"" + command.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        // UTF-16LE with a BOM is what regedit exports and "reg import" reads without guessing the encoding: the
        // command holds the plugins path, which may well have non-ASCII characters in it
        Files.write(setupFilePath, ("﻿" + Files.readString(setupFilePath).replace("$1$", regValue)).getBytes(StandardCharsets.UTF_16LE));
        // "reg import" writes the HKCU keys without the confirmation dialog regedit shows for a double-clicked .reg
        return Runtime.getRuntime().exec(new String[]{"reg", "import", setupFilePath.toString()}).waitFor();
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
        if (!Files.exists(desktopFilePath)) {
            Files.createDirectories(desktopFilePath.getParent()); // a fresh account may have no ~/.local/share/applications
            Files.createFile(desktopFilePath);
        }
        String fileContent = Files.readString(desktopFilePath);
        if (!fileContent.contains(desctopFileContent))
            Files.write(desktopFilePath, desctopFileContent.getBytes(StandardCharsets.UTF_8));
        return Runtime.getRuntime().exec(new String[]{"update-desktop-database", applicationsPath}).waitFor();
    }

    private int registerMacProtocol(String customProtocolPath, String execScriptPath) throws IOException, InterruptedException {
        //macOS delivers custom-scheme URLs via the "open location" Apple Event, which a bare shell
        //script cannot receive, so wrap the exec script in an AppleScript .app bundle.
        File setupFile = copyFile(customProtocolPath, "macos-setup.applescript");
        Path setupFilePath = setupFile.toPath();
        // an AppleScript string literal: a path may legally contain a quote or a backslash on macOS
        String quotedScriptPath = execScriptPath.replace("\\", "\\\\").replace("\"", "\\\"");
        Files.write(setupFilePath, Files.readString(setupFilePath).replace("$1$", quotedScriptPath).getBytes(StandardCharsets.UTF_8));
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

    private File copyFile(String customProtocolPath, String name) throws IOException {
        File file = new File(customProtocolPath, name);
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("custom-protocol/" + name)) {
            if (resource == null) {
                throw new IOException("custom-protocol/" + name + " is missing from the plugin");
            }
            FileUtils.copyInputStreamToFile(resource, file);
        }
        return file;
    }
}
