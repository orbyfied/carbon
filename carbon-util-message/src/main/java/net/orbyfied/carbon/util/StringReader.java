package net.orbyfied.carbon.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class StringReader {

    public static final char DONE = '\uFFFF';

    private int index = 0;
    private String str;
    private int len;

    public StringReader(String str, int index) {
        this.str   = str;
        this.len   = str.length();
        this.index = index;
    }

    public int clamp(int index) {
        return Math.min(len - 1, Math.max(0, index));
    }

    public char peekAt(int i) {
        return str.charAt(clamp(i));
    }

    public char peek(int i) {
        int idx = index + i;
        if (idx < 0 || idx >= len)
            return DONE;
        return str.charAt(idx);
    }

    public char next() {
        if ((index += 1) >= len) return DONE;
        return str.charAt(index);
    }

    public char next(int a) {
        if ((index += a) >= len) return DONE;
        return str.charAt(index);
    }

    public char prev() {
        return str.charAt(clamp(index -= 1));
    }

    public char prev(int a) {
        return str.charAt(clamp(index -= a));
    }

    public char current() {
        if (index < 0 || index >= len) return DONE;
        return str.charAt(index);
    }

    private static final Predicate<Character> ONPRED = c -> true;

    public String collect(Predicate<Character> pred, int offEnd) {
        String str = collect(pred);
        next(offEnd);
        return str;
    }

    public String collect(Predicate<Character> pred) {
        return collect(pred, null);
    }

    public String collect(Predicate<Character> pred, Predicate<Character> skip, int offEnd) {
        String str = collect(pred, skip);
        next(offEnd);
        return str;
    }

    public String collect(Predicate<Character> pred, Predicate<Character> skip) {
        if (pred == null)
            pred = ONPRED;
        StringBuilder b = new StringBuilder();
        prev();
        char c;
        while ((c = next()) != DONE && pred.test(c)) {
            if (skip == null || !skip.test(c)) {
                b.append(c);
            }
        }

        return b.toString();
    }

    public String pcollect(Predicate<Character> pred) {
        return pcollect(pred, null);
    }

    public String pcollect(Predicate<Character> pred, Predicate<Character> skip) {
        if (pred == null)
            pred = ONPRED;
        StringBuilder b = new StringBuilder();
        int off = 0;
        char c;
        while ((c = peek(off++)) != DONE && pred.test(c)) {
            if (skip == null || !skip.test(c)) {
                b.append(c);
            }
        }

        return b.toString();
    }

    public List<String> split(char... chars) {
        List<String> list = new ArrayList<>(len / 10);
        HashSet<Character> charSet = new HashSet<>();
        for (char c : chars)
            charSet.add(c);
        while (current() != DONE) {
            list.add(collect(c -> !charSet.contains(c)));
            next();
        }
        return list;
    }

    public int index() {
        return index;
    }

    public StringReader index(int i) {
        this.index = i;
        return this;
    }

    public String getString() {
        return str;
    }

    public StringReader subForward(int from, int len) {
        StringReader reader = new StringReader(str, from);
        reader.len = from + len;
        return reader;
    }

    public StringReader branch() {
        return new StringReader(str, index);
    }

}
