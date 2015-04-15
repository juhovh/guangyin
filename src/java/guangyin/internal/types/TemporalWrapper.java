package guangyin.internal.types;

import java.util.Map;
import clojure.lang.RT;
import clojure.lang.ISeq;
import clojure.lang.IMapEntry;
import clojure.lang.Associative;
import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentCollection;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;

public class TemporalWrapper extends TemporalAccessorWrapper
  implements IPersistentMap {
    private Temporal wrapped;

    public TemporalWrapper(IPersistentMap keymap, Temporal wrapped) {
        super(keymap, wrapped);
        this.wrapped = wrapped;
    }

    public IPersistentCollection cons(Object o) {
        if (o instanceof Map.Entry) {
            Map.Entry e = (Map.Entry) o;
            return assoc(e.getKey(), e.getValue());
        } else if (o instanceof IPersistentVector) {
            IPersistentVector v = (IPersistentVector) o;
            if (v.count() != 2) {
                throw new IllegalArgumentException("Vector arg to temporal conj must be a pair");
            }
            return assoc(v.nth(0), v.nth(1));
        }

        IPersistentMap ret = this;
        for (ISeq es = RT.seq(o); es != null; es = es.next()) {
          Map.Entry e = (Map.Entry) es.first();
          ret = ret.assoc(e.getKey(), e.getValue());
        }
        return ret;
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

    public IPersistentMap assocEx(Object key, Object val) {
        this.assoc(key, val);
        throw new RuntimeException("Key already present");
    }

    public IPersistentMap without(Object key) {
        throw new UnsupportedOperationException("Without not supported by temporal");
    }
}
