package guangyin.internal.types;

import clojure.lang.IDeref;

public class ObjectWrapper implements Comparable, IDeref {
    private Object wrapped;

    public ObjectWrapper(Object wrapped) {
        if (wrapped == null) {
            throw new IllegalArgumentException("Wrapped object cannot be null");
        }
        this.wrapped = wrapped;
    }

    public boolean equals(Object obj) {
        return this.wrapped.equals(obj);
    }

    public int hashCode() {
        return this.wrapped.hashCode();
    }

    public String toString() {
        return this.wrapped.toString();
    }

    @SuppressWarnings("unchecked")
    public int compareTo(Object o) {
        // Unwrap the wrapped object for comparison if needed
        if (o instanceof ObjectWrapper) {
            o = ((ObjectWrapper) o).deref();
        }
        return ((Comparable) this.wrapped).compareTo(o);
    }

    public Object deref() {
        return this.wrapped;
    }
}
