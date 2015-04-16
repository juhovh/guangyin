package guangyin.internal.types;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateTimeFormatterWrapper extends FnObjectWrapper {
    private DateTimeFormatter wrapped;

    public DateTimeFormatterWrapper(DateTimeFormatter wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    public Object invoke(Object arg1) {
        // Unwrap the wrapped object if needed
        if (arg1 instanceof IWrapper) {
            arg1 = ((IWrapper) arg1).deref();
        }

        // Types of TemporalAccessor can be formatted
        if (arg1 instanceof TemporalAccessor) {
            return this.wrapped.format((TemporalAccessor) arg1);
        }

        // Unsupported type, throw an exception
        String name = arg1.getClass().getSimpleName();
        throw new IllegalArgumentException("Could not format " + name);
    }
}
