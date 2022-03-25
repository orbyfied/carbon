package com.github.orbyfied.carbon.command;

public interface Suggester extends NodeComponent {

    void suggest(Invocation ctx, Suggestions builder);

}
