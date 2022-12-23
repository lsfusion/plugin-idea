package com.lsfusion.lang.parser;

import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringHash;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.source.resolve.FileContextUtil;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.ICompositeElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.PairProcessor;
import com.intellij.util.containers.LimitedPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * c/p from com.intellij.lang.parser.GeneratedParserUtilBase, для изменения completion-логики
 * @author gregsh
 */
@SuppressWarnings("UnusedParameters")
public class GeneratedParserUtilBase {

    private static final Logger LOG = Logger.getInstance("com.lsfusion.lang.parser.GeneratedParserUtilBase");

    public static final WhitespacesAndCommentsBinder GREEDY_WHITESPACE_AND_COMMENTS_PROCESSOR = (tokens, atStreamEdge, getter) -> tokens.size();

    private static final int MAX_RECURSION_LEVEL = 1000;
    private static final int MAX_VARIANTS_SIZE = 10000;
    private static final int MAX_VARIANTS_TO_DISPLAY = 50;

    private static final int INITIAL_VARIANTS_SIZE = 1000;
    private static final int VARIANTS_POOL_SIZE = 10000;
    private static final int FRAMES_POOL_SIZE = 500;

    public static final IElementType DUMMY_BLOCK = new DummyBlockElementType();

    public interface Parser {
        boolean parse(PsiBuilder builder, int level);
    }

    public static final Parser TOKEN_ADVANCER = (builder, level) -> {
        if (builder.eof()) return false;
        builder.advanceLexer();
        return true;
    };

    public static final Parser TRUE_CONDITION = (builder, level) -> true;

    public static boolean eof(PsiBuilder builder_, int level_) {
        return builder_.eof();
    }

    public static int current_position_(PsiBuilder builder_) {
        return builder_.rawTokenIndex();
    }

    public static boolean recursion_guard_(PsiBuilder builder_, int level_, String funcName_) {
        if (level_ > MAX_RECURSION_LEVEL) {
            builder_.error("Maximum recursion level (" + MAX_RECURSION_LEVEL + ") reached in '" + funcName_ + "'");
            return false;
        }
        return true;
    }

    public static boolean empty_element_parsed_guard_(PsiBuilder builder_, String funcName_, int prev_position_) {
        if (prev_position_ == current_position_(builder_)) {
            builder_.error("Empty element parsed in '" + funcName_ + "' at offset " + builder_.getCurrentOffset());
            return false;
        }
        return true;
    }

    public static boolean invalid_left_marker_guard_(PsiBuilder builder_, PsiBuilder.Marker marker_, String funcName_) {
        //builder_.error("Invalid left marker encountered in " + funcName_ +" at offset " + builder_.getCurrentOffset());
        boolean goodMarker = marker_ != null; // && ((LighterASTNode)marker_).getTokenType() != TokenType.ERROR_ELEMENT;
        if (!goodMarker) return false;
        ErrorState state = ErrorState.get(builder_);

        Frame frame = state.peekLastFrame();
        return frame == null || frame.errorReportedAt <= builder_.getCurrentOffset();
    }

    public static boolean consumeTokens(PsiBuilder builder_, int pin_, IElementType... tokens_) {
        ErrorState state = ErrorState.get(builder_);
        boolean result_ = true;
        boolean pinned_ = false;
        for (int i = 0, tokensLength = tokens_.length; i < tokensLength; i++) {
            if (pin_ > 0 && i == pin_) pinned_ = result_;
            if ((result_ || pinned_) && !consumeToken(builder_, tokens_[i])) {
                result_ = false;
                if (pin_ < 0 || pinned_) report_error_(builder_, state, false);
            }
        }
        return pinned_ || result_;
    }

    public static boolean parseTokens(PsiBuilder builder_, int pin_, IElementType... tokens_) {
        PsiBuilder.Marker marker_ = builder_.mark();
        boolean result_ = consumeTokens(builder_, pin_, tokens_);
        if (!result_) {
            marker_.rollbackTo();
        }
        else {
            marker_.drop();
        }
        return result_;
    }

