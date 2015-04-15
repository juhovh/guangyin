package guangyin.internal.types;

import java.util.Iterator;
import clojure.lang.ISeq;
import clojure.lang.Seqable;
import clojure.lang.Counted;
import clojure.lang.ILookup;
import clojure.lang.IMapEntry;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;
import java.time.temporal.TemporalUnit;
import java.time.temporal.TemporalAmount;

public class TemporalAmountWrapper extends ObjectWrapper
  implements Iterable, Seqable, Counted, ILookup {
    private TemporalAmount wrapped;
    protected IPersistentMap keymap;
    protected IPersistentMap valmap;

    public TemporalAmountWrapper(IPersistentMap keymap,
                                 TemporalAmount wrapped) {
        super(wrapped);
        this.keymap = keymap;
        this.wrapped = wrapped;
    }

    protected IPersistentMap valmap() {
        if (this.valmap == null) {
            IPersistentMap valmap = PersistentHashMap.EMPTY;
            for (Object val : keymap) {
                IMapEntry entry = (IMapEntry) val;
                TemporalUnit unit = (TemporalUnit) entry.val();
                if (wrapped.getUnits().contains(unit)) {
                    valmap = valmap.assoc(entry.key(), wrapped.get(unit));
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
