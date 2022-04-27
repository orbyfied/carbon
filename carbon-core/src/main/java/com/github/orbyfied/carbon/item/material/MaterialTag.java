package com.github.orbyfied.carbon.item.material;

import com.github.orbyfied.carbon.util.math.Mathf;

import java.util.*;

public class MaterialTag {

    public static MaterialTag of(String str) {
        // for example: 'dust.iron' = iron dust
        return new MaterialTag(str.split("\\."));
    }

    ///////////////////////////////////

    public MaterialTag(String... parts) {
        this.parts = parts;
    }

    protected String[] parts;

    public String[] getParts() {
        return parts;
    }

    public int length() {
        return parts.length;
    }

    public String getPart(int i) {
        return parts[Mathf.in(i, 0, parts.length - 1)];
    }

    public boolean is(String str) {
        return MaterialAPI.is(this, str);
    }

    public boolean is(MaterialTag tag) {
        return MaterialAPI.is(this, tag);
    }

    ///////////////////////////////////////////////////

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        int l = parts.length;
        for (int i = 0; i < l; i++) {
            if (i != 0)
                b.append(".");
            b.append(parts[i]);
        }
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialTag that = (MaterialTag) o;
        return Arrays.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object)parts);
    }

}
