package com.lsfusion;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DAle on 27.02.14.
 */

public class ReportFileldsChanger {
    private String extractFormNameFromPath(String reportPath) {
        Map<String, String> explicitLink = new HashMap<String, String>();
        explicitLink.put("D:/lsfusion/custom-logics/mothercare-logics/src/main/resources/reports/custom/machinery/label/LabelMothercare_printLabelTransactionA_l.jrxml", "Label_printLabelTransaction");
        explicitLink.put("D:/lsfusion/custom-logics/mothercare-logics/src/main/resources/reports/custom/machinery/label/LabelMothercare_printLabelTransactionB_l.jrxml", "Label_printLabelTransaction");
        explicitLink.put("D:/lsfusion/custom-logics/sot-logics/src/main/resources/reports/custom/machinery/label/LabelSOT_printLabelTransactionA_l.jrxml", "Label_printLabelTransaction");

        if (explicitLink.containsKey(reportPath)) {
            return explicitLink.get(reportPath);
        }

        String fileName = reportPath.substring(reportPath.lastIndexOf('/') + 1);
        String formName;
        if (fileName.indexOf('_') == fileName.lastIndexOf('_')) {
            formName = fileName.substring(0, fileName.lastIndexOf('.'));
        } else {
            formName = fileName.substring(0, fileName.indexOf('_', fileName.indexOf('_') + 1));
        }

        return formName;
    }

    private void changeReportFiles(List<String> reportPathes, Map<String, Map<String, String>> renames) {
        for (String reportPath : reportPathes) {
            String formName = extractFormNameFromPath(reportPath);
            if (!renames.containsKey(formName)) {
                System.out.println(String.format("Form '%s' not found for path %s", formName, reportPath));
            } else {
                File reportFile = new File(reportPath);
                File tmpReportFile = new File(reportPath + "_tmp");
                try {
                    if (reportFile.renameTo(tmpReportFile) && reportFile.createNewFile()) {
                        changeReportFile(tmpReportFile, reportFile, renames.get(formName), formName);
                    } else {
                        System.out.println("!!!!!!!!!!ERROR: renaming (or creating tmp) " + reportPath + " failed, form" + formName);
                    }
                } catch (IOException e) {
                    System.out.println("!!!!!!!!!!!IO ERROR!!!!!!!!!! " + reportPath);
                } finally {
                    tmpReportFile.delete();
                }
            }
        }
    }

    private static String convertString(String str, String regex, int nameGroupNumber, Map<String, String> formRenames, String formName) {
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(str);
        StringBuilder convertedStr = new StringBuilder();
        int findStart = 0;
        while (findStart < str.length() && m.find(findStart)) {
            int startGroup = m.start(nameGroupNumber);
            int endGroup = m.end(nameGroupNumber);
            String oldName = m.group(nameGroupNumber);
            convertedStr.append(str.substring(findStart, startGroup));
            if (!formRenames.containsKey(oldName)) {
                System.out.println("Old propertyDraw name (" + oldName + ") not found? form " + formName);
                convertedStr.append(oldName);
            } else {
                convertedStr.append(formRenames.get(oldName));
            }
            convertedStr.append(str.substring(endGroup, m.end()));
            findStart = m.end();
        }
        if (findStart < str.length()) {
            convertedStr.append(str.substring(findStart));
        }
        return convertedStr.toString();
    }
    
    private void changeReportFile(File input, File output, Map<String, String> formRenames, String formName) throws IOException {
        FileInputStream stream = new FileInputStream(input);
        FileOutputStream ostream = new FileOutputStream(output);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF8"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ostream, "UTF8"));
        String fieldNameExpr = "(\\w+)(\\(caption\\)|\\(footer\\))?";
        String str;

        while ((str = reader.readLine()) != null) {
            // <field name="c"
            str = convertString(str, "(?i)<\\s*field \\s*name\\s*=\"" + fieldNameExpr + "\"", 1, formRenames, formName);
            //$F{objectValueProperty_DateClass}
            str = convertString(str, "\\$F\\s*\\{\\s*" + fieldNameExpr + "\\s*\\}", 1, formRenames, formName);
            writer.write(str + "\n");
        }
        reader.close();
        writer.close();
    }

    private void findFiles(VirtualFile file, Collection<String> foundPathes, String ext) {
        if (file.isDirectory()) {
            for (VirtualFile childFile : file.getChildren()) {
                findFiles(childFile, foundPathes, ext);
            }
        } else {
            if (ext.equals(file.getExtension())) {
                foundPathes.add(file.getCanonicalPath());
            }
        }
    }

    private ArrayList<String> findAllReportPathes(VirtualFile baseDir) {
        ArrayList<String> reportPathes = new ArrayList<String>();
        findFiles(baseDir, reportPathes, "jrxml");
        return reportPathes;
    }

    public void renameAll(final Project project, final Map<String, Map<String, String>> renames) {
        if (project == null) return;
        VirtualFile baseDir = project.getBaseDir();

        List<String> reportPathes = findAllReportPathes(baseDir);

        changeReportFiles(reportPathes, renames);
    } 
    
}