    public static boolean consumeToken(PsiBuilder builder_, IElementType token) {
        if (nextTokenIsInner(builder_, token, true)) {
            builder_.advanceLexer();
            return true;
        }
        return false;
    }

    public static boolean nextTokenIsFast(PsiBuilder builder_, IElementType... tokens) {
        IElementType tokenType = builder_.getTokenType();
        for (IElementType token : tokens) {
            if (token == tokenType) return true;
        }
        return false;
    }

    public static boolean nextTokenIs(PsiBuilder builder_, String frameName, IElementType... tokens) {
        ErrorState state = ErrorState.get(builder_);
        if (state.completionCallback != null) return true;
        boolean track = !state.suppressErrors && state.predicateCount < 2 && state.predicateSign;
        if (!track) return nextTokenIsFast(builder_, tokens);
        boolean useFrameName = StringUtil.isNotEmpty(frameName);
        IElementType tokenType = builder_.getTokenType();
        if (tokenType == null) return false;
        boolean result = false;
        for (IElementType token : tokens) {
            if (!useFrameName) addVariant(builder_, state, token);
            result |= tokenType == token;
        }
        if (useFrameName) {
            addVariantInner(state, builder_.getCurrentOffset(), frameName);
        }
        return result;
    }

    public static boolean nextTokenIs(PsiBuilder builder_, IElementType token) {
        return nextTokenIsInner(builder_, token, false);
    }

    public static boolean nextTokenIsInner(PsiBuilder builder_, IElementType token, boolean force) {
        ErrorState state = ErrorState.get(builder_);
        if (state.completionCallback != null && !force) return true;
        IElementType tokenType = builder_.getTokenType();
        if (!state.suppressErrors && state.predicateCount < 2) {
            addVariant(builder_, state, token);
        }
        return token == tokenType;
    }

    private static void addVariant(PsiBuilder builder_, ErrorState state, IElementType token) {
        int offset = builder_.getCurrentOffset();

        addVariantInner(state, offset, token);

        addCompletionVariant(builder_, state, token, offset);
    }

    private static void addVariantInner(ErrorState state, int offset, Object o) {
        Variant variant = state.VARIANTS.alloc().init(offset, o);
        if (state.predicateSign) {
            state.variants.add(variant);
            if (state.lastExpectedVariantOffset < variant.offset) {
                state.lastExpectedVariantOffset = variant.offset;
            }
        }
        else {
            state.unexpected.add(variant);
        }
    }

    private static void addCompletionVariant(PsiBuilder builder_, ErrorState state, IElementType token, int offset) {
        CompletionCallback completionCallback = state.completionCallback;

        if (completionCallback != null && state.predicateSign && completionCallback.offset == offset) {

            state.completionTriggered = true;

            completionCallback.addCompletionVariants(state.frameStack, token);
        }
    }

    // here's the new section API for compact parsers & less IntelliJ platform API exposure
    public static final int _NONE_       = 0x0;
    public static final int _COLLAPSE_   = 0x1;
    public static final int _LEFT_       = 0x2;
    public static final int _LEFT_INNER_ = 0x4;
    public static final int _AND_        = 0x8;
    public static final int _NOT_        = 0x10;

    // simple enter/exit methods pair that doesn't require frame object
    public static PsiBuilder.Marker enter_section_(PsiBuilder builder_, int level, IElementType elementType) {
        PsiBuilder.Marker marker = builder_.mark();
        if (elementType != null) {
            ErrorState state = ErrorState.get(builder_);
            state.addFrame(
                    state.FRAMES.alloc().init(builder_.getCurrentOffset(), level, 0, elementType, null, state)
            );
        }
        return marker;
    }

    public static PsiBuilder.Marker enter_section_(PsiBuilder builder, int level, int modifiers) {
        return enter_section_(builder, level, modifiers, null, null);
    }

