package com.github.orbyfied.carbon.content;

import com.github.orbyfied.carbon.element.RegistrableElement;

public class Strategy<T extends RegistrableElement> {

    /**
     * The element this strategy has
     * been assigned to.
     */
    protected final T item;

    public Strategy(T item) {
        this.item = item;
    }

    public T getElement() {
        return item;
    }

    public void build() { }

}
