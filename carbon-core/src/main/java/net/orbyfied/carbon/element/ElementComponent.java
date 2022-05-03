package net.orbyfied.carbon.element;

public interface ElementComponent<E extends RegistrableElement> {

    E getElement();

    void build();

}
