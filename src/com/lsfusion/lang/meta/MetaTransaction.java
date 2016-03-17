package com.lsfusion.lang.meta;

import com.intellij.lang.ASTNode;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFParserDefinition;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.util.BaseUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class MetaTransaction {
    public enum Type {
        REPLACE, BEFORE, AFTER
    }

    public static class Change {

        private final int token;
        private final Type type;
        private final List<Pair<InToken, IElementType>> tokens;

        public Change(int token, List<Pair<InToken, IElementType>> tokens, Type type) {
            this.token = token;
            this.tokens = tokens;
            this.type = type;
        }

        private static <T> void applyTokens(List<T> tokens, List<T> change, int before, int cnt) {
            for(int i=0;i<cnt;i++)
                tokens.remove(before);
            tokens.addAll(before, change);
        }

        void apply(List<InToken> newTokens, List<Collection<List<ExtToken>>> oldTokens, List<InToken> usageParams, List<String> declParams) {
            List<InToken> original = new ArrayList<>();
            List<Collection<List<ExtToken>>> deconc = deconc(tokens, usageParams, declParams, original);

            Integer adjBefore = adjustOffset(newTokens, token);
            if(adjBefore == null)
                adjBefore = adjBefore;
            if (type == Type.AFTER)
                adjBefore++;
            int cnt = type == Type.REPLACE  ? newTokens.get(adjBefore).tokCount : 0;
            applyTokens(newTokens, original, adjBefore, cnt);
            applyTokens(oldTokens, deconc, adjBefore, cnt);
        }

        protected static Integer adjustOffset(List<InToken> newTokens, int before) {
            int at=0;
            int r = 0;
            while(true) {
                if(before <= at) {
                    if(before < at) // значит в середину пытается вставить
                        return null;
                    return r;
                }
                if(r >= newTokens.size())
                    return null;
                at += newTokens.get(r).tokCount;
                r++;
            }
        }

        private static boolean isSpecSymbol(IElementType type) {
            return LSFParserDefinition.isWhiteSpace(type) || type == LSFTypes.LSQBR || type == LSFTypes.RSQBR || type == LSFTypes.COMMA || type == LSFTypes.POINT;
        }

        protected static List<Collection<List<ExtToken>>> deconc(List<Pair<InToken, IElementType>> tokens, List<InToken> usageParams, List<String> declParams, List<InToken> original) {
            List<Collection<List<ExtToken>>> deconc = new ArrayList<>();
            for(Pair<InToken, IElementType> token : tokens) { // нужно попытаться "разрезать", преобразовать назад
                original.add(token.first);
                if(isSpecSymbol(token.second)) // оптимизация
                    deconc.add(Collections.singleton(Collections.singletonList(new ExtToken(token.first.text, false))));
                else
                    deconc.add(deconcatToken(token.first.text, usageParams, declParams));
            }
            return deconc;
        }
    }

    private static List<ExtToken> finishToken(List<ExtToken> current, String currToken) {
        if(!currToken.isEmpty())
            return BaseUtils.add(current, new ExtToken(currToken, false));
        return current;
    }

    private static void recDeconcatToken(String token, int i, List<InToken> usageParams, List<String> declParams, List<ExtToken> current, String currToken, boolean first, Set<String> usedEmptyDecls, Collection<List<ExtToken>> result) {
        for(int j=0;j<usageParams.size();j++) {
            String usageParam = usageParams.get(j).text;
            for(int k=0;k<2;k++) {
                boolean capitalized = k==1;
                if(capitalized)
                    usageParam = StringUtils.capitalize(usageParam);
                if(token.startsWith(usageParam, i)) { // парсим, будем считать что usageParam больше одного раза вставлять не будем
                    String declParam = declParams.get(j);
                    if(usageParam.isEmpty()) {
                        if(usedEmptyDecls.size() >= 2)
                            continue;
                        if(i > 0 && i < token.length()) // оптимизация
                            if(!(!Character.isUpperCase(token.charAt(i-1)) && Character.isUpperCase(token.charAt(i))))
                                continue;
                        if(!usedEmptyDecls.add(declParam))
                            continue;
                    } else {
                        if(i==0 && !first) {
                            if(capitalized) // непонятно что делать
                                continue;
                            else { // немного поговнокодим, потому как ряд частных случаев, и обобщение будет громоздким
                                List<ExtToken> addTokens = new ArrayList<>();
                                addTokens.add(new ExtToken("###", true));
                                addTokens.add(new ExtToken(declParam, true));
                                recDeconcatToken(token, i + usageParam.length(), usageParams, declParams, BaseUtils.add(finishToken(current, currToken), addTokens), "", false, usedEmptyDecls, result);
                            }
                        }
                    }

                    List<ExtToken> addTokens = new ArrayList<>();
                    if(capitalized || !first)
                        addTokens.add(new ExtToken(capitalized ? "###" : "##", true));
                    addTokens.add(new ExtToken(declParam, true));
                    recDeconcatToken(token, i + usageParam.length(), usageParams, declParams, BaseUtils.add(finishToken(current, currToken), addTokens), "", false, usedEmptyDecls, result);

                    if(usageParam.isEmpty())
                        usedEmptyDecls.remove(declParam);
                }
            }
        }

        if(i >= token.length()) {
            result.add(finishToken(current, currToken));
            return;
        }

        // не парсим
        char charAt = token.charAt(i);
        if(!first && currToken.isEmpty()) {
            boolean upperChar = Character.isUpperCase(charAt);
            if((i==0)!=upperChar)
                recDeconcatToken(token, i + 1, usageParams, declParams, BaseUtils.add(current, new ExtToken("###", true)), currToken + Character.toLowerCase(charAt), false, usedEmptyDecls, result);
        }
        recDeconcatToken(token, i + 1, usageParams, declParams, !first && currToken.isEmpty() && charAt != '.' ? BaseUtils.add(current, new ExtToken("##", true)) : current, currToken + charAt, false, usedEmptyDecls, result);
    }

    private static Collection<List<ExtToken>> deconcatToken(String token, List<InToken> usageParams, List<String> declParams) {
        Collection<List<ExtToken>> result = new ArrayList<>();
        recDeconcatToken(token, 0, usageParams, declParams, new ArrayList<ExtToken>(), "", true, new HashSet<String>(), result);
        return result;
    }

//    public static class ReplaceTokenChange extends Change {
//        private int before; // номер в списке, до добавления табуляций и без развернутого мета кода (последний может приводить к внутренним транзакциям)
//        private String token;
//    }

    private static class SingleTransaction {
        public List<Change> changes = new ArrayList<>();
    }



    private final Map<LSFMetaCodeStatement, SingleTransaction> transactions = new HashMap<>();

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
    public static void recLeafTokens(ASTNode node, List<LeafElement> result) {
        if(node instanceof LeafElement) {
            result.add((LeafElement) node);
            return;
        }

        ASTNode child = node.getFirstChildNode();
        while (child != null) {
            ASTNode nextChild = child.getTreeNext();
            recLeafTokens(child, result);

            child = nextChild;
        }
    }
    public static List<ASTNode> getLeafTokens(ASTNode node) {
        List<LeafElement> leafElements = new ArrayList<>();
        recLeafTokens(node, leafElements);
        return BaseUtils.immutableCast(leafElements);
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
                opts.add(new Option<>(BaseUtils.add(commonTokens, needed), needSd ? ift : ift + 1, needSd ? isd + 1 : isd));
                if(opts.size() > 100000)
                    opts = opts;
            } else {
                if(nextOpt.size() > needed.size()) { // ищем в противоположном списке
                    findNextOpts(nextOpt.subList(needed.size(), nextOpt.size()), !needSd, first, second, needSd ? ift : ift + 1, needSd ? isd + 1 : isd, BaseUtils.add(commonTokens, needed), opts);
                } else // ищем в том же
                    findNextOpts(needed.subList(nextOpt.size(), needed.size()), needSd, first, second, needSd ? ift : ift + 1, needSd ? isd + 1 : isd, BaseUtils.add(commonTokens, nextOpt), opts);
            }
        }
    }

    private static <K extends Comparable<K>> List<Collection<List<K>>> merge(List<Collection<List<K>>> first, List<Collection<List<K>>> second, Result<Pair<String, String>> error) {
        List<Collection<List<K>>> result = new ArrayList<>();

        Option<K> et = new Option<>(new ArrayList<K>(), 0, 0);

        TreeSet<Option<K>> opts = new TreeSet<>();
        while(true) {
            if(et.ift < first.size() && first.get(et.ift).size() == 1 && et.isd < second.size() && second.get(et.isd).size() == 1) { // оптимизация
                List<K> sf = BaseUtils.single(first.get(et.ift));
                List<K> ss = BaseUtils.single(second.get(et.isd));
                if(sf.equals(ss))
                    opts.add(new Option(BaseUtils.add(et.tokens, sf), et.ift + 1, et.isd + 1));
                else {
                    error.setResult(Pair.create("", ""));
                    return null; // diff
                }
            } else
                findNextOpts(new ArrayList<K>(), false, first, second, et.ift, et.isd, et.tokens, opts);

            if(opts.isEmpty()) {
                if(et.ift == first.size() && et.isd == second.size()) // все совпало
                    return result;
                // 10 токенов влево, 10 токенов вправо
                error.setResult(Pair.create("", ""));
                return null; // diff
            }

            // берем элемент с наименьшими fst, second
            Iterator<Option<K>> ito = opts.iterator();
            et = ito.next();
            ito.remove();

            boolean hasDiff = false;
            Collection<List<K>> optTokens = new ArrayList<>();
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
                et = new Option<>(new ArrayList<K>(), et.ift, et.isd);
                opts = new TreeSet<>(); // обнуляем варианты
            }
        }
    }

    private static List<LeafElement> getMetaTokens(LSFMetaCodeBody body) {
        List<LeafElement> result = new ArrayList<>();
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
            return text + " (" + tokCount +  ")";
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
        public String toString() {
            return text;
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

    public static String toStringExt(List<ExtToken> extTokens) {
        String result = "";
        for(ExtToken extToken : extTokens)
            result += extToken.text;
        return result + " (" + extTokens.size() + ")";
    }
    public static List<String> toString(List<Collection<List<ExtToken>>> tokens) {
        List<String> result = new ArrayList<>();
        for(Collection<List<ExtToken>> token : tokens) {
            String concOpt = "";
            for(List<ExtToken> opt : token)
                concOpt = (concOpt.length()==0?"":concOpt + ",") + toStringExt(opt);
            result.add(token.size() == 1 ? concOpt : "[" + concOpt + "]");
        }
        return result;
    }

    public void apply() { // группируем metaCodeStatement'ы по decl'ам
        Set<LSFMetaDeclaration> declChanged = new HashSet<>();
        for(LSFMetaCodeStatement usageChanged : transactions.keySet()) {
            LSFMetaDeclaration metaDecl = usageChanged.resolveDecl();
            if(metaDecl != null) {
                declChanged.add(metaDecl);
            }
        }

        int current = 0;
        for(LSFMetaDeclaration decl : declChanged) {
            System.out.println("META " + ((double)current++)/declChanged.size());

            // для meta Decl генерим мета-код, "разбавляем" его табуляциями и строим "обратное" отображение - изменения в базовом ASTNode
            List<Pair<String, IElementType>> texts = decl.getMetaCode();
            List<String> declParams = decl.getDeclParams();

            LSFMetaCodeStatement resultStatement = null;
            List<Collection<List<ExtToken>>> result = null;

            for(LSFMetaCodeStatement statement : LSFResolver.findMetaUsages(decl)) {
                if(statement != null) {
                    SingleTransaction transaction = transactions.get(statement);
                    if(transaction != null) {
                        List<List<ExtToken>> oldTokens = new ArrayList<>();
                        List<InToken> usageParams = statement.getUsageParams();
                        List<InToken> newTokens = MetaChangeDetector.getNewTokens(texts, usageParams, declParams, oldTokens);

                        List<Collection<List<ExtToken>>> oldColTokens = new ArrayList<>();
                        for(List<ExtToken> oldToken : oldTokens)
                            oldColTokens.add(Collections.singleton(oldToken));

                        for(Change change : transaction.changes)
                            change.apply(newTokens, oldColTokens, usageParams, declParams);

                        if(result == null) {
                            result = oldColTokens;
                            resultStatement = statement;
                        } else {
                            Result<Pair<String, String>> error = new Result<>();
                            result = merge(result, oldColTokens, error);
                            if(result==null) {
                                String notificationText = decl.getGlobalName() + "\nFirst :" + usageParams + " " + error.getResult().first + "\nSecond :" + resultStatement.getUsageParams() + " " + error.getResult().second;
                                Notifications.Bus.notify(new Notification("diffMeta", "Diff meta", notificationText, NotificationType.ERROR));
                                System.out.println(notificationText);
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
                        int paramLength = 0;
                        for(ExtToken token : opt)
                            if(!token.param)
                                notParsed += token.text.length();
                            else
                                paramLength += token.text.length();
                        notParsed = notParsed * 100000 + paramLength * 100 + opt.size();
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

    // важно чтобы не было "сливания токенов", например Whitespace Node Whitespace, так как Whitespace'ы сольются и уйдет нумерация
    public void regChange(List<ASTNode> add, ASTNode anchor, Type type) {
        regChange(add, anchor, type == Type.REPLACE ? leafCount(anchor) - 1 : 0, type);
    }
    public void regChange(List<ASTNode> add, ASTNode before, int cntAfterBefore, Type type) {
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

            List<Pair<InToken, IElementType>> addPairs = new ArrayList<>();
            for(ASTNode addNode : add)
                addPairs.add(Pair.create(parseToken(addNode), addNode.getElementType()));

            int indexBefore = tokens.indexOf(leafBefore);
            metaTransaction.changes.add(new Change(indexBefore, addPairs, type));
            for(int i=0;i<cntAfterBefore;i++) {
                assert type.equals(Type.REPLACE);
                metaTransaction.changes.add(new Change(indexBefore, new ArrayList<Pair<InToken, IElementType>>(), Type.REPLACE));
            }
        }
    }
}
