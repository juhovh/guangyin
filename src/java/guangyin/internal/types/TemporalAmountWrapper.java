package guangyin.internal.types;

import clojure.lang.IMapEntry;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;
import java.time.temporal.TemporalUnit;
import java.time.temporal.TemporalAmount;

public class TemporalAmountWrapper extends MapObjectWrapper {
    private TemporalAmount wrapped;
    private IPersistentMap valmap;

    public TemporalAmountWrapper(IPersistentMap keymap,
                                 TemporalAmount wrapped) {
        super(keymap, wrapped);
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
}
