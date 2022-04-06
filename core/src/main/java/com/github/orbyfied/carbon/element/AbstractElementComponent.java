package com.github.orbyfied.carbon.element;

public abstract class AbstractElementComponent<E extends RegistrableElement>
        implements ElementComponent<E> {

    protected final E element;

    public AbstractElementComponent(E element) {
        this.element = element;
    }

    @Override
    public E getElement() {
        return element;
    }

}
