package net.orbyfied.carbon.registry;

public class MalformedIdentifierException extends RuntimeException {
    
    protected Class<? extends Identifier> type;
    protected String input;

    public MalformedIdentifierException(String input, Class<? extends Identifier> type) {
        this.type = type;
        this.input = input;
    }
    
    public MalformedIdentifierException(String input, Class<? extends Identifier> type, String message) {
        super(message);
        this.type = type;
        this.input = input;
    }

    public MalformedIdentifierException(String input, Class<? extends Identifier> type, Exception e) {
        super(e);
        this.type = type;
        this.input = input;
    }

    public MalformedIdentifierException(String input, Class<? extends Identifier> type, String msg, Exception e) {
        super(msg, e);
        this.type = type;
        this.input = input;
    }

    @Override
    public String getMessage() {
        String smsg = super.getMessage();
        return "at " + type.getSimpleName() +
                " for input string: \"" + input + "\"" +
                (smsg != null ? ": " + smsg : "");
    }

}
