package com.github.orbyfied.carbon.command;

public interface SuggestionAccumulator {

    SuggestionAccumulator suggest(Object o);

    SuggestionAccumulator unsuggest(Object o);

}
