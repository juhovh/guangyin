package guangyin.internal.types;

import clojure.lang.Keyword;
import clojure.lang.ILookup;
import clojure.lang.IPersistentMap;
import java.time.temporal.TemporalUnit;
import java.time.temporal.TemporalAmount;

public class TemporalAmountWrapper extends ObjectWrapper implements ILookup {
    protected IPersistentMap keymap;
    private TemporalAmount wrapped;

    public TemporalAmountWrapper(IPersistentMap keymap, TemporalAmount wrapped) {
        super(wrapped);
        this.keymap = keymap;
        this.wrapped = wrapped;
    }

    public Object valAt(Object key) {
        return this.valAt(key, null);
    }

    public Object valAt(Object key, Object notFound) {
        Keyword keyword = (Keyword) key;
        TemporalUnit unit = (TemporalUnit) keyword.invoke(this.keymap);
        if (unit == null || !wrapped.getUnits().contains(unit)) {
            return notFound;
        }
        return wrapped.get(unit);
    }
}
