package com.github.orbyfied.carbon.command;

import java.util.List;
import java.util.stream.Stream;

public interface Suggestions {

    Suggestions suggest(Object o);

    Suggestions unsuggest(Object o);

}
