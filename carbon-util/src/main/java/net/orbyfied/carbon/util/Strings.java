package net.orbyfied.carbon.util;

import java.util.ArrayList;
import java.util.List;

public class Strings {

    public static List<String> copyPrecedingMatches(List<String> src, String term) {
        List<String> list = new ArrayList<>(src.size());
        for (String s : src)
            if (s.startsWith(term))
                list.add(s);
        return list;
    }

}
