package com.github.orbyfied.carbon.command;

public interface Completer extends NodeComponent {

    void completeSelf(Context context,
                      Node from,
                      Suggestions suggestions);

}