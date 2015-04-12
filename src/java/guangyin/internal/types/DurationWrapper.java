package guangyin.internal.types;

import clojure.lang.Keyword;
import clojure.lang.ILookup;
import clojure.lang.IPersistentMap;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.temporal.TemporalAmount;

public class DurationWrapper extends TemporalAmountWrapper implements ILookup {
    private Duration wrapped;

    public DurationWrapper(IPersistentMap keymap, Duration wrapped) {
        super(keymap, wrapped);
        this.wrapped = wrapped;
    }

    public Object valAt(Object key) {
        return this.valAt(key, null);
    }

    public Object valAt(Object key, Object notFound) {
        Keyword keyword = (Keyword) key;
        TemporalUnit unit = (TemporalUnit) keyword.invoke(this.keymap);
        if (unit == null) {
            return notFound;
        } else if (unit == ChronoUnit.MINUTES) {
            return wrapped.toMinutes();
        } else if (unit == ChronoUnit.HOURS) {
            return wrapped.toHours();
        } else if (!wrapped.getUnits().contains(unit)) {
            return notFound;
        } else {
          return wrapped.get(unit);
        }
    }
}
