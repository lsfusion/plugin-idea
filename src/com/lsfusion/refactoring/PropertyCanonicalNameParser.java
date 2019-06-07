package com.lsfusion.refactoring;

import static com.lsfusion.refactoring.PropertyCanonicalNameUtils.signatureLBracket;

public class PropertyCanonicalNameParser {
    public static String getNamespace(String canonicalName) {
        return CanonicalNameUtils.getNamespace(canonicalNameWithoutSignature(canonicalName));
    }

    public static String getName(String canonicalName) {
        return CanonicalNameUtils.getName(canonicalNameWithoutSignature(canonicalName));
    }

    private static String canonicalNameWithoutSignature(String canonicalName) {
        int bracketPos = leftBracketPosition(canonicalName);
        return canonicalName.substring(0, bracketPos);
    }
/*
    public List<ResolveClassSet> getSignature() {
        int bracketPos = leftBracketPositionWithCheck(name);

        if (name.lastIndexOf(signatureRBracket) != name.length() - 1) {
            throw new ParseException(String.format("'%s' should be at the end", signatureRBracket));
        }

        parseText = name.substring(bracketPos + 1, name.length() - 1);
        pos = 0;
        len = parseText.length();

        try {
            List<ResolveClassSet> result = parseAndClassSetList(true);
            if (pos < len) {
                throw new ParseException("parse error");
            }
            return result;
        } catch (ParseInnerException e) {
            throw new ParseException(e.getMessage());
        }
    }
*/
    private static int leftBracketPosition(String canonicalName) {
        return canonicalName.indexOf(signatureLBracket);
    }
/*
    private List<ResolveClassSet> parseAndClassSetList(boolean isSignature) {
        List<ResolveClassSet> result = new ArrayList<>();
        while (pos < len) {
            if (isSignature && isNext(UNKNOWNCLASS)) {
                checkNext(UNKNOWNCLASS);
                result.add(null);
            } else {
                result.add(parseAndClassSet());
            }

            if (isNext(",")) {
                checkNext(",");
            } else {
                break;
            }
        }
        return result;
    }

    private ResolveClassSet parseAndClassSet() {
        ResolveClassSet result;
        if (isNext(concatPrefix)) {
            result = parseConcatenateClassSet();
        } else if (isNext(ClassCanonicalNameUtils.OrObjectClassSetNameLBracket)) {
            result = parseOrObjectClassSet();
        } else if (isNext(ClassCanonicalNameUtils.UpClassSetNameLBracket)) {
            result = parseUpClassSet();
        } else {
            result = parseSingleClass();
        }
        return result;
    }

    private ResolveConcatenateClassSet parseConcatenateClassSet() {
        checkNext(concatPrefix);
        List<ResolveClassSet> classes = parseAndClassSetList(false);
        checkNext(ClassCanonicalNameUtils.ConcatenateClassNameRBracket);
        return new ResolveConcatenateClassSet(classes.toArray(new ResolveClassSet[classes.size()]));
    }

    private ResolveOrObjectClassSet parseOrObjectClassSet() {
        checkNext(ClassCanonicalNameUtils.OrObjectClassSetNameLBracket);
        ResolveUpClassSet up = parseUpClassSet();
        checkNext(",");
        ImSet<ConcreteCustomClass> customClasses = SetFact.EMPTY();
        if (!isNext(ClassCanonicalNameUtils.OrObjectClassSetNameRBracket)) {
            customClasses = parseCustomClassList();
        }
        ResolveOrObjectClassSet orSet = new ResolveOrObjectClassSet(up, customClasses);
        checkNext(ClassCanonicalNameUtils.OrObjectClassSetNameRBracket);
        return orSet;
    }

    private ImSet<ConcreteCustomClass> parseCustomClassList() {
        List<ConcreteCustomClass> classes = new ArrayList<>();
        while (pos < len) {
            ConcreteCustomClass cls = (ConcreteCustomClass) parseCustomClass();
            classes.add(cls);
            if (!isNext(",")) {
                break;
            }
            checkNext(",");
        }
        return new ArIndexedSet<>(classes.size(), classes.toArray(new ConcreteCustomClass[classes.size()]));
    }

    private ResolveUpClassSet parseUpClassSet() {
        if (isNext(ClassCanonicalNameUtils.UpClassSetNameLBracket)) {
            checkNext(ClassCanonicalNameUtils.UpClassSetNameLBracket);
            List<CustomClass> classes = new ArrayList<>();
            while (!isNext(ClassCanonicalNameUtils.UpClassSetNameRBracket)) {
                classes.add(parseCustomClass());
                if (!isNext(ClassCanonicalNameUtils.UpClassSetNameRBracket)) {
                    checkNext(",");
                }
            }
            checkNext(ClassCanonicalNameUtils.UpClassSetNameRBracket);
            return new ResolveUpClassSet(classes.toArray(new CustomClass[classes.size()]));
        } else {
            CustomClass cls = parseCustomClass();
            return new ResolveUpClassSet(cls);
        }
    }
*/    
}
