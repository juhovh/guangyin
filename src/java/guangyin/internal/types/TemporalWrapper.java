package guangyin.internal.types;

import clojure.lang.IPersistentMap;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;

public class TemporalWrapper extends TemporalAccessorWrapper {
    private Temporal wrapped;

    public TemporalWrapper(IPersistentMap keymap, Temporal wrapped) {
        super(keymap, wrapped);
        this.wrapped = wrapped;
    }

    public IPersistentMap assoc(Object key, Object val) {
        TemporalField field = (TemporalField) this.keymap.valAt(key);
        if (field == null || !this.wrapped.isSupported(field)) {
            String fname = key.toString();
            String name = this.wrapped.getClass().getSimpleName();
            String msg = "Field " + fname + " not supported for " + name;
            throw new IllegalArgumentException(msg);
        }
        Temporal temporal = wrapped.with(field, (Long) val);
        return new TemporalWrapper(this.keymap, temporal);
    }
}
