package com.github.orbyfied.carbon.content;

import com.github.orbyfied.carbon.element.RegistrableElement;

public class StrategyService<T extends Strategy<? extends RegistrableElement>> {

    protected final Class<?> runtimeType;

    public StrategyService(Class<?> runtimeType) {
        this.runtimeType = runtimeType;
    }

    public Class<?> getRuntimeType() {
        return runtimeType;
    }

}