    public static void exit_section_(PsiBuilder builder_, PsiBuilder.Marker marker, @Nullable IElementType elementType, boolean result) {
        ErrorState state = ErrorState.get(builder_);
        Frame frame = elementType != null
                      ? state.pollLastFrame()
                      : state.peekLastFrame();

        close_marker_impl_(frame, marker, elementType, result);

        if (elementType != null) {
            Frame prevFrame = state.peekLastFrame();
            if (prevFrame != null && prevFrame.errorReportedAt < frame.errorReportedAt) {
                prevFrame.errorReportedAt = frame.errorReportedAt;
            }
            state.FRAMES.recycle(frame);
        }
    }

    // complex enter/exit methods pair with frame object
    public static PsiBuilder.Marker enter_section_(PsiBuilder builder_, int level, int modifiers, IElementType elementType, @Nullable String frameName) {
        PsiBuilder.Marker marker = builder_.mark();
        enter_section_impl_(builder_, level, modifiers, elementType, frameName);
        return marker;
    }

    private static void enter_section_impl_(PsiBuilder builder_, int level, int modifiers, IElementType elementType, @Nullable String frameName) {
        ErrorState state = ErrorState.get(builder_);
        Frame frame = state.FRAMES.alloc().init(builder_.getCurrentOffset(), level, modifiers, elementType, frameName, state);
        if (((modifiers & _LEFT_) | (modifiers & _LEFT_INNER_)) != 0) {
            PsiBuilder.Marker left = (PsiBuilder.Marker)builder_.getLatestDoneMarker();
            if (invalid_left_marker_guard_(builder_, left, frameName)) {
                frame.leftMarker = left;
            }
        }
        state.addFrame(frame);
        if ((modifiers & _AND_) != 0) {
            if (state.predicateCount == 0 && !state.predicateSign) {
                throw new AssertionError("Incorrect false predicate sign");
            }
            state.predicateCount++;
        }
        else if ((modifiers & _NOT_) != 0) {
            if (state.predicateCount == 0) {
                state.predicateSign = false;
            }
            else {
                state.predicateSign = !state.predicateSign;
            }
            state.predicateCount++;
        }
    }

