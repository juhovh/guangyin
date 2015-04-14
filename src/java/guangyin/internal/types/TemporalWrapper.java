package guangyin.internal.types;

import clojure.lang.Keyword;
import clojure.lang.IMapEntry;
import clojure.lang.Associative;
import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentCollection;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;

public class TemporalWrapper extends TemporalAccessorWrapper implements Associative {
    private Temporal wrapped;

    public TemporalWrapper(IPersistentMap keymap, Temporal wrapped) {
        super(keymap, wrapped);
        this.wrapped = wrapped;
    }

    public IPersistentCollection cons(Object o) {
        throw new UnsupportedOperationException("Cons not supported by temporal");
    }

    public IPersistentCollection empty() {
        throw new UnsupportedOperationException("Empty not supported by temporal");
    }

    public boolean equiv(Object o) {
        return wrapped.equals(o);
    }

    public boolean containsKey(Object key) {
        return this.valmap().containsKey(key);
    }

    public IMapEntry entryAt(Object key) {
        return this.valmap().entryAt(key);
    }

    public Associative assoc(Object key, Object val) {
        Keyword keyword = (Keyword) key;
        TemporalField field = (TemporalField) keyword.invoke(this.keymap);
        if (field == null || !wrapped.isSupported(field)) {
            String msg = "Field " + keyword.getName() + " not supported";
            throw new IllegalArgumentException(msg);
        }
        Temporal temporal = wrapped.with(field, (Long) val);
        return new TemporalWrapper(this.keymap, temporal);
    }
}
