package guangyin.internal.types;

import clojure.lang.Keyword;
import clojure.lang.ILookup;
import clojure.lang.IMapEntry;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentArrayMap;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalAccessor;

public class TemporalAccessorWrapper extends ObjectWrapper implements ILookup {
    private TemporalAccessor wrapped;
    protected IPersistentMap keymap;
    protected IPersistentMap valmap;

    public TemporalAccessorWrapper(IPersistentMap keymap, TemporalAccessor wrapped) {
        super(wrapped);

        this.wrapped = wrapped;
        this.keymap = keymap;

        IPersistentMap valmap = PersistentArrayMap.EMPTY;
        for (Object val : keymap) {
            IMapEntry entry = (IMapEntry) val;
            TemporalField field = (TemporalField) entry.val();
            if (wrapped.isSupported(field)) {
                valmap = valmap.assoc(entry.key(), wrapped.getLong(field));
            }
        }
        this.valmap = valmap;
    }

    public Object valAt(Object key) {
        return this.valmap.valAt(key);
    }

    public Object valAt(Object key, Object notFound) {
        return this.valmap.valAt(key, notFound);
    }
}
