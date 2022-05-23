package net.orbyfied.carbon.util.message.writer;

import net.orbyfied.carbon.util.message.Context;

public class StringMessageWriter extends MessageWriter<String, String> {

    protected StringBuilder builder = new StringBuilder();

    public StringMessageWriter() {
        super(String.class);
    }

    @Override
    public StringMessageWriter contextual(Context context) {
        super.contextual(context);
        return this;
    }

    @Override
    public StringMessageWriter append(String component) {
        builder.append(component);
        return this;
    }

    @Override
    public String build() {
        return builder.toString();
    }

}
