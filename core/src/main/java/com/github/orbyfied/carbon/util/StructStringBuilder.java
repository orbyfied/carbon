package com.github.orbyfied.carbon.util;

import java.util.StringJoiner;

public class StructStringBuilder {
    
    private StringBuilder b;
    private int indentLevel = 0;
    private String indent = "  ";
    
    public StructStringBuilder() {
        this.b = new StringBuilder();
    }

    public StructStringBuilder(int indentLevel) {
        this.b = new StringBuilder();
        this.indentLevel = indentLevel;
    }
    
    public StructStringBuilder(String s, int lvl) {
        this.b = new StringBuilder(s);
        this.indentLevel = lvl;
    }

    public int getIndentLevel() {
        return indentLevel;
    }

    public StructStringBuilder indent(int off) {
        this.indentLevel += off;
        this.indentLevel = Math.max(0, indentLevel);
        return this;
    }

    public StructStringBuilder setIndent(int a) {
        this.indentLevel = Math.max(0, a);
        return this;
    }

    public StructStringBuilder setIndentString(String str) {
        this.indent = str;
        return this;
    }
    
    public int compareTo(StringBuilder another) {
        return b.compareTo(another);
    }

    
    public StructStringBuilder append(Object obj) {
        return append(String.valueOf(obj));
    }
    
    public StructStringBuilder append(String str) {
        b.append(str);
        if (str.endsWith("\n"))
            b.append(indent.repeat(indentLevel));
        return this;
    }

    public StructStringBuilder append(StringBuffer sb) {
        append(sb.toString());
        return this;
    }

    
    public StructStringBuilder append(CharSequence s) {
        b.append(s);
        if (s.charAt(s.length() - 1) == '\n')
            b.append(indent.repeat(indentLevel));
        return this;
    }

    /**
     * @throws     IndexOutOfBoundsException {@inheritDoc}
     */
    
    public StructStringBuilder append(CharSequence s, int start, int end) {
        b.append(s, start, end);
        if (s.charAt(s.length() - 1) == '\n')
            b.append(indent.repeat(indentLevel));
        return this;
    }

    
    public StructStringBuilder append(char[] str) {
        b.append(str);
        if (str[str.length - 1] == '\n')
            b.append(indent.repeat(indentLevel));
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    
    public StructStringBuilder append(char[] str, int offset, int len) {
        b.append(str, offset, len);
        if (str[str.length - 1] == '\n')
            b.append(indent.repeat(indentLevel));
        return this;
    }

    
    public StructStringBuilder append(boolean b) {
        this.b.append(b);
        return this;
    }

    
    
    public StructStringBuilder append(char c) {
        b.append(c);
        return this;
    }

    
    
    public StructStringBuilder append(int i) {
        b.append(i);
        return this;
    }

    
    public StructStringBuilder append(long lng) {
        b.append(lng);
        return this;
    }

    
    public StructStringBuilder append(float f) {
        b.append(f);
        return this;
    }

    
    public StructStringBuilder append(double d) {
        b.append(d);
        return this;
    }

    
    public StructStringBuilder appendCodePoint(int codePoint) {
        b.appendCodePoint(codePoint);
        return this;
    }

    
    public StructStringBuilder delete(int start, int end) {
        b.delete(start, end);
        return this;
    }

    
    public StructStringBuilder deleteCharAt(int index) {
        b.deleteCharAt(index);
        return this;
    }
    
    
    public StructStringBuilder replace(int start, int end, String str) {
        b.replace(start, end, str);
        return this;
    }
    
    
    public StructStringBuilder insert(int index, char[] str, int offset,
                                int len)
    {
        b.insert(index, str, offset, len);
        return this;
    }
    
    
    public StructStringBuilder insert(int offset, Object obj) {
        b.insert(offset, obj);
        return this;
    }
    
    public StructStringBuilder insert(int offset, String str) {
        b.insert(offset, str);
        return this;
    }
    
    public StructStringBuilder insert(int offset, char[] str) {
        b.insert(offset, str);
        return this;
    }
    
    
    public StructStringBuilder insert(int dstOffset, CharSequence s) {
        b.insert(dstOffset, s);
        return this;
    }
    
    
    public StructStringBuilder insert(int dstOffset, CharSequence s,
                                int start, int end)
    {
        b.insert(dstOffset, s, start, end);
        return this;
    }
    
    
    public StructStringBuilder insert(int offset, boolean b) {
        this.b.insert(offset, b);
        return this;
    }
    
    
    public StructStringBuilder insert(int offset, char c) {
        b.insert(offset, c);
        return this;
    }

    
    public StructStringBuilder insert(int offset, int i) {
        b.insert(offset, i);
        return this;
    }

    
    public StructStringBuilder insert(int offset, long l) {
        b.insert(offset, l);
        return this;
    }

    
    public StructStringBuilder insert(int offset, float f) {
        b.insert(offset, f);
        return this;
    }

    
    public StructStringBuilder insert(int offset, double d) {
        b.insert(offset, d);
        return this;
    }

    
    public int indexOf(String str) {
        return b.indexOf(str);
    }

    
    public int indexOf(String str, int fromIndex) {
        return b.indexOf(str, fromIndex);
    }

    
    public int lastIndexOf(String str) {
        return b.lastIndexOf(str);
    }

    
    public int lastIndexOf(String str, int fromIndex) {
        return b.lastIndexOf(str, fromIndex);
    }

    
    public StructStringBuilder reverse() {
        b.reverse();
        return this;
    }

    @Override
    public String toString() {
        return b.toString();
    }
}
