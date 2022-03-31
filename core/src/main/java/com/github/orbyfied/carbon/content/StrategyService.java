package com.github.orbyfied.carbon.content;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.element.RegistrableElement;

public class StrategyService<T extends Strategy<? extends RegistrableElement>> {

    protected final Class<?> runtimeType;
    protected final Carbon   main;

    public StrategyService(
            Carbon main,
            Class<?> runtimeType) {
        this.main = main;
        this.runtimeType = runtimeType;
    }

    public Class<?> getRuntimeType() {
        return runtimeType;
    }

}
