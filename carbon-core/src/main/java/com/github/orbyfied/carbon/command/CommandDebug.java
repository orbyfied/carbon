package com.github.orbyfied.carbon.command;

public class CommandDebug {

    public static void traverseAndPrintChildren(Node base, int depth) {
        Selecting sel = base.getComponentOf(Selecting.class);

        StringBuilder msgb = new StringBuilder();
        msgb.append("| " + "  ".repeat(depth) + depth + " -> '" + base.getName() + "'");
        if (sel != null)
            msgb.append(" , c_selecting: " + sel.getClass().getSimpleName() + "(" + Integer.toHexString(sel.hashCode()) + ")");
        System.out.println(msgb);

        for (Node child : base.children)
            traverseAndPrintChildren(child, depth + 1);
    }

}
