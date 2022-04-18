package com.github.orbyfied.carbon.util.ops;

import java.util.List;
import java.util.Map;
import java.util.function.*;

public interface EntryOperation<R, K, V> extends Operation<R> {

    R with();

    R without();

    V get();

    boolean has();

    K key();

    V val();

    //////////////////////////////////////

    class Builder<R, K, V> {

        Supplier<R> returner;

        K key;
        V val;

        BiConsumer <K, V> doWith;
        BiConsumer <K, V> doWithout;
        Function   <K, V> doGet;
        BiPredicate<K, V> doHas;

        public Builder<R, K, V> returns(final R ret) {
            returner = () -> ret;
            return this;
        }

        public Builder<R, K, V> returns(Supplier<R> ret) {
            returner = ret;
            return this;
        }

        public Builder<R, K, V> key(K k) {
            key = k;
            return this;
        }

        public Builder<R, K, V> value(V v) {
            val = v;
            return this;
        }

        public Builder<R, K, V> doWith(BiConsumer<K, V> doWith) {
            this.doWith = doWith;
            return this;
        }

        public Builder<R, K, V> doWithout(BiConsumer<K, V> doWithout) {
            this.doWithout = doWithout;
            return this;
        }

        public Builder<R, K, V> doGet(Function<K, V> doGet) {
            this.doGet = doGet;
            return this;
        }

        public Builder<R, K, V> doHas(BiPredicate<K, V> doHas) {
            this.doHas = doHas;
            return this;
        }

        public EntryOperation<R, K, V> build() {
            return new EntryOperation<>() {
                @Override
                public R with() {
                    doWith.accept(key, val);
                    return returner.get();
                }

                @Override
                public R without() {
                    doWithout.accept(key, val);
                    return returner.get();
                }

                @Override
                public V get() {
                    return doGet.apply(key);
                }

                @Override
                public boolean has() {
                    return doHas.test(key, val);
                }

                @Override
                public K key() {
                    return key;
                }

                @Override
                public V val() {
                    return val;
                }

                @Override
                public R skip() {
                    return returner.get();
                }
            };
        }

    }

    static <R, K, V> Builder<R, K, V> builder() {
        return new Builder<>();
    }

    static <R, K, V> EntryOperation<R, K, V> ofMapEntry(R ret, Map<K, V> map, K k, V v) {
        return new EntryOperation<>() {
            @Override
            public R with() {
                map.put(k, v);
                return ret;
            }

            @Override
            public R without() {
                map.remove(k, v);
                return ret;
            }

            @Override
            public V get() {
                return map.get(k);
            }

            @Override
            public boolean has() {
                return map.containsKey(k);
            }

            @Override
            public K key() {
                return k;
            }

            @Override
            public V val() {
                return v;
            }

            @Override
            public R skip() {
                return ret;
            }
        };
    }

    static <R, V> EntryOperation<R, V, V> ofListEntry(R ret, List<V> list, V val) {
        return new EntryOperation<>() {
            @Override
            public R with() {
                list.add(val);
                return ret;
            }

            @Override
            public R without() {
                list.add(val);
                return ret;
            }

            @Override
            public V get() {
                return val;
            }

            @Override
            public boolean has() {
                return list.contains(val);
            }

            @Override
            public V key() {
                return val;
            }

            @Override
            public V val() {
                return val;
            }

            @Override
            public R skip() {
                return ret;
            }
        };
    }

}
