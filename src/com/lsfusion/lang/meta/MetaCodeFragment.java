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
        tokens = new ArrayList<String>();
        types = new ArrayList<IElementType>();
        for(Pair<String, IElementType> tokenType : tokenTypes) {
            tokens.add(tokenType.first);
            types.add(tokenType.second);
        }
    }

    public String getCode(List<MetaTransaction.InToken> params) {
        List<MetaTransaction.InToken> newTokens = getNewTokens(params, null);
        return getNewCode(newTokens);
    }
    
    public int mapOffset(int offset, List<MetaTransaction.InToken> params) {
        List<List<MetaTransaction.ExtToken>> oldTokens = new ArrayList<List<MetaTransaction.ExtToken>>();
        List<MetaTransaction.InToken> newTokens = getNewTokens(params, oldTokens);
        int mapOffset = 0;
        int j=0;
        for(int i=0;i<newTokens.size();i++) {
            offset = offset - newTokens.get(i).text.length();

            if(offset < 0)
                return mapOffset;

            for(int k=0;k<oldTokens.get(i).size();k++) {
                mapOffset = mapOffset + tokens.get(j).length();
                j++;
            }
        }
        
        return mapOffset;
    }
    
    private static boolean isConcat(IElementType type) {
        return type == LSFTypes.ID || type == LSFTypes.LEX_STRING_LITERAL;
    }

    public List<MetaTransaction.InToken> getNewTokens(List<MetaTransaction.InToken> params, List<List<MetaTransaction.ExtToken>> oldTokens) {
        ArrayList<MetaTransaction.InToken> newTokens = new ArrayList<MetaTransaction.InToken>();
        List<IElementType> newTokTypes = new ArrayList<IElementType>();

        for (int i = 0; i < tokens.size(); i++) {
            Result<Boolean> transformed = new Result<Boolean>();
            MetaTransaction.InToken tokenStr = transformedToken(params, tokens.get(i), transformed);
            if (tokenStr.text.equals("##") || tokenStr.text.equals("###")) {
                if (!newTokens.isEmpty() && i+1 < tokens.size()) { // если не первый и не последний
                    MetaTransaction.InToken nextToken = transformedToken(params, tokens.get(i + 1), transformed);
                    if(isConcat(newTokTypes.get(newTokTypes.size()-1))) {
                        MetaTransaction.InToken lastToken = newTokens.get(newTokens.size() - 1);

                        newTokens.set(newTokens.size()-1, concatTokens(lastToken, nextToken, tokenStr.text.equals("###")));
                        if(oldTokens != null) {
                            List<MetaTransaction.ExtToken> prev = oldTokens.get(oldTokens.size() - 1);
                            prev.add(new MetaTransaction.ExtToken(tokens.get(i), true));
                            prev.add(new MetaTransaction.ExtToken(tokens.get(i+1), transformed.getResult()));
                        }
                    } else {
                        if(tokenStr.text.equals("###"))
                            nextToken = capToken(nextToken);
                        newTokens.add(nextToken);
                        newTokTypes.add(types.get(i+1)); 
                        if(oldTokens!=null) {
                            List<MetaTransaction.ExtToken> prev = new ArrayList<MetaTransaction.ExtToken>();
                            prev.add(new MetaTransaction.ExtToken(tokens.get(i), true));
                            prev.add(new MetaTransaction.ExtToken(tokens.get(i+1), transformed.getResult()));
                            oldTokens.add(prev);
                        }
                    }
                    ++i;
                }
            } else {
                newTokens.add(tokenStr);
                newTokTypes.add(types.get(i));
                if(oldTokens!=null) {
                    List<MetaTransaction.ExtToken> prev = new ArrayList<MetaTransaction.ExtToken>();
                    prev.add(new MetaTransaction.ExtToken(tokens.get(i), transformed.getResult()));
                    oldTokens.add(prev);
                }
            }
        }
        return newTokens;
    }

    public static String getNewCode(List<MetaTransaction.InToken> newTokens) {
        StringBuilder transformedCode = new StringBuilder();
        for (MetaTransaction.InToken newToken : newTokens) transformedCode.append(newToken.text);
        return transformedCode.toString();
    }

    public static String getOldCode(List<List<String>> oldTokens) {
        StringBuilder transformedCode = new StringBuilder();
        for (List<String> oldmTokens : oldTokens)
            for(String oldToken : oldmTokens)
                transformedCode.append(oldToken);
        return transformedCode.toString();
    }

    private MetaTransaction.InToken transformedToken(List<MetaTransaction.InToken> actualParams, String token, Result<Boolean> transformed) {
        int index = parameters.indexOf(token);
        if(index >= 0 && index < actualParams.size()) {
            transformed.setResult(true);
            return actualParams.get(index);
        }

        transformed.setResult(false);
        return new MetaTransaction.InToken(token, 1);
    }

    private MetaTransaction.InToken concatTokens(MetaTransaction.InToken t1, MetaTransaction.InToken t2, boolean toCapitalize) {
        if (t1.text.isEmpty() || t2.text.isEmpty()) {
            return new MetaTransaction.InToken(t1.text + capitalize(t2.text, toCapitalize && !t1.text.isEmpty()), t1.tokCount + t2.tokCount);
        } else if (t1.text.charAt(0) == QUOTE || t2.text.charAt(0) == QUOTE) {
            return new MetaTransaction.InToken(QUOTE + unquote(t1.text) + capitalize(unquote(t2.text), toCapitalize) + QUOTE, t1.tokCount + t2.tokCount - 1);
        } else {
            return new MetaTransaction.InToken(t1.text + capitalize(t2.text, toCapitalize), t1.tokCount + t2.tokCount - 1);
        }
    }
    
    private MetaTransaction.InToken capToken(MetaTransaction.InToken t) {
        if(!t.text.isEmpty() && t.text.charAt(0) == QUOTE)
            return new MetaTransaction.InToken(capitalize(unquote(t.text), true), t.tokCount);
        return new MetaTransaction.InToken(capitalize(t.text, true), t.tokCount);
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
