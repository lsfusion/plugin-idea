package com.lsfusion.lang.meta;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.psi.LSFTypes;
import com.lsfusion.lang.psi.Result;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MetaCodeFragment {
    public List<String> parameters;
    public List<String> tokens;
    public List<IElementType> types;

    public Set<String> metaParameters;

    private char QUOTE = '\'';

    public MetaCodeFragment(List<String> params, Set<String> metaParameters, List<Pair<String, IElementType>> tokenTypes) {
        this.parameters = params;
        this.metaParameters = metaParameters;
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
            if(tokenType == LSFTypes.LEX_STRING_LITERAL || tokenType == LSFTypes.ID)
                token = transformToken(params, tokens.get(i));
            newTokens.add(token);
        }
        return newTokens;
    }

    public static String getNewCode(List<String> newTokens) {
        StringBuilder transformedCode = new StringBuilder();
        for (String newToken : newTokens) transformedCode.append(newToken);
        return transformedCode.toString();
    }

    private String transformParamToken(List<MetaTransaction.InToken> actualParams, String token) {
        int index = parameters.indexOf(token);
        return index >= 0 && index < actualParams.size() ? actualParams.get(index).text : token;
    }
    // lex
    private boolean assertOnlyFirstCanBeEmpty(String[] parts) {
        for(int i = 1; i < parts.length; i++)
            assert !parts[i].isEmpty() && !parts[i].equals("#");
        return true;
    }
    private String[] splitToken(String token) {
        String[] parts = token.split("##");
        assert assertOnlyFirstCanBeEmpty(parts);
        return parts;
    }
    private String[] transformParamTokens(List<MetaTransaction.InToken> actualParams, String token, Result<Boolean> rCapitalizeFirstToken, Result<Boolean> rIsColor) {
        List<String> result = new ArrayList<>();
        String[] parts = splitToken(token);

        boolean capitalizeFirstToken = false;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if(part.isEmpty()) {
                assert i == 0;
                capitalizeFirstToken = true; // ###id
            } else {
                boolean capitalize = false;
                if (part.startsWith("#")) {
                    assert i > 0; // cannot be null
                    part = part.substring(1);
                    capitalize = true;
                }
                String transformedToken = transformParamToken(actualParams, part);
                if (!transformedToken.isEmpty()) {
                    if (capitalize && !transformedToken.startsWith("#")) // can be ###a and we don't want to capitalize it one more time
                        transformedToken = "#" + transformedToken;

                    String[] transformedSplit = splitToken(transformedToken);
                    if(parts.length == 1 && transformedSplit.length == 1 && transformedSplit[0].startsWith("#") && !transformedSplit[0].startsWith("##"))
                        rIsColor.setResult(true);

                    for (int j = 0; j < transformedSplit.length; j++) {
                        String transPart = transformedSplit[j];
                        if (transPart.isEmpty()) {
                            assert j == 0;
                            if(i == 0)
                                capitalizeFirstToken = true;
                        } else
                            result.add(transPart);
                    }
                }
            }
        }
        rCapitalizeFirstToken.setResult(capitalizeFirstToken);
        return result.toArray(new String[0]);
    }

    private void transformTokenPartString(StringBuilder resultString, String[] parts, int from, int to, boolean capitalizeFirstToken, boolean isColor) {
        if(to > from) {
            boolean isStringLiteral = false;
            String result = "";
            for (int i = from; i < to; i++) {
                String part = parts[i];

                boolean capitalize = false;
                if (part.startsWith("#") && !isColor) {
                    capitalize = i > 0 || capitalizeFirstToken; // when there is ###param, forcing its capitalization
                    part = part.substring(1);
                }

                if (part.charAt(0) == QUOTE) {
                    isStringLiteral = true;
                    part = unquote(part);
                }

                if (capitalize)
                    part = StringUtils.capitalize(part);

                result += part;
            }

            addPart(resultString, isStringLiteral ? QUOTE + result + QUOTE : result);
        }
    }
    private void addPart(StringBuilder builder, String part) {
        assert !part.isEmpty();

        if(builder.length() > 0)
            builder.append("##");
        builder.append(part);
    }

    private String transformToken(List<MetaTransaction.InToken> actualParams, String token) {
        if(!token.contains("##") && !parameters.contains(token)) // optimization;
            return token;

        Result<Boolean> capitalizeFirstToken = new Result<>(false);
        Result<Boolean> isColorToken = new Result<>(false);
        String[] parts = transformParamTokens(actualParams, token, capitalizeFirstToken, isColorToken);

        StringBuilder result = new StringBuilder();
        int from = 0;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            String actualPart = part.startsWith("#") ? part.substring(1) : part;
            if(metaParameters.contains(actualPart)) {
                transformTokenPartString(result, parts, from, i, capitalizeFirstToken.getResult(), isColorToken.getResult()); // transforming all parts till this parameter
                if(i == 0) {
                    if(capitalizeFirstToken.getResult()) // forcing ### at the start
                        result.append("#");
//                    if(!isColorToken.getResult()) // not sure that this check is needed (since color tokens, are not capitalized, prefixed, etc.), so if this is a meta parameter it will have no # prefixes
                    part = actualPart;
                }
                addPart(result, part);
                from = i + 1;
            }
        }
        transformTokenPartString(result, parts, from, parts.length, capitalizeFirstToken.getResult(), isColorToken.getResult()); // transforming all parts till this parameter
        return result.toString();
    }

    private String unquote(String s) {
        if (s.length() >= 2 && s.charAt(0) == QUOTE && s.charAt(s.length()-1) == QUOTE) {
            s = s.substring(1, s.length()-1);
        }
        return s;
    }

}