    public static void exit_section_(PsiBuilder builder_,
                                     int level,
                                     PsiBuilder.Marker marker,
                                     @Nullable IElementType elementType,
                                     boolean result,
                                     boolean pinned,
                                     @Nullable Parser eatMore) {
        ErrorState state = ErrorState.get(builder_);

        Frame frame = state.pollLastFrame();
        if (frame == null || level != frame.level) {
            LOG.error("Unbalanced error section: got " + frame + ", expected level " + level);
            if (frame != null) state.FRAMES.recycle(frame);
            close_marker_impl_(frame, marker, elementType, result);
            return;
        }

        if (((frame.modifiers & _AND_) | (frame.modifiers & _NOT_)) != 0) {
            close_marker_impl_(frame, marker, null, false);
            state.predicateCount--;
            if ((frame.modifiers & _NOT_) != 0) state.predicateSign = !state.predicateSign;
            state.FRAMES.recycle(frame);
            return;
        }
        exit_section_impl_(state, frame, builder_, marker, elementType, result, pinned);

        int initialOffset = builder_.getCurrentOffset();
        boolean willFail = !result && !pinned;
        if (willFail
            && initialOffset == frame.offset
            && state.lastExpectedVariantOffset == frame.offset
            && frame.name != null
            && state.variants.size() - frame.variantCount > 1) {
            state.clearVariants(true, frame.variantCount);
            addVariantInner(state, initialOffset, frame.name);
        }
        if (!state.suppressErrors && eatMore != null) {
            state.suppressErrors = true;
            final boolean eatMoreFlagOnce = !builder_.eof() && eatMore.parse(builder_, frame.level + 1);
            final int lastErrorPos = getLastVariantOffset(state, initialOffset);
            boolean eatMoreFlag = eatMoreFlagOnce || !result && frame.offset == initialOffset && lastErrorPos > frame.offset;

            final LighterASTNode latestDoneMarker =
                    (pinned || result) && (state.altMode || lastErrorPos > initialOffset || level == 0) &&
                    eatMoreFlagOnce ? builder_.getLatestDoneMarker() : null;
            PsiBuilder.Marker extensionMarker = null;
            IElementType extensionTokenType = null;
            // whitespace prefix makes the very first frame offset bigger than marker start offset which is always 0
            if (latestDoneMarker instanceof PsiBuilder.Marker &&
                frame.offset >= latestDoneMarker.getStartOffset() &&
                frame.offset <= latestDoneMarker.getEndOffset()) {
                extensionMarker = ((PsiBuilder.Marker)latestDoneMarker).precede();
                extensionTokenType = latestDoneMarker.getTokenType();
                ((PsiBuilder.Marker)latestDoneMarker).drop();
            }
            // advance to the last error pos
            // skip tokens until lastErrorPos. parseAsTree might look better here...
            int parenCount = 0;
            while ((eatMoreFlag || parenCount > 0) && builder_.getCurrentOffset() < lastErrorPos) {
                if (state.braces != null) {
                    if (builder_.getTokenType() == state.braces[0].getLeftBraceType()) parenCount ++;
                    else if (builder_.getTokenType() == state.braces[0].getRightBraceType()) parenCount --;
                }
                builder_.advanceLexer();
                eatMoreFlag = eatMore.parse(builder_, frame.level + 1);
            }
            boolean errorReported = frame.errorReportedAt == initialOffset || !result && frame.errorReportedAt >= frame.offset;
            if (errorReported) {
                if (eatMoreFlag) {
                    builder_.advanceLexer();
                    parseAsTree(state, builder_, frame.level + 1, DUMMY_BLOCK, true, TOKEN_ADVANCER, eatMore);
                }
            }
            else if (eatMoreFlag) {
                errorReported = reportError(builder_, state, frame, true, true);
                parseAsTree(state, builder_, frame.level + 1, DUMMY_BLOCK, true, TOKEN_ADVANCER, eatMore);
            }
            else if (eatMoreFlagOnce || (!result && frame.offset != builder_.getCurrentOffset())) {
                errorReported = reportError(builder_, state, frame, true, false);
            }
            if (extensionMarker != null) {
                extensionMarker.done(extensionTokenType);
            }
            state.suppressErrors = false;
            if (errorReported || result) {
                state.clearVariants(true, 0);
                state.clearVariants(false, 0);
                state.lastExpectedVariantOffset = -1;
            }
        }
        else if (!result && pinned && frame.errorReportedAt < 0) {
            // do not report if there're errors after current offset
            if (getLastVariantOffset(state, initialOffset) == initialOffset) {
                // do not force, inner recoverRoot might have skipped some tokens
                reportError(builder_, state, frame, false, false);
            }
        }
        // propagate errorReportedAt up the stack to avoid duplicate reporting
        Frame prevFrame = willFail && eatMore == null ? null : state.peekLastFrame();
        if (prevFrame != null && prevFrame.errorReportedAt < frame.errorReportedAt) prevFrame.errorReportedAt = frame.errorReportedAt;
        state.FRAMES.recycle(frame);
    }

    private static void exit_section_impl_(ErrorState state,
                                           Frame frame,
                                           PsiBuilder builder_,
                                           PsiBuilder.Marker marker,
                                           IElementType elementType,
                                           boolean result,
                                           boolean pinned) {
        if (elementType != null && marker != null) {
            if ((frame.modifiers & _COLLAPSE_) != 0) {
                LighterASTNode last = result || pinned ? builder_.getLatestDoneMarker() : null;
                if (last != null && last.getStartOffset() == frame.offset && state.typeExtends(last.getTokenType(), elementType)) {
                    IElementType resultType = last.getTokenType();
                    ((PsiBuilder.Marker)last).drop();
                    marker.done(resultType);
                    return;
                }
            }
            if (result || pinned) {
                if ((frame.modifiers & _LEFT_INNER_) != 0 && frame.leftMarker != null) {
                    marker.done(elementType);
                    frame.leftMarker.precede().done(((LighterASTNode)frame.leftMarker).getTokenType());
                    frame.leftMarker.drop();
                }
                else if ((frame.modifiers & _LEFT_) != 0 && frame.leftMarker != null) {
                    marker.drop();
                    frame.leftMarker.precede().done(elementType);
                }
                else {
                    marker.done(elementType);
                }
            }
            else {
                close_marker_impl_(frame, marker, null, false);
            }
        }
        else if (result || pinned) {
            if (marker != null) marker.drop();
            if ((frame.modifiers & _LEFT_INNER_) != 0 && frame.leftMarker != null) {
                frame.leftMarker.precede().done(((LighterASTNode)frame.leftMarker).getTokenType());
                frame.leftMarker.drop();
            }
        }
        else {
            close_marker_impl_(frame, marker, null, false);
        }
    }

