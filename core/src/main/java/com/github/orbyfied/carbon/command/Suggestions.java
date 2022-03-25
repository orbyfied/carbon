package com.github.orbyfied.carbon.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Suggestions {

    protected final ArrayList<String> suggestions = new ArrayList<>();

    public Suggestions suggest(Object o) {
        if (o == null)
            return this;
        suggestions.add(o.toString());
        return this;
    }

    public Suggestions unsuggest(Object o) {
        if (o == null)
            return this;
        suggestions.remove(o.toString());
        return this;
    }

    public List<String> getSuggestions() {
        return Collections.unmodifiableList(suggestions);
    }

    public Stream<String> streamSuggestions() {
        return suggestions.stream();
    }

}
