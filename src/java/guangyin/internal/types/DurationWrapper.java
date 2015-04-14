package guangyin.internal.types;

import clojure.lang.BigInt;
import clojure.lang.Keyword;
import clojure.lang.ILookup;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;
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

    protected IPersistentMap valmap() {
        if (this.valmap == null) {
            // Get seconds and nano-of-second as BigInt values
            BigInt secs = BigInt.valueOf(this.wrapped.getSeconds());
            BigInt nano = BigInt.valueOf(this.wrapped.getNano());

            // Calculate milliseconds, microseconds and nanoseconds as BigInt
            BigInt millis = secs.multiply(BigInt.valueOf(1000));
            millis = millis.add(nano.quotient(BigInt.valueOf(1000000)));
            BigInt micros = secs.multiply(BigInt.valueOf(1000000));
            micros = micros.add(nano.quotient(BigInt.valueOf(1000)));
            BigInt nanos = secs.multiply(BigInt.valueOf(1000000000)).add(nano);

            this.valmap = PersistentHashMap.create(
                Keyword.intern("hours"), this.wrapped.toHours(),
                Keyword.intern("minutes"), this.wrapped.toMinutes(),
                Keyword.intern("seconds"), this.wrapped.getSeconds(),
                Keyword.intern("millis"), millis,
                Keyword.intern("micros"), micros,
                Keyword.intern("nanos"), nanos
            );
        }
        return this.valmap;
    }
}
