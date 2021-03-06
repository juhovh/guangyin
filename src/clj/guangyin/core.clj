(ns guangyin.core
  "The core namespace for basic date and time handling."
  (:require [guangyin.internal.fields :as fields]
            [guangyin.internal.types :refer :all])
  (:import (java.time Clock Duration Instant LocalDate LocalDateTime LocalTime
                      MonthDay OffsetDateTime OffsetTime Period Year YearMonth
                      ZonedDateTime ZoneId ZoneOffset DayOfWeek Month)))

(declare local-date zone-id zone-offset)

(defn instant?
  "Returns true if the given value is an instant.
   Examples:

    => (instant? (instant :now))
    true
    => (instant? (days 1))
    false"
  [x]
  (instance? Instant (unwrap x)))

(defprotocol PInstant
  (instant [this]
   "Coerce to instant.
    Examples:

      => (instant :now) ; Current instant
      #<Instant 2015-01-01T12:15:00.123Z>
      => (instant (clock)) ; Current instant from clock
      #<Instant 2015-01-01T12:15:00.123Z>
      => (instant \"2015-01-01T12:15:00.123Z\")
      #<Instant 2015-01-01T12:15:00.123Z>
      => (instant :epoch)
      #<Instant 1970-01-01T00:00:00Z>
      => (instant (offset-date-time :now)) ; From date-time containing instant
      #<Instant 2015-01-01T12:15:00.123Z>"))

(extend-protocol PInstant
  guangyin.internal.types.IWrapper
  (instant [this] (instant @this))
  clojure.lang.Keyword
  (instant [this] (if (= this :now)
                      (wrap (Instant/now))
                      (wrap (fields/get-field fields/instants this))))
  java.time.temporal.TemporalAccessor
  (instant [this] (wrap (Instant/from this)))
  java.time.Clock
  (instant [this] (wrap (Instant/now this)))
  java.lang.String
  (instant [this] (wrap (Instant/parse this))))

(defn duration?
  "Returns true if the given value is a duration.
   Duration refers to an exact length of time that can be converted accurately
   to seconds without any time zone information, basically any length of time
   that is measured in hours or smaller units than that.
   Examples:

     => (duration? (hours 1))
     true
     => (duration? (days 1))
     false"
  [x]
  (instance? Duration (unwrap x)))

(defprotocol PDuration
  (duration [this] [this other]
   "Coerce to duration.
    Notice that duration is only for exact times, if you are working with dates
    you most likely want to use a period instead. Can also be used to create a
    duration from start and end times. See also functions hours, minutes,
    seconds and nanos, they all return a duration.
    Examples:

      => (duration \"P2D\") ; 2 days is converted to 48 hours
      #<Duration PT48H>
      => (duration \"PT15M\") ; 15 minutes
      #<Duration PT15M>
      => (duration \"PT-6H3M\") ; -6 hours and +3 minutes
      #<Duration PT-5H-57M>
      => (duration \"-PT6H3M\") ; -6 hours and -3 minutes
      #<Duration PT-6H-3M>
      => (duration (instant :epoch) (instant \"2015-01-01T12:15:00.123Z\"))
      #<Duration PT394476H15M0.123S>
      => (duration (local-time :midnight) (local-time :noon))
      #<Duration PT12H>"))

(extend-protocol PDuration
  guangyin.internal.types.IWrapper
  (duration ([this] (duration @this))
            ([this other] (duration @this other)))
  clojure.lang.Keyword
  (duration [this] (wrap (fields/get-field fields/durations this)))
  java.time.temporal.Temporal
  (duration [this other] (wrap (Duration/between this (unwrap other))))
  java.time.temporal.TemporalAmount
  (duration [this] (wrap (Duration/from this)))
  java.lang.String
  (duration [this] (wrap (Duration/parse this))))

(defn period?
  "Returns true if the given value is a period.
   Period refers to a length of time that can not be converted accurately to
   seconds without any time zone information, basically any length of time that
   is measured in days or larger units than that.
   Examples:

     => (period? (weeks 1))
     true
     => (period? (minutes 30))
     false"
  [x]
  (instance? Period (unwrap x)))

(defprotocol PPeriod
  (period [this] [this other]
   "Coerce to period.
    Notice that period is only for days and larger time units. If you want to
    work with time you should use duration instead. Can also be used to create a
    period from start and end date. See also functions years, months, weeks and
    days, they all return a period.
    Examples:

      => (period \"P2Y\")
      #<Period P2Y>
      => (period \"P1Y2M3W4D\")
      #<Period P1Y2M25D>
      => (period \"P-1Y2M\")
      #<Period P-1Y2M>
      => (period \"-P1Y2M\")
      #<Period P-1Y-2M>
      => (period :zero)
      #<Period P0D>
      => (period (local-date \"2015-01-01\") (local-date \"2017-04-12\"))
      #<Period P2Y3M11D>"))
  
(extend-protocol PPeriod
  guangyin.internal.types.IWrapper
  (period ([this] (period @this))
          ([this other] (period @this other)))
  clojure.lang.Keyword
  (period [this] (wrap (fields/get-field fields/periods this)))
  java.time.temporal.TemporalAmount
  (period [this] (wrap (Period/from this)))
  java.time.chrono.ChronoLocalDate
  (period [this other] (wrap (Period/between @(local-date this)
                                             @(local-date other))))
  java.lang.String
  (period [this] (wrap (Period/parse this))))

(defn local-date?
  "Returns true if the given value is a local date.
   Examples:

     => (local-date? (local-date :now))
     true
     => (local-date? (local-date-time :now))
     false"
  [x]
  (instance? LocalDate (unwrap x)))

(defprotocol PLocalDate
  (local-date [this] [this param] [year month date]
   "Coerce to local date.
    Can also be used to parse a local date using a custom formatter, or to
    create a date from year, month and day of month.
    Examples:

      => (local-date :now) ; Current date
      #<LocalDate 2015-04-01>
      => (local-date (clock))
      #<LocalDate 2015-04-01>
      => (local-date (zone-id \"Asia/Shanghai\")) ; Current date in Shanghai
      #<LocalDate 2015-04-02>
      => (local-date \"2015-04-01\")
      #<LocalDate 2015-04-01>
      => (local-date :max) ; Largest local date, also :min supported
      #<LocalDate +999999999-12-31>
      => (local-date (offset-date-time :now))
      #<LocalDate 2015-04-01>
      => (local-date \"01.04.2015\" (date-time-formatter \"dd.MM.yyyy\"))
      #<LocalDate 2015-04-01>
      => (local-date 2015 4 1)
      #<LocalDate 2015-04-01>"))

(extend-protocol PLocalDate
  guangyin.internal.types.IWrapper
  (local-date [this] (local-date @this))
  clojure.lang.Keyword
  (local-date [this] (if (= this :now)
                         (wrap (LocalDate/now))
                         (wrap (fields/get-field fields/local-dates this))))
  java.time.temporal.TemporalAccessor
  (local-date [this] (wrap (LocalDate/from this)))
  java.time.Clock
  (local-date [this] (wrap (LocalDate/now this)))
  java.time.ZoneId
  (local-date [this] (wrap (LocalDate/now this)))
  java.lang.String
  (local-date ([this] (wrap (LocalDate/parse this)))
              ([this param] (wrap (LocalDate/parse this (unwrap param)))))
  java.lang.Long
  (local-date [year month day] (wrap (LocalDate/of year month day))))

(defn local-time?
  "Returns true if the given value is a local time.
   Examples:

     => (local-time? (local-time :now))
     true
     => (local-time? (local-date-time :now))
     false"
  [x]
  (instance? LocalTime (unwrap x)))

(defprotocol PLocalTime
  (local-time [this] [this param] [hour minute second]
              [hour minute second nanosecond]
   "Coerce to local time.
    Can also be used to parse a local time using a custom formatter, or to
    create a time from hours, minutes, seconds and nanoseconds.
    Examples:

      => (local-time :now) ; Current time
      #<LocalTime 12:15:00.123>
      => (local-time (clock))
      #<LocalTime 12:15:00.123>
      => (local-time (zone-id \"Asia/Shanghai\")) ; Current time in Shanghai
      #<LocalTime 17:15:00.123>
      => (local-time \"12:15\")
      #<LocalTime 12:15>
      => (local-time \"12:15:00.123\")
      #<LocalTime 12:15:00.123>
      => (local-time :noon)
      #<LocalTime 12:00>
      => (local-time (local-date-time :now)) ; Time part of local date-time
      #<LocalTime 12:15:00.123>
      => (local-time \"12.15\" (date-time-formatter \"HH.mm\"))
      #<LocalTime 12:15>
      => (local-time 12 15)
      #<LocalTime 12:15>
      => (local-time 12 15 0)
      #<LocalTime 12:15>
      => (local-time 12 15 0 123000000)
      #<LocalTime 12:15:00.123>"))

(extend-protocol PLocalTime
  guangyin.internal.types.IWrapper
  (local-time [this] (local-time @this))
  clojure.lang.Keyword
  (local-time [this] (if (= this :now)
                         (wrap (LocalTime/now))
                         (wrap (fields/get-field fields/local-times this))))
  java.time.temporal.TemporalAccessor
  (local-time [this] (wrap (LocalTime/from this)))
  java.time.Clock
  (local-time [this] (wrap (LocalTime/now this)))
  java.time.ZoneId
  (local-time [this] (wrap (LocalTime/now this)))
  java.lang.String
  (local-time ([this] (wrap (LocalTime/parse this)))
              ([this param] (wrap (LocalTime/parse this (unwrap param)))))
  java.lang.Long
  (local-time ([hour minute] (wrap (LocalTime/of hour minute)))
              ([hour minute second] (wrap (LocalTime/of hour minute second)))
              ([hour minute second nanosecond]
               (wrap (LocalTime/of hour minute second nanosecond)))))

(defn offset-time?
  "Returns true if the given value is a time with a zone offset.
   Examples:

     => (offset-time? (offset-time :now))
     true
     => (offset-time? (offset-date-time :now))
     false"
  [x] (instance? OffsetTime (unwrap x)))

(defprotocol POffsetTime
  (offset-time [this] [this param] [hour minute second nanosecond offset]
   "Coerce to time with offset.
    Can also be used to parse a time with offset using a custom formatter, or to
    create a time with offset from hours, minutes, seconds and nanoseconds.
    Examples:

      => (offset-time :now) ; Current time with offset
      #<OffsetTime 12:15:00.123+03:00>
      => (offset-time (clock))
      #<OffsetTime 12:15:00.123+03:00>
      => (offset-time (zone-id \"Asia/Shanghai\"))
      #<OffsetTime 17:15:00.123+08:00>
      => (offset-time \"12:15:00.123+03:00\")
      #<OffsetTime 12:15:00.123+03:00>
      => (offset-time :max)
      #<OffsetTime 23:59:59.999999999-18:00>
      => (offset-time (offset-date-time :now))
      #<OffsetTime 12:15:00.123+03:00>
      => (offset-time (local-time 12 15) (zone-offset (hours 3)))
      #<OffsetTime 12:15+03:00>
      => (offset-time (instant :now) (zone-id \"Europe/Helsinki\"))
      #<OffsetTime 12:15:00.123+03:00>
      => (offset-time \"12.15+0300\" (date-time-formatter \"HH.mmx\"))
      #<OffsetTime 12:15+03:00>
      => (offset-time 12 15 0 123000000 (zone-offset (hours 3)))
      #<OffsetTime 12:15:00.123+03:00>"))

(extend-protocol POffsetTime
  guangyin.internal.types.IWrapper
  (offset-time ([this] (offset-time @this))
               ([this param] (offset-time @this param)))
  clojure.lang.Keyword
  (offset-time [this] (if (= this :now)
                         (wrap (OffsetTime/now))
                         (wrap (fields/get-field fields/offset-times this))))
  java.time.LocalTime
  (offset-time [this param]
    (wrap (OffsetTime/of this @(zone-offset param))))
  java.time.Instant
  (offset-time [this param]
    (wrap (OffsetTime/ofInstant this @(zone-id param))))
  java.time.temporal.TemporalAccessor
  (offset-time [this] (wrap (OffsetTime/from this)))
  java.time.Clock
  (offset-time [this] (wrap (OffsetTime/now this)))
  java.time.ZoneId
  (offset-time [this] (wrap (OffsetTime/now this)))
  java.lang.String
  (offset-time ([this] (wrap (OffsetTime/parse this)))
               ([this param] (wrap (OffsetTime/parse this (unwrap param)))))
  java.lang.Long
  (offset-time ([hour minute second nanosecond offset]
                (wrap (OffsetTime/of hour minute second nanosecond
                                     @(zone-offset offset))))))

(defn local-date-time?
  "Returns true if the given value is a local date-time.
   Examples:

     => (local-date-time? (local-date-time :now))
     true
     => (local-date-time? (offset-date-time :now))
     false"
  [x] (instance? LocalDateTime (unwrap x)))

(defprotocol PLocalDateTime
  (local-date-time [this] [this param] [year month day hour minute]
                   [year month day hour minute second]
                   [year month day hour minute second nanosecond]
  "Coerce to local date-time.
   Examples:

     => (local-date-time :now)
     #<LocalDateTime 2015-04-16T12:15:00.123>
     => (local-date-time (clock))
     #<LocalDateTime 2015-04-16T12:15:00.123>
     => (local-date-time (zone-id \"Asia/Shanghai\"))
     #<LocalDateTime 2015-04-16T17:15:00.123>
     => (local-date-time \"2015-04-16T12:15:00.123\")
     #<LocalDateTime 2015-04-16T12:15:00.123>
     => (local-date-time \"16.04.2015 12.15\" (date-time-formatter \"dd.MM.yyyy HH.mm\"))
     #<LocalDateTime 2015-04-16T12:15>
     => (local-date-time :max)
     #<LocalDateTime +999999999-12-31T23:59:59.999999999>
     => (local-date-time (offset-date-time :now))
     #<LocalDateTime 2015-04-16T12:15:00.123>
     => (local-date-time (instant :now) (zone-id \"Asia/Shanghai\"))
     #<LocalDateTime 2015-04-16T17:15:00.123>
     => (local-date-time (local-date :now) (local-time :now))
     #<LocalDateTime 2015-04-16T12:15:00.123>
     => (local-date-time 2015 4 16 12 15)
     #<LocalDateTime 2015-04-16T12:15>
     => (local-date-time 2015 4 16 12 15 0)
     #<LocalDateTime 2015-04-16T12:15>
     => (local-date-time 2015 4 16 12 15 0 123000000)
     #<LocalDateTime 2015-04-16T12:15:00.123>"))

(extend-protocol PLocalDateTime
  guangyin.internal.types.IWrapper
  (local-date-time ([this] (local-date-time @this))
                   ([this param] (local-date-time @this param)))
  clojure.lang.Keyword
  (local-date-time [this] (if (= this :now)
                              (wrap (LocalDateTime/now))
                              (wrap (fields/get-field fields/local-date-times
                                                      this))))
  java.time.chrono.ChronoLocalDate
  (local-date-time [this param] (wrap (LocalDateTime/of @(local-date this)
                                                        (unwrap param))))
  java.time.Instant
  (local-date-time [this param]
    (wrap (LocalDateTime/ofInstant this @(zone-id param))))
  java.time.temporal.TemporalAccessor
  (local-date-time [this] (wrap (LocalDateTime/from this)))
  java.time.Clock
  (local-date-time [this] (wrap (LocalDateTime/now this)))
  java.time.ZoneId
  (local-date-time [this] (wrap (LocalDateTime/now this)))
  java.lang.String
  (local-date-time ([this] (wrap (LocalDateTime/parse this)))
                   ([this param]
                    (wrap (LocalDateTime/parse this (unwrap param)))))
  java.lang.Long
  (local-date-time ([year month day hour minute]
                    (wrap (LocalDateTime/of year month day hour minute)))
                   ([year month day hour minute second]
                    (wrap (LocalDateTime/of year month day hour minute second)))
                   ([year month day hour minute second nanosecond]
                    (wrap (LocalDateTime/of year month day hour minute second
                                            nanosecond)))))

(defn offset-date-time?
  "Returns true if the given value is a date-time with a zone offset.
   Examples:

     => (offset-date-time? (offset-date-time :now))
     true
     => (offset-date-time? (zoned-date-time :now))
     false"
  [x] (instance? OffsetDateTime (unwrap x)))

(defprotocol POffsetDateTime
  (offset-date-time [this] [this param] [date time offset]
                    [year month day hour minute second nanosecond offset]
   "Coerce to date-time with offset.
    Examples:

     => (offset-date-time :now)
     #<OffsetDateTime 2015-04-16T12:15:00.123+03:00>
     => (offset-date-time (clock))
     #<OffsetDateTime 2015-04-16T12:15:00.123+03:00>
     => (offset-date-time (zone-id \"Asia/Shanghai\"))
     #<OffsetDateTime 2015-04-16T17:15:00.123+08:00>
     => (offset-date-time \"2015-04-16T12:15:00.123+03:00\")
     #<OffsetDateTime 2015-04-16T12:15:00.123+03:00>
     => (offset-date-time \"16.04.2015 12.15+0300\" (date-time-formatter \"dd.MM.yyyy HH.mmx\"))
     #<OffsetDateTime 2015-04-16T12:15+03:00>
     => (offset-date-time :max)
     #<OffsetDateTime +999999999-12-31T23:59:59.999999999-18:00>
     => (offset-date-time (zoned-date-time :now))
     #<OffsetDateTime 2015-04-16T12:15:00.123+03:00>
     => (offset-date-time (instant :now) (zone-id \"Asia/Shanghai\"))
     #<OffsetDateTime 2015-04-16T17:15:00.123+08:00>
     => (offset-date-time (local-date-time :now) (zone-offset (hours 3)))
     #<OffsetDateTime 2015-04-16T12:15:00.123+03:00>
     => (offset-date-time (local-date :now) (local-time :now) (zone-offset (hours 3)))
     #<OffsetDateTime 2015-04-16T12:15:00.123+03:00>
     => (offset-date-time 2015 4 16 12 15 0 123000000 (zone-offset (hours 3)))
     #<OffsetDateTime 2015-04-16T12:15:00.123>"))

(extend-protocol POffsetDateTime
  guangyin.internal.types.IWrapper
  (offset-date-time ([this] (offset-date-time @this))
                    ([this param] (offset-date-time @this param))
                    ([date time offset] (offset-date-time @date time offset)))
  clojure.lang.Keyword
  (offset-date-time [this] (if (= this :now)
                               (wrap (OffsetDateTime/now))
                               (wrap (fields/get-field fields/offset-date-times
                                                       this))))
  java.time.LocalDateTime
  (offset-date-time [this param]
    (wrap (OffsetDateTime/of this @(zone-offset param))))
  java.time.chrono.ChronoLocalDate
  (offset-date-time [date time offset]
    (wrap (OffsetDateTime/of @(local-date date) (unwrap time) @(zone-offset offset))))
  java.time.Instant
  (offset-date-time [this param]
    (wrap (OffsetDateTime/ofInstant this @(zone-id param))))
  java.time.temporal.TemporalAccessor
  (offset-date-time [this] (wrap (OffsetDateTime/from this)))
  java.time.Clock
  (offset-date-time [this] (wrap (OffsetDateTime/now this)))
  java.time.ZoneId
  (offset-date-time [this] (wrap (OffsetDateTime/now this)))
  java.lang.String
  (offset-date-time ([this] (wrap (OffsetDateTime/parse this)))
                    ([this param]
                     (wrap (OffsetDateTime/parse this (unwrap param)))))
  java.lang.Long
  (offset-date-time ([year month day hour minute second nanosecond offset]
                     (wrap (OffsetDateTime/of year month day hour minute second
                                              nanosecond
                                              @(zone-offset offset))))))

(defn zoned-date-time?
  "Returns true if the given value is a date-time with a time zone.
   Examples:

     => (zoned-date-time? (zoned-date-time :now))
     true
     => (zoned-date-time? (offset-date-time :now))
     false"
  [x] (instance? ZonedDateTime (unwrap x)))

(defprotocol PZonedDateTime
  (zoned-date-time [this] [this param] [date time zone]
                   [year month day hour minute second nanosecond zone]
   "Coerce to date-time with zone.
    Examples:

      => (zoned-date-time :now)
      #<ZonedDateTime 2015-04-16T12:15:00.123+03:00[Europe/Helsinki]>
      => (zoned-date-time (clock))
      #<ZonedDateTime 2015-04-16T12:15:00.123+03:00[Europe/Helsinki]>
      => (zoned-date-time (zone-id \"Asia/Shanghai\"))
      #<ZonedDateTime 2015-04-16T17:15:00.123+08:00[Asia/Shanghai]>
      => (zoned-date-time \"2015-04-16T12:15:00.123+03:00[Europe/Helsinki]\")
      #<ZonedDateTime 2015-04-16T12:15:00.123+03:00[Europe/Helsinki]>
      => (zoned-date-time \"2015-04-16T12:15:00.123+03:00[Europe/Helsinki]\"
           (date-time-formatter :iso-date-time))
      #<ZonedDateTime 2015-04-16T12:15:00.123+03:00[Europe/Helsinki]>
      => (zoned-date-time (instant :now) (zone-offset (hours 3)))
      #<ZonedDateTime 2015-04-16T12:15:00.123+03:00>
      => (zoned-date-time (instant :now) (zone-id \"Europe/Helsinki\"))
      #<ZonedDateTime 2015-04-16T12:15:00.123+03:00[Europe/Helsinki]>
      => (zoned-date-time (local-date-time :now) (zone-id \"Europe/Helsinki\"))
      #<ZonedDateTime 2015-04-16T12:15:00.123+03:00[Europe/Helsinki]>
      => (zoned-date-time (local-date :now) (local-time :now) (zone-id \"Europe/Helsinki\"))
      #<ZonedDateTime 2015-04-16T12:15:00.123+03:00[Europe/Helsinki]>
      => (zoned-date-time 2015 4 16 12 15 0 123000000 (zone-id \"Europe/Helsinki\"))
      #<ZonedDateTime 2015-04-16T12:15:00.123+03:00[Europe/Helsinki]>"))

(extend-protocol PZonedDateTime
  guangyin.internal.types.IWrapper
  (zoned-date-time ([this] (zoned-date-time @this))
                   ([this param] (zoned-date-time @this param))
                   ([date time zone] (zoned-date-time @date time zone)))
  clojure.lang.Keyword
  (zoned-date-time [this] (if (= this :now)
                              (wrap (ZonedDateTime/now))
                              (throw (IllegalArgumentException.
                                       (str "Invalid key " this)))))
  java.time.LocalDateTime
  (zoned-date-time [this param]
    (wrap (ZonedDateTime/of this @(zone-id param))))
  java.time.chrono.ChronoLocalDate
  (zoned-date-time [date time zone]
    (wrap (ZonedDateTime/of @(local-date date) (unwrap time) @(zone-id zone))))
  java.time.Instant
  (zoned-date-time [this param]
    (wrap (ZonedDateTime/ofInstant this @(zone-id param))))
  java.time.temporal.TemporalAccessor
  (zoned-date-time [this] (wrap (ZonedDateTime/from this)))
  java.time.Clock
  (zoned-date-time [this] (wrap (ZonedDateTime/now this)))
  java.time.ZoneId
  (zoned-date-time [this] (wrap (ZonedDateTime/now this)))
  java.lang.String
  (zoned-date-time ([this] (wrap (ZonedDateTime/parse this)))
                   ([this param]
                    (wrap (ZonedDateTime/parse this (unwrap param)))))
  java.lang.Long
  (zoned-date-time ([year month day hour minute second nanosecond zone]
                     (wrap (ZonedDateTime/of year month day hour minute second
                                              nanosecond @(zone-id zone))))))

(defn year?
  "Returns true if the given value is an exact year."
  [x] (instance? Year (unwrap x)))

(defprotocol PYear
  (year [this] [this param]
   "Coerce to year.
    Examples:

      => (year :now)
      #<Year 2015>
      => (year (clock))
      #<Year 2015>
      => (year (zone-id \"Asia/Shanghai\"))
      #<Year 2015>
      => (year \"2015\")
      #<Year 2015>
      => (year \"2015\" (date-time-formatter \"yyyy\"))
      #<Year 2015>
      => (year :max)
      #<Year 999999999>
      => (year (local-date :now))
      #<Year 2015>
      => (year 2015)
      #<Year 2015>"))

(extend-protocol PYear
  guangyin.internal.types.IWrapper
  (year [this] (year @this))
  clojure.lang.Keyword
  (year [this] (case this
                 :now (wrap (Year/now))
                 :min (wrap (Year/of (fields/year-values :min-value)))
                 :max (wrap (Year/of (fields/year-values :max-value)))
                 (throw (IllegalArgumentException.
                          (str "Invalid key " this)))))
  java.time.temporal.TemporalAccessor
  (year [this] (wrap (Year/from this)))
  java.time.Clock
  (year [this] (wrap (Year/now this)))
  java.time.ZoneId
  (year [this] (wrap (Year/now this)))
  java.lang.String
  (year ([this] (wrap (Year/parse this)))
        ([this param] (wrap (Year/parse this (unwrap param)))))
  java.lang.Long
  (year [this] (wrap (Year/of this))))

(defn year-month?
  "Returns true if the given value is a combination of year and month."
  [x] (instance? YearMonth (unwrap x)))

(defprotocol PYearMonth
  (year-month [this] [year month]
   "Coerce to year and month.
    Examples:

      => (year-month :now)
      #<YearMonth 2015-04>
      => (year-month (clock))
      #<YearMonth 2015-04>
      => (year-month (zone-id \"Asia/Shanghai\"))
      #<YearMonth 2015-04>
      => (year-month \"2015-04\")
      #<YearMonth 2015-04>
      => (year-month \"04.2015\" (date-time-formatter \"MM.yyyy\"))
      #<YearMonth 2015-04>
      => (year-month (local-date :now))
      #<YearMonth 2015-04>
      => (year-month 2015 4)
      #<YearMonth 2015-04>"))

(extend-protocol PYearMonth
  guangyin.internal.types.IWrapper
  (year-month [this] (year-month @this))
  clojure.lang.Keyword
  (year-month [this] (if (= this :now)
                         (wrap (YearMonth/now))
                         (throw (IllegalArgumentException.
                                  (str "Invalid key " this)))))
  java.time.temporal.TemporalAccessor
  (year-month [this] (wrap (YearMonth/from this)))
  java.time.Clock
  (year-month [this] (wrap (YearMonth/now this)))
  java.time.ZoneId
  (year-month [this] (wrap (YearMonth/now this)))
  java.lang.String
  (year-month ([this] (wrap (YearMonth/parse this)))
              ([this param] (wrap (YearMonth/parse this (unwrap param)))))
  java.lang.Long
  (year-month [year month] (wrap (YearMonth/of year month))))

(defn month?
  "Returns true if the given value is an exact month.
   Examples:

     => (month? (month 5))
     true
     => (month? (months 2))
     false"
  [x] (instance? Month (unwrap x)))

(defprotocol PMonth
  (month [this]
   "Coerce to month.
    Examples:

      => (month :now)
      #<Month APRIL>
      => (month (clock))
      #<Month APRIL>
      => (month (zone-id \"Asia/Shanghai\"))
      #<Month APRIL>
      => (month (local-date :now))
      #<Month APRIL>
      => (month :january)
      #<Month JANUARY>
      => (month :december)
      #<Month DECEMBER>"))

(extend-protocol PMonth
  guangyin.internal.types.IWrapper
  (month [this] (month @this))
  clojure.lang.Keyword
  (month [this] (if (= this :now)
                    (wrap (Month/from (LocalDate/now)))
                    (wrap (fields/get-field fields/months this))))
  java.time.temporal.TemporalAccessor
  (month [this] (wrap (Month/from this)))
  java.time.Clock
  (month [this] (wrap (Month/from (LocalDate/now this))))
  java.time.ZoneId
  (month [this] (wrap (Month/from (LocalDate/now this))))
  java.lang.Long
  (month [this] (wrap (Month/of this))))

(defn month-day?
  "Returns true if the given value is a combination of month and day of month."
  [x] (instance? MonthDay (unwrap x)))

(defprotocol PMonthDay
  (month-day [this] [month day]
   "Coerce to month and day.
    Examples:

      => (month-day :now)
      #<MonthDay --04-16>
      => (month-day (clock))
      #<MonthDay --04-16>
      => (month-day (zone-id \"Asia/Shanghai\"))
      #<MonthDay --04-16>
      => (month-day \"--04-16\")
      #<MonthDay --04-16>
      => (month-day \"16.04.\" (date-time-formatter \"dd.MM.\"))
      #<MonthDay --04-16>
      => (month-day (local-date :now))
      #<MonthDay --04-16>
      => (month-day 4 16)
      #<MonthDay --04-16>"))

(extend-protocol PMonthDay
  guangyin.internal.types.IWrapper
  (month-day ([this] (month-day @this))
             ([month day] (month-day @month day)))
  clojure.lang.Keyword
  (month-day [this] (if (= this :now)
                        (wrap (MonthDay/now))
                        (throw (IllegalArgumentException.
                                 (str "Invalid key " this)))))
  java.time.Month
  (month-day [month day] (wrap (MonthDay/of month day)))
  java.time.temporal.TemporalAccessor
  (month-day [this] (wrap (MonthDay/from this)))
  java.time.Clock
  (month-day [this] (wrap (MonthDay/now this)))
  java.time.ZoneId
  (month-day [this] (wrap (MonthDay/now this)))
  java.lang.String
  (month-day ([this] (wrap (MonthDay/parse this)))
             ([this param] (wrap (MonthDay/parse this (unwrap param)))))
  java.lang.Long
  (month-day [month day] (wrap (MonthDay/of month day))))

(defn day-of-week?
  "Returns true if the given value is an exact day of week.
   Examples:

     => (day-of-week? (day-of-week :saturday))
     true
     => (day-of-week? (days 5))
     false"
  [x] (instance? DayOfWeek (unwrap x)))

(defprotocol PDayOfWeek
  (day-of-week [this]
   "Coerce to day of week.
    Examples:

      => (day-of-week :now)
      => (day-of-week (clock))
      => (day-of-week (zone-id \"Asia/Shanghai\"))
      => (day-of-week (local-date :now))
      => (day-of-week :monday)
      => (day-of-week :saturday)"))

(extend-protocol PDayOfWeek
  guangyin.internal.types.IWrapper
  (day-of-week [this] (day-of-week @this))
  clojure.lang.Keyword
  (day-of-week [this] (if (= this :now)
                          (wrap (DayOfWeek/from (LocalDate/now)))
                          (wrap (fields/get-field fields/day-of-weeks this))))
  java.time.temporal.TemporalAccessor
  (day-of-week [this] (wrap (DayOfWeek/from this)))
  java.time.Clock
  (day-of-week [this] (wrap (DayOfWeek/from (LocalDate/now this))))
  java.time.ZoneId
  (day-of-week [this] (wrap (DayOfWeek/from (LocalDate/now this))))
  java.lang.Long
  (day-of-week [this] (wrap (DayOfWeek/of this))))

(defn zone-id?
  "Returns true if the given value is a time zone.
   Examples:

     => (zone-id? (zone-id \"Europe/Helsinki\"))
     true
     => (zone-id? (zone-offset :utc))
     true
     => (zone-id? (hours 1))
     false"
  [x] (instance? ZoneId (unwrap x)))

(defprotocol PZoneId
  (zone-id [this] [this param]
   "Create zone-id from values.
    If created from prefix and offset, the valid prefix values are \"GMT\",
    \"UTC\", \"UT\" and \"\". This is usually not very useful and should be
    avoided unless necessary.
    Examples:

      => (zone-id :default) ; Default zone of this computer
      #<ZoneRegion Europe/Helsinki>
      => (zone-id \"America/New_York\")
      #<ZoneRegion America/New_York>
      => (zone-id (zoned-date-time :now)) ; Zone from date-time
      #<ZoneRegion Europe/Helsinki>
      => (zone-id \"UTC\" (zone-offset \"+08:00\")) ; For completeness
      #<ZoneRegion UTC+08:00>"))

(extend-protocol PZoneId
  guangyin.internal.types.IWrapper
  (zone-id [this] (zone-id @this))
  clojure.lang.Keyword
  (zone-id [this] (if (= this :default)
                      (wrap (ZoneId/systemDefault))
                      (throw (IllegalArgumentException.
                               (str "Invalid key " this)))))
  java.time.temporal.TemporalAccessor
  (zone-id [this] (wrap (ZoneId/from this)))
  java.time.ZoneId
  (zone-id [this] (wrap this))
  java.lang.String
  (zone-id ([this] (wrap (ZoneId/of this)))
           ([prefix offset]
            (wrap (ZoneId/ofOffset prefix @(zone-offset offset))))))

(defn zone-offset?
  "Returns true if the given value is a zone offset.
   Examples:

     => (zone-offset? (zone-offset :utc))
     true
     => (zone-offset? (zone-id \"Europe/Helsinki\"))
     false"
  [x] (instance? ZoneOffset (unwrap x)))

(defprotocol PZoneOffset
  (zone-offset [this] [hours minutes]
   "Coerce to zone-offset.
    Getting current offset can be found using offset-time, however for current
    zone zone-id should usually be used instead of zone-offset. Examples:
    Examples:

      => (zone-offset \"+02:00\")
      #<ZoneOffset +02:00>
      => (zone-offset :utc)
      #<ZoneOffset Z>
      => (zone-offset (hours -10))
      #<ZoneOffset -10:00>
      => (zone-offset (offset-time :now)) ; Current offset
      #<ZoneOffset +03:00>"))

(extend-protocol PZoneOffset
  guangyin.internal.types.IWrapper
  (zone-offset [this] (zone-offset @this))
  clojure.lang.Keyword
  (zone-offset [this] (wrap (fields/get-field fields/zone-offsets this)))
  java.time.Duration
  (zone-offset [this]
    (wrap (ZoneOffset/ofTotalSeconds (/ (.toMillis this) 1000))))
  java.time.temporal.TemporalAccessor
  (zone-offset [this] (wrap (ZoneOffset/from this)))
  java.time.ZoneOffset
  (zone-offset [this] (wrap this))
  java.lang.String
  (zone-offset [this] (wrap (ZoneOffset/of this)))
  java.lang.Long
  (zone-offset ([this] (wrap (ZoneOffset/ofHours this)))
               ([hours minutes]
                (wrap (ZoneOffset/ofHoursMinutes hours minutes)))))

(defn clock?
  "Returns true if the given value is a clock instance."
  [x] (instance? Clock (unwrap x)))

(defprotocol PClock
  (clock [this] [this param]
   "Creates a clock instance.
    Examples:

      => (clock :default) ; System default zone
      #<SystemClock SystemClock[Europe/Helsinki]>
      => (clock (zone-id \"Asia/Shanghai\"))
      #<SystemClock SystemClock[Asia/Shanghai]>
      => (clock (instant :now) (zone-id \"Asia/Shanghai\"))
      #<FixedClock FixedClock[2015-04-01T10:15:00.123Z,Asia/Shanghai]>
      => (clock (clock) (hours 3)) ; Clock 3 hours ahead
      #<OffsetClock OffsetClock[SystemClock[Europe/Helsinki],PT3H]>"))

(extend-protocol PClock
  guangyin.internal.types.IWrapper
  (clock ([this] (clock @this))
         ([this param] (clock @this param)))
  clojure.lang.Keyword
  (clock [this] (if (= this :default)
                    (wrap (Clock/systemDefaultZone))
                    (throw (IllegalArgumentException.
                             (str "Invalid key " this)))))
  java.time.ZoneId
  (clock [this] (wrap (Clock/system this)))
  java.time.Instant
  (clock [this param] (wrap (Clock/fixed this @(zone-id param))))
  java.time.Clock
  (clock ([this] (wrap this))
         ([this param] (wrap (Clock/offset this @(duration param))))))

(defn years
  "Create a period of given years."
  [years]
  (wrap (Period/ofYears years)))

(defn months
  "Create a period of given months."
  [months]
  (wrap (Period/ofMonths months)))

(defn weeks
  "Create a period of given weeks converted to days."
  [weeks]
  (wrap (Period/ofWeeks weeks)))

(defn days
  "Create a period of given days."
  [days]
  (wrap (Period/ofDays days)))

(defn hours
  "Create a duration of given hours."
  [hours]
  (wrap (Duration/ofHours hours)))

(defn minutes
  "Create a duration of given minutes."
  [minutes]
  (wrap (Duration/ofMinutes minutes)))

(defn seconds
  "Create a duration of given seconds."
  [seconds]
  (wrap (Duration/ofSeconds seconds)))

(defn millis
  "Create a duration of given milliseconds."
  [millis]
  (wrap (Duration/ofMillis millis)))

(defn nanos
  "Create a duration of given nanoseconds."
  [nanos]
  (wrap (Duration/ofNanos nanos)))

(defn- fplus
  ([x [key val]]
   (wrap (.plus (unwrap x) val (fields/get-field fields/all-units key))))
  ([x keyval & more]
   (reduce fplus (fplus x keyval) more)))

(defn plus
  "Add periods or durations to supported objects.
   Examples:

     => (plus (days 1) (months 4))
     #<Period P4M1D>
     => (plus (hours 1) (minutes 3))
     #<Duration PT1H3M>
     => (plus (local-date \"2015-04-01\") (days 5))
     #<LocalDate 2015-04-06>

   For non-ISO dates one can not add ISO periods, but adding single fields using
   field-value maps works. However, this does not work for ISO periods at the
   moment although durations and date-times are ok:

     => (plus (minguo-date :now) (days 5))
     DateTimeException Chronology mismatch, expected: ISO, actual: Minguo
       java.time.Period.validateChrono (Period.java:971)
     => (plus (minguo-date :now) {:days 5})
     #<MinguoDate Minguo ROC 104-04-6>
     => (plus (days 5) {:months 5})
     IllegalArgumentException No matching method found: plus for class
       java.time.Period  clojure.lang.Reflector.invokeMatchingMethod
       (Reflector.java:53)
     => (plus (hours 3) {:minutes 4 :seconds 5})
     #<Duration PT3H4M5S>
     => (plus (local-date-time \"2015-04-01T12:15:00\")
              {:days 1 :hours 3} {:minutes 5 :seconds 2})
     #<LocalDateTime 2015-04-02T15:20:02>"
  ([x] (wrap x))
  ([x y] (if (map? (unwrap y))
             (apply fplus x (seq (unwrap y)))
             (wrap (.plus (unwrap x) (unwrap y)))))
  ([x y & more]
   (reduce plus (plus x y) more)))

(defn- fminus
  ([x [key val]]
   (wrap (.minus (unwrap x) val (fields/get-field fields/all-units key))))
  ([x keyval & more]
   (reduce fminus (fminus x keyval) more)))

(defn minus
  "Add periods or durations to supported objects. For more information see the
   plus function, which works the same way.
   Examples:

     => (minus (days 1) (months 1))
     #<Period P-1M1D>
     => (minus (hours 3) (minutes 30))
     #<Duration PT2H30M>
     => (minus (local-date \"2015-04-01\") (days 3))
     #<LocalDate 2015-03-29>"
  ([x] (wrap x))
  ([x y] (if (map? (unwrap y))
             (apply fminus x (seq (unwrap y)))
             (wrap (.minus (unwrap x) (unwrap y)))))
  ([x y & more]
   (reduce minus (minus x y) more)))

(defn multiplied-by
  "Multiply a period or duration by constant.
   Examples:

     => (multiplied-by (plus (months 1) (days 2)) 3)
     #<Period P3M6D>"
  [x ^long multiplicand]
  (wrap (.multipliedBy (unwrap x) multiplicand)))

(defn divided-by
  "Divide a duration by constant.
   Examples:

     => (divide-by (hours 2) 2)
     #<Duration PT1H>"
  [x ^long divisor]
  (wrap (.dividedBy (unwrap x) divisor)))
