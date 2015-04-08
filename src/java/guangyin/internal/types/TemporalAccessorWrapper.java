package guangyin.internal.types;

import clojure.lang.Keyword;
import clojure.lang.ILookup;
import clojure.lang.IPersistentMap;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalAccessor;

public class TemporalAccessorWrapper extends ObjectWrapper implements ILookup {
    protected IPersistentMap keymap;
    private TemporalAccessor wrapped;

    public TemporalAccessorWrapper(IPersistentMap keymap, TemporalAccessor wrapped) {
        super(wrapped);
        this.keymap = keymap;
        this.wrapped = wrapped;
    }

    public Object valAt(Object key) {
        return this.valAt(key, null);
    }

    public Object valAt(Object key, Object notFound) {
        Keyword keyword = (Keyword) key;
        TemporalField field = (TemporalField) keyword.invoke(this.keymap);
        if (field == null || !wrapped.isSupported(field)) {
            return notFound;
        }
        return wrapped.getLong(field);
    }
}