    private static void close_marker_impl_(Frame frame, PsiBuilder.Marker marker, IElementType elementType, boolean result) {
        if (marker == null) return;
        if (result) {
            if (elementType != null) {
                marker.done(elementType);
            }
            else {
                marker.drop();
            }
        }
        else {
            if (frame != null) {
                int offset = ((LighterASTNode)marker).getStartOffset();
                if (frame.errorReportedAt > offset) {
                    frame.errorReportedAt = frame.errorReportedAtPrev;
                }
            }
            marker.rollbackTo();
        }
    }

    public static void report_error_(PsiBuilder builder_, ErrorState state, boolean advance) {
        Frame frame = state.peekLastFrame();
        if (frame == null) {
            LOG.error("unbalanced enter/exit section call: got null");
            return;
        }
        int offset = builder_.getCurrentOffset();
        if (frame.errorReportedAt < offset && getLastVariantOffset(state, builder_.getCurrentOffset()) <= offset) {
            reportError(builder_, state, frame, true, advance);
        }
    }

    private static int getLastVariantOffset(ErrorState state, int defValue) {
        return state.lastExpectedVariantOffset < 0? defValue : state.lastExpectedVariantOffset;
    }

    private static boolean reportError(PsiBuilder builder_,
                                       ErrorState state,
                                       Frame frame,
                                       boolean force,
                                       boolean advance) {
        String expectedText = state.getExpectedText(builder_);
        boolean notEmpty = StringUtil.isNotEmpty(expectedText);
        if (force || notEmpty || advance) {
            String gotText = builder_.eof()? "unexpected end of file" :
                             notEmpty? "got '" + builder_.getTokenText() +"'" :
                             "'" + builder_.getTokenText() +"' unexpected";
            String message = expectedText + gotText;
            if (advance) {
                PsiBuilder.Marker mark = builder_.mark();
                builder_.advanceLexer();
                mark.error(message);
            }
            else {
                builder_.error(message);
            }
            frame.errorReportedAt = builder_.getCurrentOffset();
            state.stopCompletionIfAlreadyTriggered();
            return true;
        }
        return false;
    }


    public static final Key<CompletionCallback> COMPLETION_CALLBACK_KEY = Key.create("COMPLETION_CALLBACK_KEY");

    public static class CompletionCallback {
        public final int offset;

        public CompletionCallback(int offset) {
            this.offset = offset;
        }

        public void addCompletionVariants(List<Frame> frames, IElementType elementType) {
        }
    }

    public static class Builder extends PsiBuilderAdapter {
        public final ErrorState state;
        public final PsiParser parser;

        public Builder(PsiBuilder builder, ErrorState state, PsiParser parser) {
            super(builder);
            this.state = state;
            this.parser = parser;
        }
    }

    public static PsiBuilder adapt_builder_(IElementType root, PsiBuilder builder, PsiParser parser, TokenSet[] extendsSets) {
        ErrorState state = new ErrorState();
        ErrorState.initState(state, builder, root, extendsSets);
        return new Builder(builder, state, parser);
    }

    public static class ErrorState {
        TokenSet[] extendsSets;
        public PairProcessor<IElementType, IElementType> altExtendsChecker;

        int predicateCount;
        boolean predicateSign = true;
        boolean suppressErrors;
        public final ArrayList<Frame> frameStack = new ArrayList<>(30);
        public CompletionCallback completionCallback;
        public boolean completionTriggered = false;

