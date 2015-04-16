package guangyin.internal.types;

import clojure.lang.IMapEntry;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalAccessor;

public class TemporalAccessorWrapper extends MapObjectWrapper {
    private TemporalAccessor wrapped;
    private IPersistentMap valmap;

    public TemporalAccessorWrapper(IPersistentMap keymap,
                                   TemporalAccessor wrapped) {
        super(keymap, wrapped);
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
}
