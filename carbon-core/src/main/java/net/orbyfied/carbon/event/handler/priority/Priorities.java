package net.orbyfied.carbon.event.handler.priority;

public enum Priorities implements HandlerPriority {

    HIGHEST(0, 0f),
    HIGH(1, 0.1f),
    HIGHER(2, 0.2f),
    NORMAL(3, 0.5f),
    LOWER(4, 0.7f),
    LOW(5, 0.8f),
    LOWEST(6, 0.9f)
    ;

    private int ord;
    private float pos;

    Priorities(int ord, float pos) {
        this.ord = ord;
        this.pos = pos;
    }

    @Override
    public int getOrdinal() {
        return ord;
    }

    @Override
    public float getEstimatedPosition() {
        return pos;
    }

}