        private boolean caseSensitive;
        public BracePair[] braces;
        public boolean altMode;

        private int lastExpectedVariantOffset = -1;
        public MyList<Variant> variants = new MyList<>(INITIAL_VARIANTS_SIZE);
        public MyList<Variant> unexpected = new MyList<>(INITIAL_VARIANTS_SIZE / 10);

        final LimitedPool<Variant> VARIANTS = new LimitedPool<>(VARIANTS_POOL_SIZE, new LimitedPool.ObjectFactory<>() {
            @Override
            public Variant create() {
                return new Variant();
            }

            @Override
            public void cleanup(final Variant o) {
            }
        });
        final LimitedPool<Frame> FRAMES = new LimitedPool<>(FRAMES_POOL_SIZE, new LimitedPool.ObjectFactory<>() {
            @Override
            public Frame create() {
                return new Frame();
            }

            @Override
            public void cleanup(final Frame o) {
            }
        });

        public static ErrorState get(PsiBuilder builder) {
            return ((Builder)builder).state;
        }

        public static void initState(ErrorState state, PsiBuilder builder, IElementType root, TokenSet[] extendsSets) {
            state.extendsSets = extendsSets;
            PsiFile file = builder.getUserDataUnprotected(FileContextUtil.CONTAINING_FILE_KEY);
            state.completionCallback = file == null? null: file.getUserData(COMPLETION_CALLBACK_KEY);
            Language language = file == null? root.getLanguage() : file.getLanguage();
            state.caseSensitive = language.isCaseSensitive();
            PairedBraceMatcher matcher = LanguageBraceMatching.INSTANCE.forLanguage(language);
            state.braces = matcher == null ? null : matcher.getPairs();
            if (state.braces != null && state.braces.length == 0) state.braces = null;
        }

        public String getExpectedText(PsiBuilder builder_) {
            int offset = builder_.getCurrentOffset();
            StringBuilder sb = new StringBuilder();
            if (addExpected(sb, offset, true)) {
                sb.append(" expected, ");
            }
            else if (addExpected(sb, offset, false)) sb.append(" unexpected, ");
            return sb.toString();
        }

        private boolean addExpected(StringBuilder sb, int offset, boolean expected) {
            MyList<Variant> list = expected ? variants : unexpected;
            String[] strings = new String[list.size()];
            long[] hashes = new long[strings.length];
            Arrays.fill(strings, "");
            int count = 0;
            loop: for (Variant variant : list) {
                if (offset == variant.offset) {
                    String text = variant.object.toString();
                    long hash = StringHash.calc(text);
                    for (int i=0; i<count; i++) {
                        if (hashes[i] == hash) continue loop;
                    }
                    hashes[count] = hash;
                    strings[count] = text;
                    count++;
                }
            }
            Arrays.sort(strings);
            count = 0;
            for (String s : strings) {
                if (s.length() == 0) continue;
                if (count++ > 0) {
                    if (count > MAX_VARIANTS_TO_DISPLAY) {
                        sb.append(" and ...");
                        break;
                    }
                    else {
                        sb.append(", ");
                    }
                }
                char c = s.charAt(0);
                String displayText = c == '<' || StringUtil.isJavaIdentifierStart(c) ? s : '\'' + s + '\'';
                sb.append(displayText);
            }
            if (count > 1 && count < MAX_VARIANTS_TO_DISPLAY) {
                int idx = sb.lastIndexOf(", ");
                sb.replace(idx, idx + 1, " or");
            }
            return count > 0;
        }

        public void clearVariants(boolean expected, int start) {
            MyList<Variant> list = expected? variants : unexpected;
            for (int i = start, len = list.size(); i < len; i ++) {
                VARIANTS.recycle(list.get(i));
            }
            list.setSize(start);
        }

        boolean typeExtends(IElementType child_, IElementType parent_) {
            if (child_ == parent_) return true;
            if (extendsSets != null) {
                for (TokenSet set : extendsSets) {
                    if (set.contains(child_) && set.contains(parent_)) return true;
                }
            }
            return altExtendsChecker != null && altExtendsChecker.process(child_, parent_);
        }

