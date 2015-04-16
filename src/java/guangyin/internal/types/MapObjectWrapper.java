package guangyin.internal.types;

import java.util.Map;
import java.util.Iterator;
import clojure.lang.RT;
import clojure.lang.ISeq;
import clojure.lang.IMapEntry;
import clojure.lang.Associative;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;
import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentCollection;
import java.time.temporal.TemporalField;

public abstract class MapObjectWrapper extends ObjectWrapper
  implements IPersistentMap {
    protected final IPersistentMap keymap;
    private IPersistentMap valmap;
    private Object wrapped;

    public MapObjectWrapper(IPersistentMap keymap, Object wrapped) {
        super(wrapped);
        this.keymap = keymap;
        this.wrapped = wrapped;
    }

    protected abstract IPersistentMap valmap();

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

    public IPersistentCollection empty() {
        String name = this.wrapped.getClass().getSimpleName();
        throw new UnsupportedOperationException("Empty not supported by " + name);
    }

    public boolean equiv(Object obj) {
        // Unwrap the wrapped object for check if needed
        if (obj instanceof IWrapper) {
            obj = ((IWrapper) obj).deref();
        }

        return wrapped.equals(obj);
    }

    public boolean containsKey(Object key) {
        return this.valmap().containsKey(key);
    }

    public IMapEntry entryAt(Object key) {
        return this.valmap().entryAt(key);
    }

    public IPersistentMap assoc(Object key, Object val) {
        String name = this.wrapped.getClass().getSimpleName();
        throw new UnsupportedOperationException("Assoc not supported by " + name);
    }

    public IPersistentMap assocEx(Object key, Object val) {
        this.assoc(key, val);
        throw new RuntimeException("Key already present");
    }

    public IPersistentMap without(Object key) {
        String name = this.wrapped.getClass().getSimpleName();
        throw new UnsupportedOperationException("Without not supported by " + name);
    }

    // Taken from clojure.lang.APersistentMap
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
}
