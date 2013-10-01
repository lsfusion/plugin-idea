package com.simpleplugin.meta;

import com.intellij.lang.ASTNode;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.simpleplugin.BaseUtils;
import com.simpleplugin.LSFElementGenerator;
import com.simpleplugin.LSFParserDefinition;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class MetaTransaction {
    
    public abstract static class Change {
        
        abstract void apply(List<InToken> newTokens, List<Collection<List<ExtToken>>> oldTokens, List<InToken> usageParams, List<String> declParams);
        
        protected static Integer adjustOffset(List<InToken> newTokens, int before) {
            int at=0;
            int r = 0;
            while(true) {
                if(before <= at) {
                    if(before < at) // значит в середину пытается вставить
                        return null;
                    return r;
                }
                at += newTokens.get(r).tokCount;
                r++;
            }
        }
        
    }
    
    private static List<ExtToken> finishToken(List<ExtToken> current, String currToken) {
        if(!currToken.isEmpty())
            return BaseUtils.add(current, new ExtToken(currToken, false));
        return current;
    }
    
    private static void recDeconcatToken(String token, int i, List<InToken> usageParams, List<String> declParams, List<ExtToken> current, String currToken, boolean first, Set<String> usedEmptyDecls, Collection<List<ExtToken>> result) {
        if(i >= token.length()) {
            result.add(finishToken(current, currToken));
            return;
        }
        
        for(int j=0;j<usageParams.size();j++) {
            String usageParam = usageParams.get(j).text;
            for(int k=0;k<2;k++) {
                boolean capitalized = k==1;
                if(capitalized)
                    usageParam = StringUtils.capitalize(usageParam);
                if(token.startsWith(usageParam, i)) { // парсим, будем считать что usageParam больше одного раза вставлять не будем
                    String declParam = declParams.get(j);
                    if(usageParam.isEmpty())
                        if(!usedEmptyDecls.add(declParam))
                            continue;
                    List<ExtToken> addTokens = new ArrayList<ExtToken>();
                    if(capitalized || !first)
                        addTokens.add(new ExtToken(capitalized ? "###" : "##", true));
                    addTokens.add(new ExtToken(declParam, true));
                    recDeconcatToken(token, i + usageParam.length(), usageParams, declParams, BaseUtils.add(finishToken(current, currToken), addTokens), "", false, usedEmptyDecls, result);
                    if(usageParam.isEmpty())
                        usedEmptyDecls.remove(declParam);
                }
            }
        }
        
        // не парсим
        recDeconcatToken(token, i + 1, usageParams, declParams, !first && currToken.isEmpty() ? BaseUtils.add(current, new ExtToken("##", true)) : current, currToken + token.charAt(i), false, usedEmptyDecls, result);
    }

    private static Collection<List<ExtToken>> deconcatToken(String token, List<InToken> usageParams, List<String> declParams) {
        Collection<List<ExtToken>> result = new ArrayList<List<ExtToken>>();
        recDeconcatToken(token, 0, usageParams, declParams, new ArrayList<ExtToken>(), "", true, new HashSet<String>(), result);
        return result;
    }

    public static class AddTokenChange extends Change {
        private final int before;
        private final List<Pair<InToken, IElementType>> tokens;

        public AddTokenChange(int before, List<Pair<InToken, IElementType>> tokens) {
            this.before = before;
            this.tokens = tokens;
        }

        void apply(List<InToken> newTokens, List<Collection<List<ExtToken>>> oldTokens, List<InToken> usageParams, List<String> declParams) {
            List<InToken> original = new ArrayList<InToken>();
            List<Collection<List<ExtToken>>> deconc = new ArrayList<Collection<List<ExtToken>>>();
            for(Pair<InToken, IElementType> token : tokens) { // нужно попытаться "разрезать", преобразовать назад
                original.add(token.first);
                if(LSFParserDefinition.isWhiteSpace(token.second)) // оптимизация
                    deconc.add(Collections.singleton(Collections.singletonList(new ExtToken(token.first.text, false))));
                else
                    deconc.add(deconcatToken(token.first.text, usageParams, declParams));
            }
            
            int adjBefore = adjustOffset(newTokens, before);            
            newTokens.addAll(adjBefore, original);
            oldTokens.addAll(adjBefore, deconc);
        }
    }

//    public static class ReplaceTokenChange extends Change {
//        private int before; // номер в списке, до добавления табуляций и без развернутого мета кода (последний может приводить к внутренним транзакциям)
//        private String token;
//    }

    private static class SingleTransaction {
        public List<Change> changes = new ArrayList<Change>();
    }
    
    
    
    private final Map<LSFMetaCodeStatement, SingleTransaction> transactions = new HashMap<LSFMetaCodeStatement, SingleTransaction>();

    private static int leafCount(ASTNode node) {
        if(node instanceof LeafElement) {
            return 1;
        }

        ASTNode child = node.getFirstChildNode();
        int result = 0;
        while (child != null) {
            ASTNode nextChild = child.getTreeNext();
            result += leafCount(child);
            child = nextChild;
        }
        return result;
    }

    private static void recMetaTokens(ASTNode node, List<LeafElement> result, boolean first) {
        if(node instanceof LeafElement) {
            result.add((LeafElement) node);
            return;
        }
        if(!first && node.getPsi() instanceof LSFMetaCodeBody) // мета коды не интересуют
            return;

        ASTNode child = node.getFirstChildNode();
        boolean firstChild = true; // отрежем скобки слева и справа
        while (child != null) {
            ASTNode nextChild = child.getTreeNext();
            if(!(first && (firstChild || nextChild == null))) // первое и последнее выкидываем
                recMetaTokens(child, result, false);
            
            firstChild = false;
            child = nextChild;
        }
    }
    
    private static class Option<K extends Comparable<K>> implements Comparable<Option<K>> {
        private final List<K> tokens;
        private final int ift;
        private final int isd;

        private Option(List<K> tokens, int ift, int isd) {
            this.tokens = tokens;
            this.ift = ift;
            this.isd = isd;
        }

        public int compareI(Option<K> o) {
            if(ift > o.ift)
                return 1;
            if(ift < o.ift)
                return -1;

            if(isd > o.isd)
                return 1;
            if(isd < o.isd)
                return -1;

            return 0;
        }
        @Override
        public int compareTo(Option<K> o) {
            int cmpI = compareI(o);
            if(cmpI != 0)
                return cmpI;
            
            if(tokens.size() > o.tokens.size())
                return 1;
            
            if(tokens.size() < o.tokens.size())
                return -1;
            
            for(int i=0;i<tokens.size();i++) {
                int tokComp = tokens.get(i).compareTo(o.tokens.get(i));
                if(tokComp != 0)
                    return tokComp;
            }
            return 0;
        }
    }

    private static <K extends Comparable<K>> void findNextOpts(List<K> needed, boolean needSd, List<Collection<List<K>>> first, List<Collection<List<K>>> second, int ift, int isd, List<K> commonTokens, TreeSet<Option<K>> opts) {
        if(needSd) {
            if(isd >= second.size()) // не подходит вариант
                return;
        } else {
            if(ift >= first.size()) // не подходит вариант
                return;
        }
        
        Collection<List<K>> nextOpts = (needSd ? second.get(isd) : first.get(ift));
        for(List<K> nextOpt : nextOpts) {
            boolean notEquals = false;
            for(int i=0,size=BaseUtils.min(needed.size(), nextOpt.size());i<size;i++)
                if(!nextOpt.get(i).equals(needed.get(i))) {
                    notEquals = true;
                    break;
                }
            if(notEquals) // не подходит вариант
                continue;
            
            if(nextOpt.size() == needed.size()) { // подошел
                opts.add(new Option<K>(BaseUtils.add(commonTokens, needed), needSd ? ift : ift + 1, needSd ? isd + 1 : isd));
            } else {
                if(nextOpt.size() > needed.size()) { // ищем в противоположном списке
                    findNextOpts(nextOpt.subList(needed.size(), nextOpt.size()), !needSd, first, second, needSd ? ift : ift + 1, needSd ? isd + 1 : isd, BaseUtils.add(commonTokens, needed), opts);
                } else // ищем в том же
                    findNextOpts(needed.subList(nextOpt.size(), needed.size()), needSd, first, second, needSd ? ift + 1 : ift, needSd ? isd : isd + 1, BaseUtils.add(commonTokens, nextOpt), opts);
            }
        }
    }

    private static <K extends Comparable<K>> List<Collection<List<K>>> merge(List<Collection<List<K>>> first, List<Collection<List<K>>> second, Result<Pair<String, String>> error) {
        List<Collection<List<K>>> result = new ArrayList<Collection<List<K>>>();
        
        Option<K> et = new Option<K>(new ArrayList<K>(), 0, 0);
        
        TreeSet<Option<K>> opts = new TreeSet<Option<K>>();
        while(true) {
            if(et.ift < first.size() && first.get(et.ift).size() == 1 && et.isd < second.size() && second.get(et.isd).size() == 1) { // оптимизация
                List<K> sf = BaseUtils.single(first.get(et.ift));
                List<K> ss = BaseUtils.single(first.get(et.ift));
                if(sf.equals(ss))
                    opts.add(new Option(BaseUtils.add(et.tokens, sf), et.ift + 1, et.isd + 1));
                else {
                    error.setResult(new Pair<String, String>("", ""));
                    return null; // diff
                }
            } else
                findNextOpts(new ArrayList<K>(), false, first, second, et.ift, et.isd, et.tokens, opts);
    
            if(opts.isEmpty()) {
                if(et.ift == first.size() && et.isd == second.size()) // все совпало
                    return result;
                // 10 токенов влево, 10 токенов вправо
                error.setResult(new Pair<String, String>("", ""));
                return null; // diff
            }

            // берем элемент с наименьшими fst, second
            Iterator<Option<K>> ito = opts.iterator();
            et = ito.next();
            ito.remove();
        
            boolean hasDiff = false;
            Collection<List<K>> optTokens = new ArrayList<List<K>>();
            while(ito.hasNext()) {
                Option<K> next = ito.next();
                if(et.compareI(next) == 0)
                    optTokens.add(next.tokens);
                else {
                    hasDiff = true;
                    break;
                }
            }
        
            if(!hasDiff) {
                optTokens.add(et.tokens);
                result.add(optTokens);
                et = new Option<K>(new ArrayList<K>(), et.ift, et.isd);
                opts = new TreeSet<Option<K>>(); // обнуляем варианты
            }
        }
    }
    
    private static List<LeafElement> getMetaTokens(LSFMetaCodeBody body) {
        List<LeafElement> result = new ArrayList<LeafElement>();
        recMetaTokens(body.getNode(), result, true);
        return result;
    }

    public static class InToken {
        public final String text;
        public final int tokCount;

        public InToken(String text, int tokCount) {
            this.text = text;
            this.tokCount = tokCount;
        }

        @Override
        public String toString() {
            return super.toString();    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
    
    public static InToken parseToken(PsiElement element) {
        return parseToken(element.getNode());
    }

    public static InToken parseToken(ASTNode node) {
        int tokCount = leafCount(node);
        return new InToken(node.getText(), tokCount);
    }

    public static class ExtToken implements Comparable<ExtToken> {
        private final String text;
        private final boolean param;

        public ExtToken(String text, boolean param) {
            this.text = text;
            this.param = param;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof ExtToken && param == ((ExtToken) o).param && text.equals(((ExtToken) o).text);
        }

        @Override
        public int hashCode() {
            return 31 * text.hashCode() + (param ? 1 : 0);
        }

        @Override
        public int compareTo(ExtToken o) {
            int textCmp = text.compareTo(o.text);
            if(textCmp != 0)
                return textCmp;
            if(param == o.param)
                return 0;
            if(param)
                return 1;
            return -1;
        }
    }

    public void apply() { // группируем metaCodeStatement'ы по decl'ам
        Set<LSFMetaDeclaration> declChanged = new HashSet<LSFMetaDeclaration>();
        for(LSFMetaCodeStatement usageChanged : transactions.keySet()) {
            LSFMetaDeclaration metaDecl = usageChanged.resolveDecl();
            if(metaDecl != null) {
                declChanged.add(metaDecl);
            }                
        }
        for(LSFMetaDeclaration decl : declChanged) {
            // для meta Decl генерим мета-код, "разбавляем" его табуляциями и строим "обратное" отображение - изменения в базовом ASTNode
            List<Pair<String, IElementType>> texts = decl.getMetaCode();
            List<String> declParams = decl.getDeclParams();
            
            LSFMetaCodeStatement resultStatement = null;
            List<Collection<List<ExtToken>>> result = null;
            
            for(LSFMetaCodeStatement statement : LSFResolver.findMetaUsages(decl)) {
                if(statement != null) {
                    SingleTransaction transaction = transactions.get(statement);
                    if(transaction != null) {
                        List<List<ExtToken>> oldTokens = new ArrayList<List<ExtToken>>();
                        List<InToken> usageParams = statement.getUsageParams();
                        List<InToken> newTokens = MetaChangeDetector.getNewTokens(texts, usageParams, declParams, oldTokens);

                        List<Collection<List<ExtToken>>> oldColTokens = new ArrayList<Collection<List<ExtToken>>>();
                        for(List<ExtToken> oldToken : oldTokens)
                            oldColTokens.add(Collections.singleton(oldToken));

                        for(Change change : transaction.changes)
                            change.apply(newTokens, oldColTokens, usageParams, declParams);
                        
                        if(result == null) {
                            result = oldColTokens;
                            resultStatement = statement;
                        } else {
                            Result<Pair<String, String>> error = new Result<Pair<String, String>>();
                            result = merge(result, oldColTokens, error);
                            if(result==null) {
                                Notifications.Bus.notify(new Notification("diffMeta", "Diff meta", decl.getGlobalName() + "\nFirst :" + usageParams + " " + error.getResult().first + "\nSecond :" + resultStatement.getUsageParams() + " "  + error.getResult().second, NotificationType.ERROR));
                                break;
                            }
                        }
                    }
                }
            }
            
            if(result!=null) {
                StringBuilder transformedCode = new StringBuilder();
                for (Collection<List<ExtToken>> opts : result) {
                    
                    int minNotParsed = Integer.MAX_VALUE;
                    List<ExtToken> bestNotParsed = null;
                    for(List<ExtToken> opt : opts) { // выберем лучший вариант
                        int notParsed = 0;
                        for(ExtToken token : opt)
                            if(!token.param)
                                notParsed += token.text.length();
                        notParsed = notParsed * 1000 + opt.size();
                        if(notParsed < minNotParsed) {
                            minNotParsed = notParsed;
                            bestNotParsed = opt; 
                        }                                
                    }
                    
                    for(ExtToken token : bestNotParsed)
                        transformedCode.append(token.text);
                }
                decl.setBody(LSFElementGenerator.createMetaCodeFromText(decl.getProject(), transformedCode.toString()));
            }
        }
    }
    
    public void regAddChange(List<ASTNode> add, ASTNode before) {
        PsiElement psi = before.getPsi();
        LeafElement leafBefore = (LeafElement) before.findLeafElementAt(0);

        LSFMetaCodeStatement metaCodeStatement = PsiTreeUtil.getParentOfType(psi, LSFMetaCodeStatement.class);
        if(metaCodeStatement != null) { // смотрим может изменение в мета коде
            SingleTransaction metaTransaction = transactions.get(metaCodeStatement);
            if(metaTransaction == null) {
                metaTransaction = new SingleTransaction();
                transactions.put(metaCodeStatement, metaTransaction);
            }
            List<LeafElement> tokens = getMetaTokens(metaCodeStatement.getMetaCodeBody());
            
            List<Pair<InToken, IElementType>> addPairs = new ArrayList<Pair<InToken, IElementType>>();
            for(ASTNode addNode : add)
                addPairs.add(new Pair<InToken, IElementType>(parseToken(addNode), addNode.getElementType()));
            
            metaTransaction.changes.add(new AddTokenChange(tokens.indexOf(leafBefore), addPairs));
        }
    }
}