        public void stopCompletionIfAlreadyTriggered() {
            if (completionTriggered && completionCallback != null) {
                completionCallback = null;
            }
        }

        public Frame peekLastFrame() {
            return frameStack.isEmpty() ? null : frameStack.get(frameStack.size() - 1);
        }

        public Frame pollLastFrame() {
            return frameStack.isEmpty() ? null : frameStack.remove(frameStack.size() - 1);
        }

        public void addFrame(Frame newFrame) {
            frameStack.add(newFrame);
        }
    }

    public static class Frame {
        public int offset;
        public IElementType type;
        public int level;
        public int modifiers;
        public String name;
        public int variantCount;
        public int errorReportedAt;
        public int errorReportedAtPrev;
        public PsiBuilder.Marker leftMarker;

        public Frame() {
        }

        public Frame init(int offset, int level, int modifiers, IElementType elementType, String frameName, ErrorState state) {
            this.type = elementType;
            this.offset = offset;
            this.level = level;
            this.modifiers = modifiers;
            this.name = frameName;
            this.variantCount = state.variants.size();
            this.errorReportedAt = -1;

            Frame prev = state.peekLastFrame();
            errorReportedAtPrev = prev == null? -1 : prev.errorReportedAt;
            leftMarker = null;
            return this;
        }

        @Override
        public String toString() {
            String mod = modifiers == _NONE_ ? "_NONE_, " :
                         ((modifiers & _COLLAPSE_) != 0? "_CAN_COLLAPSE_, ": "") +
                         ((modifiers & _LEFT_) != 0? "_LEFT_, ": "") +
                         ((modifiers & _LEFT_INNER_) != 0? "_LEFT_INNER_, ": "") +
                         ((modifiers & _AND_) != 0? "_AND_, ": "") +
                         ((modifiers & _NOT_) != 0? "_NOT_, ": "");
            return "<" + offset + ", " + mod + level + (type != null ? ", " + type : "") + (errorReportedAt > -1 ? ", [" + errorReportedAt + "]" : "") + ">";
        }
    }


    public static class Variant {
        int offset;
        Object object;

        public Variant init(int offset, Object text) {
            this.offset = offset;
            this.object = text;
            return this;
        }

        @Override
        public String toString() {
            return "<" + offset + ", " + object + ">";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Variant variant = (Variant)o;

            if (offset != variant.offset) return false;
            if (!this.object.equals(variant.object)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = offset;
            result = 31 * result + object.hashCode();
            return result;
        }
    }


