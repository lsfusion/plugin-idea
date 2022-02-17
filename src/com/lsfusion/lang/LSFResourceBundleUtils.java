package com.lsfusion.lang;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.lang.properties.PropertiesReferenceManager;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.util.LSFFileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LSFResourceBundleUtils {

    private static final String googleTranslateApiKey = "googleTranslateApiKey";

    private static final Map<GlobalSearchScope, ScopeData> scopeDataMap = new HashMap<>();
    private static final Map<GlobalSearchScope, String> lsfStrLiteralsLanguageMap = new HashMap<>();

    private static final Map<String, Map<String, String>> propertiesOrdinaryMap = new HashMap<>();
    private static final Map<String, Map<String, String>> propertiesReverseMap = new HashMap<>();

    public static ScopeData getScopeData(Module module) {
        if(module != null) {
            GlobalSearchScope scope = module.getModuleScope();
            ScopeData scopeData = scopeDataMap.get(scope);
            if (scopeData == null) {

                Set<VirtualFile> resourceBundleFiles = new HashSet<>();
                Set<String> resourceBundleNames = new HashSet<>();
                Pattern pattern = Pattern.compile("([^/]*ResourceBundle)\\.properties");
                for (VirtualFile file : FileTypeIndex.getFiles(PropertiesFileType.INSTANCE, scope)) {
                    if (file.isWritable()) {
                        Matcher matcher = pattern.matcher(file.getName());
                        if (matcher.matches()) {
                            resourceBundleFiles.add(file);
                            resourceBundleNames.add(matcher.group(1));
                        }
                    }
                }

                Map<String, Map<String, PropertiesFile>> propertiesFilesMap = new HashMap<>();

                for (String resourceBundleName : resourceBundleNames) {
                    PropertiesReferenceManager refManager = PropertiesReferenceManager.getInstance(module.getProject());

                    for (PropertiesFile propertiesFile : refManager.findPropertiesFiles(module, resourceBundleName)) {
                        Map<String, PropertiesFile> propFiles = propertiesFilesMap.getOrDefault(resourceBundleName, new HashMap<>());
                        String language = propertiesFile.getLocale().getLanguage();
                        propFiles.put(language.isEmpty() ? "default" : language, propertiesFile);
                        propertiesFilesMap.put(resourceBundleName, propFiles);

                        try {
                            updateResourceBundleMaps(propertiesFile.getVirtualFile());
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to read ResourceBundle", e);
                        }
                    }
                }
                scopeData = new ScopeData(resourceBundleFiles, propertiesFilesMap);
                scopeDataMap.put(scope, scopeData);
            }
            return scopeData;
        } else return null;
    }

    public static String getLsfStrLiteralsLanguage(Module module, boolean noCache) {
        try {
            GlobalSearchScope scope = module.getModuleScope();
            String lsfStrLiteralsLanguage = noCache ? null : lsfStrLiteralsLanguageMap.get(scope);
            if (lsfStrLiteralsLanguage == null) {
                lsfStrLiteralsLanguage = LSFFileUtils.getLsfStrLiteralsLanguage(LSFFileUtils.findFilesByPath(module, "lsfusion.properties"));
                lsfStrLiteralsLanguageMap.put(scope, lsfStrLiteralsLanguage);
            }
            return lsfStrLiteralsLanguage;
        } catch (Throwable t) {
            return null;
        }
    }

    public static void setLsfStrLiteralsLanguage(GlobalSearchScope scope, String value) {
        lsfStrLiteralsLanguageMap.put(scope, value);
    }

    public static void updateResourceBundleMaps(VirtualFile file) throws IOException {
        String path = file.getPath();
        ResourceBundle resourceBundle = readResourceBundle(path);

        Map<String, String> ordinaryMap = new HashMap<>();
        Map<String, String> reverseMap = new HashMap<>();
        for (String key : resourceBundle.keySet()) {
            String value = resourceBundle.getString(key);
            ordinaryMap.put(key, value);
            reverseMap.put(value, key);
        }
        propertiesOrdinaryMap.put(path, ordinaryMap);
        propertiesReverseMap.put(path, reverseMap);
    }

    public static void updateFile(Project project, VirtualFile file) {
        try {
            if (isLsfusionProperties(file.getName())) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                if (psiFile != null) {
                    Module module = ModuleUtil.findModuleForPsiElement(psiFile);
                    if (module != null) {
                        getLsfStrLiteralsLanguage(module, true);
                    }
                }
            } else if (isResourceBundle(file.getName())) {
                updateResourceBundleMaps(file);
            }
        } catch (IOException ignored) {
        }
    }

    private static boolean isLsfusionProperties(String fileName) {
        return fileName.matches("lsfusion\\.properties");
    }

    private static boolean isResourceBundle(String fileName) {
        return fileName.matches("([^/]*ResourceBundle)(?:_(.*))?\\.properties");
    }

    private static ResourceBundle readResourceBundle(String path) throws IOException {
        return new PropertyResourceBundle(new FileInputStream(path));
    }

    public static Map<String, String> getOrdinaryMap(String path) {
        return propertiesOrdinaryMap.get(path);
    }

    public static String getOrdinaryMapValue(String path, String key) {
        return propertiesOrdinaryMap.get(path).get(key);
    }

    public static String getReverseMapValue(String path, String key) {
        return propertiesReverseMap.get(path).get(key);
    }

    public static class ScopeData {
        public Set<VirtualFile> resourceBundleFiles;
        //resourceBundle name -> files (MyCompanyResourceBundle -> MyCompanyResourceBundle_en.properties, MyCompanyResourceBundle_ru.properties)
        public Map<String, Map<String, PropertiesFile>> propertiesFiles;

        public ScopeData(Set<VirtualFile> resourceBundleFiles, Map<String, Map<String, PropertiesFile>> propertiesFiles) {
            this.resourceBundleFiles = resourceBundleFiles;
            this.propertiesFiles = propertiesFiles;
        }
    }

    //copy from lsfusion.server.physics.dev.i18n.ResourceBundleGenerator
    private static final String russianAlphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final String[] transliteration = {"a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "i", "k", "l", "m",
            "n", "o", "p", "r", "s", "t", "u", "f", "kh", "ts", "ch", "sh", "shch", "ie", "y", "", "e", "iu", "ia"};

    private static final Map<Character, String> transliterationMap = new HashMap<>();
    static {
        for (int i = 0; i < russianAlphabet.length(); ++i) {
            transliterationMap.put(russianAlphabet.charAt(i), transliteration[i]);
        }
    }

    public static String getDefaultBundleKey(String key) {
        StringBuilder builder = new StringBuilder();
        boolean isStart = true;
        for (int i = 0; i < key.length(); ++i) {
            char ch = key.charAt(i);
            if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z'|| ch == '_' || Character.isDigit(ch) && builder.length() > 0) {
                builder.append(ch);
                isStart = false;
            } else if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.CYRILLIC)) {
                String tranliterated = transliterationMap.get(Character.toLowerCase(ch));
                if (Character.isUpperCase(ch)) {
                    tranliterated = tranliterated.toUpperCase();
                }
                builder.append(tranliterated);
                isStart = false;
            } else if (!isStart) {
                isStart = true;
                builder.append('_');
            }
        }
        return builder.toString();
    }

    public static String getDefaultBundleValue(String value, String currentLang, String targetLang) {
        String apiKey = getGoogleTranslateApiKey();
        if (apiKey != null && !targetLang.isEmpty() && !currentLang.equals(targetLang)) {
            try {
                HttpPost httpPost = new HttpPost("https://translation.googleapis.com/language/translate/v2?key=" + apiKey);
                httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
                httpPost.setEntity(new StringEntity(String.format("{\"q\":\"%s\", \"source\": \"%s\", \"target\":\"%s\"}", value, currentLang, targetLang), StandardCharsets.UTF_8));
                HttpEntity fileEntity = HttpClients.createDefault().execute(httpPost).getEntity();
                if (fileEntity != null) {
                    Object root = new JSONTokener(new InputStreamReader(fileEntity.getContent(), StandardCharsets.UTF_8)).nextValue();
                    if (root instanceof JSONObject) {
                        JSONObject data = ((JSONObject) root).optJSONObject("data");
                        if (data != null) {
                            return data.getJSONArray("translations").getJSONObject(0).getString("translatedText");
                        }
                    }
                }
            } catch (IOException ignored) {
            }
        }
        return value;
    }

    public static String getGoogleTranslateApiKey() {
        return PropertiesComponent.getInstance().getValue(googleTranslateApiKey);
    }

    public static void setGoogleTranslateApiKey(String value) {
        PropertiesComponent.getInstance().setValue(googleTranslateApiKey, value);
    }

}
