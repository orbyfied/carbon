package net.orbyfied.carbon.util.message;

public interface Slice {

    /**
     * Should write the contents of this
     * slice to the string builder potentially
     * using the values and options provided by
     * the given context.
     * @param ctx The context.
     * @param builder The result builder.
     */
    void write(Context ctx, StringBuilder builder);

    default String getString(Object... params) {
        // build context
        Context context = new Context();

        String key = null;
        int l = params.length;
        for (int i = 0; i < l; i++) {
            Object o = params[i];
            if (i % 2 == 0) { // key
                if (!(o instanceof String))
                    throw new IllegalArgumentException("Expected key at parameter index " + i);
                key = (String) params[i];
            } else { // value
                // get key type
                char first = key.charAt(0);
                key = key.substring(1);

                switch (first) {
                    case '%' -> { // value
                        context.value(key, o);
                    }

                    case '!' -> { // option
                        context.option(key, o);
                    }
                }
            }
        }

        // create string builder
        StringBuilder builder = new StringBuilder();

        // write
        write(context, builder);

        // return
        return builder.toString();
    }

}