    private static final int MAX_CHILDREN_IN_TREE = 10;
    public static boolean parseAsTree(ErrorState state, final PsiBuilder builder_, int level, final IElementType chunkType,
                                      boolean checkBraces, final Parser parser, final Parser eatMoreCondition) {
        final LinkedList<Pair<PsiBuilder.Marker, PsiBuilder.Marker>> parenList = new LinkedList<>();
        final LinkedList<Pair<PsiBuilder.Marker, Integer>> siblingList = new LinkedList<>();
        PsiBuilder.Marker marker = null;

        final Runnable checkSiblingsRunnable = () -> {
            main:
            while (!siblingList.isEmpty()) {
                final Pair<PsiBuilder.Marker, PsiBuilder.Marker> parenPair = parenList.peek();
                final int rating = siblingList.getFirst().second;
                int count = 0;
                for (Pair<PsiBuilder.Marker, Integer> pair : siblingList) {
                    if (pair.second != rating || parenPair != null && pair.first == parenPair.second) break main;
                    if (++count >= MAX_CHILDREN_IN_TREE) {
                        final PsiBuilder.Marker parentMarker = pair.first.precede();
                        while (count-- > 0) {
                            siblingList.removeFirst();
                        }
                        parentMarker.done(chunkType);
                        siblingList.addFirst(Pair.create(parentMarker, rating + 1));
                        continue main;
                    }
                }
                break;
            }
        };
        boolean checkParens = state.braces != null && checkBraces;
        int totalCount = 0;
        int tokenCount = 0;
        if (checkParens) {
            int tokenIdx = -1;
            while (builder_.rawLookup(tokenIdx) == TokenType.WHITE_SPACE) tokenIdx --;
            LighterASTNode doneMarker = builder_.rawLookup(tokenIdx) == state.braces[0].getLeftBraceType() ? builder_.getLatestDoneMarker() : null;
            if (doneMarker != null && doneMarker.getStartOffset() == builder_.rawTokenTypeStart(tokenIdx) && doneMarker.getTokenType() == TokenType.ERROR_ELEMENT) {
                parenList.add(Pair.create(((PsiBuilder.Marker)doneMarker).precede(), (PsiBuilder.Marker)null));
            }
        }
        while (true) {
            final IElementType tokenType = builder_.getTokenType();
            if (checkParens && (tokenType == state.braces[0].getLeftBraceType() || tokenType == state.braces[0].getRightBraceType() && !parenList.isEmpty())) {
                if (marker != null) {
                    marker.done(chunkType);
                    siblingList.addFirst(Pair.create(marker, 1));
                    marker = null;
                    tokenCount = 0;
                }
                if (tokenType == state.braces[0].getLeftBraceType()) {
                    final Pair<PsiBuilder.Marker, Integer> prev = siblingList.peek();
                    parenList.addFirst(Pair.create(builder_.mark(), prev == null ? null : prev.first));
                }
                checkSiblingsRunnable.run();
                builder_.advanceLexer();
                if (tokenType == state.braces[0].getRightBraceType()) {
                    final Pair<PsiBuilder.Marker, PsiBuilder.Marker> pair = parenList.removeFirst();
                    pair.first.done(chunkType);
                    // drop all markers inside parens
                    while (!siblingList.isEmpty() && siblingList.getFirst().first != pair.second) {
                        siblingList.removeFirst();
                    }
                    siblingList.addFirst(Pair.create(pair.first, 1));
                    checkSiblingsRunnable.run();
                }
            }
            else {
                if (marker == null) {
                    marker = builder_.mark();
                }
                final boolean result = (!parenList.isEmpty() || eatMoreCondition.parse(builder_, level + 1)) && parser.parse(builder_, level + 1);
                if (result) {
                    tokenCount++;
                    totalCount++;
                }
                if (!result) {
                    break;
                }
            }

            if (tokenCount >= MAX_CHILDREN_IN_TREE && marker != null) {
                marker.done(chunkType);
                siblingList.addFirst(Pair.create(marker, 1));
                checkSiblingsRunnable.run();
                marker = null;
                tokenCount = 0;
            }
        }
        if (marker != null) {
            marker.drop();
        }
        for (Pair<PsiBuilder.Marker, PsiBuilder.Marker> pair : parenList) {
            pair.first.drop();
        }
        return totalCount != 0;
    }

    private static class DummyBlockElementType extends IElementType implements ICompositeElementType{
        DummyBlockElementType() {
            super("DUMMY_BLOCK", Language.ANY);
        }

        @NotNull
        @Override
        public ASTNode createCompositeNode() {
            return new DummyBlock();
        }
    }

    public static class DummyBlock extends CompositePsiElement {
        DummyBlock() {
            super(DUMMY_BLOCK);
        }

        @NotNull
        @Override
        public PsiReference[] getReferences() {
            return PsiReference.EMPTY_ARRAY;
        }

        @NotNull
        @Override
        public Language getLanguage() {
            return getParent().getLanguage();
        }
    }

    protected static class MyList<E> extends ArrayList<E> {
        public MyList(int initialCapacity) {
            super(initialCapacity);
        }

        protected void setSize(int fromIndex) {
            removeRange(fromIndex, size());
        }

        @Override
        public void ensureCapacity(int minCapacity) {
            int size = size();
            if (size >= MAX_VARIANTS_SIZE) {
                removeRange(MAX_VARIANTS_SIZE / 4, size - MAX_VARIANTS_SIZE / 4);
            }
            super.ensureCapacity(minCapacity);
        }
    }
}
