package guangyin.internal.types;

import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.MapEntry;
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

    public ISeq seq() {
        throw new UnsupportedOperationException("Seq not supported by temporal");
    }

    public int count() {
        throw new UnsupportedOperationException("Count not supported by temporal");
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
        Keyword keyword = (Keyword) key;
        TemporalField field = (TemporalField) keyword.invoke(this.keymap);
        if (field == null) {
            return false;
        }
        return wrapped.isSupported(field);
    }

    public IMapEntry entryAt(Object key) {
        Keyword keyword = (Keyword) key;
        TemporalField field = (TemporalField) keyword.invoke(this.keymap);
        if (field == null || !wrapped.isSupported(field)) {
            return null;
        }
        return new MapEntry(key, wrapped.getLong(field));
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
