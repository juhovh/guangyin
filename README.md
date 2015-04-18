# `Guangyin` <a href="http://travis-ci.org/#!/juhovh/guangyin/builds"><img src="https://secure.travis-ci.org/juhovh/guangyin.png" /></a>

**Clojure date and time library wrapping Java 8 java.time package.**

[![Clojars Project](http://clojars.org/guangyin/latest-version.svg)](http://clojars.org/guangyin)

The Chinese have a saying “一寸光阴一寸金，寸金难买寸光阴”, which approximately
translates to "time is money, but money can't buy time". Whenever working with
date and time handling it is important to have respect for time itself. Hence
the old Chinese name for time, Guangyin.

## Usage

The most common usage is quite simple, you can parse combine and format types:

```
=> (local-date "2015-01-01")
#<LocalDate 2015-01-01>
=> (instant "2015-01-01T00:00:00Z")
#<Instant 2015-01-01T00:00:00Z>
=> (zoned-date-time (instant "2015-01-01T00:00:00Z") "Europe/Helsinki")
#<ZonedDateTime 2015-01-01T02:00+02:00[Europe/Helsinki]>
=> (str (plus (instant "2015-01-01T00:00:00Z") (hours 3)))
"2015-01-01T03:00:00Z"
```

You can also use them as you would use maps:

```
=> (into {} (local-date "2015-01-01"))
{:proleptic-month 24180, :aligned-week-of-month 1, :julian-day 2457024,
:week-based-year 2015, :epoch-day 16436, :aligned-week-of-year 1, :era 1,
:rata-die 735599, :day-of-week 4, :month-of-year 1,
:aligned-day-of-week-in-month 1, :day-of-month 1, :year 2015, :day-of-year 1,
:day-of-quarter 1, :year-of-era 2015, :aligned-day-of-week-in-year 1,
:modified-julian-day 57023, :week-of-week-based-year 1, :quarter-of-year 1}
=> (:month-of-year (local-date "2015-01-01"))
1
=> (day-of-week (local-date "2015-01-01"))
#<DayOfWeek THURSDAY>
=> (assoc (local-date "2015-01-01") :day-of-month 15)
#<LocalDate 2015-01-15>
=> (merge (local-date "2015-01-01") (month :december))
#<LocalDate 2015-12-01>
=> (merge (local-date "2015-01-01") {:year 2016 :quarter-of-year 2})
#<LocalDate 2016-04-01>
=> (merge (local-date "2015-01-01") (day-of-week :monday))
#<LocalDate 2014-12-29>
```

And you can naturally always use the system time and zone:

```
=> (str (instant :now))
"2015-01-01T00:00:00.000Z"
=> (str (offset-date-time :now))
"2015-01-01T03:00:00.000+03:00"
```

The returned objects are wrapped, but it is easy to get the Java object by
simply dereferencing the returned object. All functions accept both wrapped and
Java objects as arguments.

```
=> (class (local-time :now))
guangyin.internal.types.TemporalWrapper
=> (class @(local-time :now))
java.time.LocalTime
=> (local-time (local-date-time :now))
#<LocalTime 00:00:00.000>
=> (local-time (java.time.LocalDateTime/now))
#<LocalTime 00:00:00.000>
```

For more information, please see [API documentation](http://juhovh.github.io/guangyin/doc/).

## Disclaimer

The library is currently in a beta stage. This means that the API might change a
bit and there is some functionality still missing. That is because personally I
believe in API design coming from real needs. So if you find something weird in
the API or it doesn't work as you expected, please open an issue and let's fix
it together!

## Design Principles

Guangyin is an opinionated date and time library, which means there are several
rules that are used in designing the API:

1. Be liberal in input, conservative in output
2. Enforce use of correct abstraction for the data
3. Extensibility and readability over performance
4. Easy access for lower level methods for performance
5. Always use explicit time zones over implicit time zones
6. Overload existing Clojure functions whenever possible
7. Make error messages understandable whenever possible

This means that input types should always be coerced to the requested output if
it is possible. It should not be easy or useful to use a date time object for
describing a date only, there is a local date for a reason. Extensibility and
readability is the main focus of this library, if performance is needed it
should be achieved by calling the Java methods directly in performance critical
sections.

Time zones are the single most difficult thing in handling date and time
calculations, therefore the programmer should always state in which time zone
they are working on, also it should never be assumed that e.g. a day is 24 hours
in the API. Should never introduce a new function if it is possible and makes
any sense to overload an existing Clojure function. Proper error messages can
save lots of time in debugging, so should always aim for that.

## License

```
The MIT License (MIT)

Copyright (c) 2015 Juho Vähä-Herttua

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
