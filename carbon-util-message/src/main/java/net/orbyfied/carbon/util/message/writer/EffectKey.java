package net.orbyfied.carbon.util.message.writer;

import java.util.Objects;

public class EffectKey<E, R> {

    final Class<R> resType;
    final Class<E> effType;

    public EffectKey(Class<E> effType, Class<R> resType) {
        this.resType = resType;
        this.effType = effType;
    }

    public Class<R> getTargetType() {
        return resType;
    }

    public Class<E> getEffectType() {
        return effType;
    }

    /////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectKey<?, ?> effectKey = (EffectKey<?, ?>) o;
        return Objects.equals(resType, effectKey.resType) && Objects.equals(effType, effectKey.effType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resType, effType);
    }

    @Override
    public String toString() {
        return effType.getSimpleName() + "->" + resType.getSimpleName();
    }

}
