package com.lsfusion.lang.meta;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.psi.LSFTypes;
import com.lsfusion.lang.psi.Result;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MetaCodeFragment {
    public List<String> parameters;
    public List<String> tokens;
    public List<IElementType> types;

    private char QUOTE = '\'';

    public MetaCodeFragment(List<String> params, List<Pair<String, IElementType>> tokenTypes) {
        this.parameters = params;
        tokens = new ArrayList<>();
        types = new ArrayList<>();
        for(Pair<String, IElementType> tokenType : tokenTypes) {
            tokens.add(tokenType.first);
            types.add(tokenType.second);
        }
    }

    public String getCode(List<MetaTransaction.InToken> params) {
        List<String> newTokens = getNewTokens(params, null);
        return getNewCode(newTokens);
    }
    
    public int mapOffset(int offset, List<MetaTransaction.InToken> params) {
        List<String> newTokens = getNewTokens(params, null);
        assert newTokens.size() == tokens.size();
        int mapOffset = 0;
        for(int i=0;i<newTokens.size();i++) {
            offset = offset - newTokens.get(i).length();

            if(offset < 0)
                return mapOffset;

            mapOffset = mapOffset + tokens.get(i).length();
        }
        
        return mapOffset;
    }

    public List<String> getNewTokens(List<MetaTransaction.InToken> params, List<MetaTransaction.ExtToken> oldTokens) {
        List<String> newTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            IElementType tokenType = types.get(i);
            boolean isMeta = tokenType == LSFTypes.FAKEMETAID;
            Result<Boolean> transformed = new Result<>();
            if(isMeta || tokenType == LSFTypes.ID) // ID also can be meta parameter
                token = transformToken(params, tokens.get(i), isMeta, transformed);
            newTokens.add(token);
        }
        return newTokens;
    }

    public static String getNewCode(List<String> newTokens) {
        StringBuilder transformedCode = new StringBuilder();
        for (String newToken : newTokens) transformedCode.append(newToken);
        return transformedCode.toString();
    }

    private String transformParamToken(List<MetaTransaction.InToken> actualParams, String token, Result<Boolean> transformed) {
        int index = parameters.indexOf(token);
        if(index >= 0)
            transformed.setResult(true);
        return index >= 0 && actualParams.size() > index ? actualParams.get(index).text : token;
    }
    private String transformToken(List<MetaTransaction.InToken> actualParams, String token, boolean isMeta, Result<Boolean> transformed) {
        if(!isMeta) { // optimization;
            assert !token.contains("##");
            return transformParamToken(actualParams, token, transformed);
        }
        transformed.setResult(true);
        String[] parts = token.split("##");
        boolean isStringLiteral = false;
        String result = "";
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            boolean capitalize = false;
            if (part.startsWith("#")) {
                assert i > 0;
                capitalize = !result.isEmpty() || (i == 1 && parts[0].isEmpty()); // when there is ###param, forcing its capitalization
                part = part.substring(1);
            }

            part = transformParamToken(actualParams, part, transformed);

            if (!part.isEmpty() && part.charAt(0) == QUOTE) {
                isStringLiteral = true;
                part = unquote(part);
            }

            result += capitalize(part, capitalize);
        }

        if(isStringLiteral)
            result = QUOTE + result + QUOTE;

        return result;
    }

    private String unquote(String s) {
        if (s.length() >= 2 && s.charAt(0) == QUOTE && s.charAt(s.length()-1) == QUOTE) {
            s = s.substring(1, s.length()-1);
        }
        return s;
    }

    private String capitalize(String s, boolean toCapitalize) {
        if (toCapitalize && s.length() > 0) {
            s = StringUtils.capitalize(s);
        }
        return s;
    }
}
