package guangyin.internal.types;

import java.util.Iterator;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.Seqable;
import clojure.lang.Counted;
import clojure.lang.ILookup;
import clojure.lang.IMapEntry;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalAccessor;

public class TemporalAccessorWrapper extends ObjectWrapper
  implements Iterable, Seqable, Counted, ILookup {
    private TemporalAccessor wrapped;
    protected IPersistentMap keymap;
    protected IPersistentMap valmap;

    public TemporalAccessorWrapper(IPersistentMap keymap,
                                   TemporalAccessor wrapped) {
        super(wrapped);
        this.keymap = keymap;
        this.wrapped = wrapped;
    }

    protected IPersistentMap valmap() {
        if (this.valmap == null) {
            IPersistentMap valmap = PersistentHashMap.EMPTY;
            for (Object val : keymap) {
                IMapEntry entry = (IMapEntry) val;
                TemporalField field = (TemporalField) entry.val();
                if (wrapped.isSupported(field)) {
                    valmap = valmap.assoc(entry.key(), wrapped.getLong(field));
                }
            }
            this.valmap = valmap;
        }
        return this.valmap;
    }

    public Iterator iterator() {
        return this.valmap().iterator();
    }

    public ISeq seq() {
        return this.valmap().seq();
    }

    public int count() {
        return this.valmap().count();
    }

    public Object valAt(Object key) {
        return this.valmap().valAt(key);
    }

    public Object valAt(Object key, Object notFound) {
        return this.valmap().valAt(key, notFound);
    }
}
