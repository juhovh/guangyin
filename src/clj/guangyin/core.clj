(ns guangyin.core
  "The core namespace for basic date and time handling."
  (:require [guangyin.internal.fields :as fields]
            [guangyin.internal.types :refer :all]
            [guangyin.internal.utils :refer :all])
  (:import (java.time Clock Duration Instant LocalDate LocalDateTime LocalTime
                      MonthDay OffsetDateTime OffsetTime Period Year YearMonth
                      ZonedDateTime ZoneId ZoneOffset DayOfWeek Month)))

(defprotocol IInstant
  (instant? [this]
   "Returns true if the given value is an instant.
    Examples:

     => (instant? (instant))
     true
     => (instant? (days 1))
     false")
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
      => (instant (offset-date-time)) ; From date-time containing instant
      #<Instant 2015-01-01T12:15:00.123Z>"))

(extend-protocol IInstant
  guangyin.internal.types.ObjectWrapper
  (instant? [this] (instant? @this))
  (instant [this] (instant @this))
  clojure.lang.Keyword
  (instant? [this] false)
  (instant [this] (if (= this :now)
                      (wrap (Instant/now))
                      (wrap (fields/instants this))))
  java.time.temporal.TemporalAccessor
  (instant? [this] (instance? Instant this))
  (instant [this] (wrap (Instant/from this)))
  java.time.Clock
  (instant? [this] false)
  (instant [this] (wrap (Instant/now this)))
  java.lang.String
  (instant? [this] false)
  (instant [this] (wrap (Instant/parse this)))
  java.lang.Object
  (instant? [this] false))

(defprotocol IDuration
  (duration? [this]
   "Returns true if the given value is a duration.
    Duration refers to an exact length of time that can be converted accurately
    to seconds without any time zone information, basically any length of time
    that is measured in hours or smaller units than that.
    Examples:

      => (duration? (hours 1))
      true
      => (duration? (days 1))
      false")
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

(extend-protocol IDuration
  guangyin.internal.types.ObjectWrapper
  (duration? [this] (duration? @this))
  (duration [this] (duration @this))
  (duration [this other] (duration @this (unwrap other)))
  clojure.lang.Keyword
  (duration? [this] false)
  (duration [this] (wrap (fields/durations this)))
  java.time.temporal.Temporal
  (duration? [this] false)
  (duration [this other] (wrap (Duration/between this other)))
  java.time.temporal.TemporalAmount
  (duration? [this] (instance? Duration this))
  (duration [this] (wrap (Duration/from this)))
  java.lang.String
  (duration? [this] false)
  (duration [this] (wrap (Duration/parse this)))
  java.lang.Object
  (duration? [this] false))

(defprotocol IPeriod
  (period? [this]
   "Returns true if the given value is a period.
    Period refers to a length of time that can not be converted accurately to
    seconds without any time zone information, basically any length of time that
    is measured in days or larger units than that.
    Examples:

      => (period? (weeks 1))
      true
      => (period? (minutes 30))
      false")
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
  
(extend-protocol IPeriod
  guangyin.internal.types.ObjectWrapper
  (period? [this] (period? @this))
  (period [this] (period @this))
  (period [this other] (period @this (unwrap other)))
  clojure.lang.Keyword
  (period? [this] false)
  (period [this] (wrap (fields/periods this)))
  java.time.temporal.TemporalAmount
  (period? [this] (instance? Period this))
  (period [this] (wrap (Period/from this)))
  java.time.LocalDate
  (period? [this] false)
  (period [this other] (wrap (Period/between this (unwrap other))))
  java.lang.String
  (period? [this] false)
  (period [this] (wrap (Period/parse this)))
  java.lang.Object
  (period? [this] false))

(defn local-date?
  "Returns true if the given value is a local date."
  [x] (wrapped-instance? LocalDate x))

(defn local-time?
  "Returns true if the given value is a local time."
  [x] (wrapped-instance? LocalTime x))

(defn offset-time?
  "Returns true if the given value is a time with a zone offset."
  [x] (wrapped-instance? OffsetTime x))

(defn local-date-time?
  "Returns true if the given value is a local date-time."
  [x] (wrapped-instance? LocalDateTime x))

(defn offset-date-time?
  "Returns true if the given value is a date-time with a zone offset."
  [x] (wrapped-instance? OffsetDateTime x))

(defn zoned-date-time?
  "Returns true if the given value is a date-time with a time zone."
  [x] (wrapped-instance? ZonedDateTime x))

(defn year?
  "Returns true if the given value is an exact year."
  [x] (wrapped-instance? Year x))

(defn year-month?
  "Returns true if the given value is a combination of year and month."
  [x] (wrapped-instance? YearMonth x))

(defn month?
  "Returns true if the given value is an exact month.
   Examples:

     => (month? (month 5))
     true
     => (month? (months 2))
     false"
  [x] (wrapped-instance? Month x))

(defn month-day?
  "Returns true if the given value is a combination of month and day of month."
  [x] (wrapped-instance? MonthDay x))

(defn day-of-week?
  "Returns true if the given value is an exact day of week.
   Examples:

     => (day-of-week? (day-of-week :saturday))
     true
     => (day-of-week? (days 5))
     false"
  [x] (wrapped-instance? DayOfWeek x))

(defn zone-id?
  "Returns true if the given value is a time zone.
   Examples:

     => (zone-id? (zone-id \"Europe/Helsinki\"))
     true
     => (zone-id? (zone-offset :utc))
     true
     => (zone-id? (hours 1))
     false"
  [x] (wrapped-instance? ZoneId x))

(defn zone-offset?
  "Returns true if the given value is a zone offset.
   Examples:

     => (zone-offset? (zone-offset :utc))
     true
     => (zone-offset? (zone-id \"Europe/Helsinki\"))
     false"
  [x] (wrapped-instance? ZoneOffset x))

(defn clock?
  "Returns true if the given value is a clock instance."
  [x] (wrapped-instance? Clock x))

(defn zone-offset
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
     => (zone-offset (offset-time)) ; Current offset
     #<ZoneOffset +03:00>"
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid zone-offset: " x)
       zone-offset? x
       string? (ZoneOffset/of x)
       keyword? (fields/zone-offsets x)
       duration? (ZoneOffset/ofTotalSeconds (/ (.toMillis x) 1000))
       :else (ZoneOffset/from x)))))

(defn zone-id
  "Create zone-id from values.
   If created from prefix and offset, the valid prefix values are \"GMT\",
   \"UTC\", \"UT\" and \"\". This is usually not very useful and should be
   avoided unless necessary.
   Examples:

     => (zone-id) ; Current zone
     #<ZoneRegion Europe/Helsinki>
     => (zone-id \"America/New_York\")
     #<ZoneRegion America/New_York>
     => (zone-id (zoned-date-time)) ; Zone from date-time
     #<ZoneRegion Europe/Helsinki>
     => (zone-id \"UTC\" (zone-offset \"+08:00\")) ; For completeness
     #<ZoneRegion UTC+08:00>"
  ([]
   (wrap
     (ZoneId/systemDefault)))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid zone-id: " x)
       zone-id? x
       string? (ZoneId/of x)
       :else (ZoneId/from x))))
  ([prefix offset]
   (wrap
     (ZoneId/ofOffset prefix offset))))

(defn local-date
  "Coerce to local date.
   Can also be used to parse a local date using a custom formatter, or to create
   a date from year, month and day of month.
   Examples:

     => (local-date) ; Current date
     #<LocalDate 2015-04-01>
     => (local-date (clock))
     #<LocalDate 2015-04-01>
     => (local-date (zone-id \"Asia/Shanghai\")) ; Current date in Shanghai
     #<LocalDate 2015-04-02>
     => (local-date \"2015-04-01\")
     #<LocalDate 2015-04-01>
     => (local-date :max) ; Largest local date, also :min supported
     #<LocalDate +999999999-12-31>
     => (local-date (offset-date-time))
     #<LocalDate 2015-04-01>
     => (local-date \"01.04.2015\" (date-time-formatter \"dd.MM.yyyy\"))
     #<LocalDate 2015-04-01>
     => (local-date 2015 4 1)
     #<LocalDate 2015-04-01>"
  ([]
   (wrap
     (LocalDate/now)))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid local-date: " x)
       local-date? x
       clock? (LocalDate/now x)
       zone-id? (LocalDate/now x)
       string? (LocalDate/parse x)
       keyword? (fields/local-dates x)
       :else (LocalDate/from x))))
  ([text formatter]
   (wrap
     (LocalDate/parse text formatter)))
  ([year month day-of-month]
   (wrap
     (LocalDate/of year month day-of-month))))

(defn local-time
  "Coerce to local time.
   Can also be used to parse a local time using a custom formatter, or to create
   a time from hours, minutes, seconds and nanoseconds.
   Examples:

     => (local-time) ; Current time
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
     => (local-time (local-date-time)) ; Time part of local date-time
     #<LocalTime 12:15:00.123>
     => (local-time \"12.15\" (date-time-formatter \"HH.mm\"))
     #<LocalTime 12:15>
     => (local-time 12 15)
     #<LocalTime 12:15>
     => (local-time 12 15 0)
     #<LocalTime 12:15>
     => (local-time 12 15 0 123000000)
     #<LocalTime 12:15:00.123>"
  ([]
   (wrap
     (LocalTime/now)))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid local-time: " x)
       local-time? x
       clock? (LocalTime/now x)
       zone-id? (LocalTime/now x)
       string? (LocalTime/parse x)
       keyword? (fields/local-times x)
       :else (LocalTime/from x))))
  ([hour-or-text minute-or-formatter]
   (wrap
     (pred-cond hour-or-text
       integer? (LocalTime/of hour-or-text minute-or-formatter)
       :else (LocalTime/parse hour-or-text minute-or-formatter))))
  ([hour minute second]
   (wrap
     (LocalTime/of hour minute second)))
  ([hour minute second nano-of-second]
   (wrap
     (LocalTime/of hour minute second nano-of-second))))

(defn offset-time
  "Coerce to time with offset.
   Can also be used to parse a time with offset using a custom formatter, or to
   create a time with offset from hours, minutes, seconds and nanoseconds.
   Examples:

     => (offset-time) ; Current time with offset
     #<OffsetTime 12:15:00.123+03:00>
     => (offset-time (clock))
     #<OffsetTime 12:15:00.123+03:00>
     => (offset-time (zone-id \"Asia/Shanghai\"))
     #<OffsetTime 17:15:00.123+08:00>
     => (offset-time \"12:15:00.123+03:00\")
     #<OffsetTime 12:15:00.123+03:00>
     => (offset-time :max)
     #<OffsetTime 23:59:59.999999999-18:00>
     => (offset-time (offset-date-time))
     #<OffsetTime 12:15:00.123+03:00>
     => (offset-time (local-time 12 15) (zone-offset (hours 3)))
     #<OffsetTime 12:15+03:00>
     => (offset-time (instant) (zone-id \"Europe/Helsinki\"))
     #<OffsetTime 12:15:00.123+03:00>
     => (offset-time \"12.15+0300\" (date-time-formatter \"HH.mmx\"))
     #<OffsetTime 12:15+03:00>
     => (offset-time 12 15 0 123000000 (zone-offset (hours 3)))
     #<OffsetTime 12:15:00.123+03:00>"
  ([]
   (wrap
     (OffsetTime/now)))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid offset-time: " x)
       offset-time? x
       clock? (OffsetTime/now x)
       zone-id? (OffsetTime/now x)
       string? (OffsetTime/parse x)
       keyword? (fields/offset-times x)
       :else (OffsetTime/from x))))
  ([time-or-instant-or-text offset-or-zone-or-formatter]
   (wrap
     (pred-cond time-or-instant-or-text
       local-time? (OffsetTime/of
                     time-or-instant-or-text
                     (zone-offset offset-or-zone-or-formatter))
       instant? (OffsetTime/ofInstant
                  time-or-instant-or-text
                  (zone-id offset-or-zone-or-formatter))
       :else (OffsetTime/parse
               time-or-instant-or-text
               offset-or-zone-or-formatter))))
  ([hour minute second nano-of-second offset]
   (wrap
     (OffsetTime/of hour minute second nano-of-second (zone-offset offset)))))

(defn local-date-time
  ([]
   (wrap
     (LocalDateTime/now)))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid local-date-time: " x)
       local-date-time? x
       clock? (LocalDateTime/now x)
       zone-id? (LocalDateTime/now x)
       string? (LocalDateTime/parse x)
       keyword? (fields/local-date-times x)
       :else (LocalDateTime/from x))))
  ([date-or-instant-or-text time-or-zone-or-formatter]
   (wrap
     (pred-cond date-or-instant-or-text
       local-date? (LocalDateTime/of
                     date-or-instant-or-text
                     time-or-zone-or-formatter)
       instant? (LocalDateTime/ofInstant
                  date-or-instant-or-text
                  (zone-id time-or-zone-or-formatter))
       :else (LocalDateTime/parse
               date-or-instant-or-text
               time-or-zone-or-formatter))))
  ([year month day-of-month hour minute]
   (wrap
     (LocalDateTime/of year month day-of-month hour minute)))
  ([year month day-of-month hour minute second]
   (wrap
     (LocalDateTime/of year month day-of-month hour minute second)))
  ([year month day-of-month hour minute second nano-of-second]
   (wrap
     (LocalDateTime/of year month day-of-month hour minute second
                       nano-of-second))))

(defn offset-date-time
  ([]
   (wrap
     (OffsetDateTime/now)))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid offset-date-time: " x)
       offset-date-time? x
       clock? (OffsetDateTime/now x)
       zone-id? (OffsetDateTime/now x)
       string? (OffsetDateTime/parse x)
       keyword? (fields/offset-date-times x)
       :else (OffsetDateTime/from x))))
  ([date-time-or-instant-or-text offset-or-zone-or-formatter]
   (wrap
     (pred-cond date-time-or-instant-or-text
       local-date-time? (OffsetDateTime/of
                          date-time-or-instant-or-text
                          (zone-offset offset-or-zone-or-formatter))
       instant? (OffsetDateTime/ofInstant
                  date-time-or-instant-or-text
                  (zone-id offset-or-zone-or-formatter))
       :else (OffsetDateTime/parse
               date-time-or-instant-or-text
               offset-or-zone-or-formatter))))
  ([date time offset]
   (wrap
     (OffsetDateTime/of date time offset)))
  ([year month day-of-month hour minute second nano-of-second offset]
   (wrap
     (OffsetDateTime/of year month day-of-month hour minute second
                        nano-of-second offset))))

(defn zoned-date-time
  ([]
   (wrap
     (ZonedDateTime/now)))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid zoned-date-time: " x)
       zoned-date-time? x
       clock? (ZonedDateTime/now x)
       zone-id? (ZonedDateTime/now x)
       string? (ZonedDateTime/parse x)
       :else (ZonedDateTime/from x))))
  ([date-time-or-instant-or-text zone-or-formatter]
   (wrap
     (pred-cond date-time-or-instant-or-text
       local-date-time? (ZonedDateTime/of
                          date-time-or-instant-or-text
                          (zone-id zone-or-formatter))
       instant? (ZonedDateTime/ofInstant
                  date-time-or-instant-or-text
                  (zone-id zone-or-formatter))
       :else (ZonedDateTime/parse
               date-time-or-instant-or-text
               zone-or-formatter))))
  ([local-date-time zone preferred-zone]
   (ZonedDateTime/ofLocal local-date-time zone preferred-zone))
  ([year month day-of-month hour minute second nano-of-second zone]
   (ZonedDateTime/of year month day-of-month hour minute second nano-of-second
                     zone)))

(defn year
  ([]
   (wrap
     (Year/now)))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid year: " x)
       year? x
       clock? (Year/now x)
       zone-id? (Year/now x)
       integer? (Year/of x)
       string? (Year/parse x)
       keyword? (fields/years x)
       :else (Year/from x))))
  ([text formatter]
   (wrap
     (Year/parse text formatter))))

(defn year-month
  ([]
   (wrap
     (YearMonth/now)))
  ([x]
   (wrap
     (pred-cond x
       year-month? x
       clock? (YearMonth/now x)
       zone-id? (YearMonth/now x)
       string? (YearMonth/parse x)
       :else (YearMonth/from x))))
  ([year-or-text month-or-formatter]
   (wrap
     (if (integer? year-or-text)
         (YearMonth/of year-or-text month-or-formatter)
         (YearMonth/parse year-or-text month-or-formatter)))))

(defn month
  ([]
   (month (:month-of-year (local-date))))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid month: " x)
       month? x
       integer? (Month/of x)
       keyword? (fields/months x)
       :else (Month/of (:month-of-year (local-date x)))))))

(defn month-day
  ([]
   (wrap
     (MonthDay/now)))
  ([x]
   (wrap
     (pred-cond x
       month-day? x
       clock? (MonthDay/now x)
       zone-id? (MonthDay/now x)
       string? (MonthDay/parse x)
       :else (MonthDay/from x))))
  ([month-or-text day-or-formatter]
   (wrap
     (if (integer? month-or-text)
         (MonthDay/of month-or-text day-or-formatter)
         (MonthDay/parse month-or-text day-or-formatter)))))

(defn day-of-week
  ([]
   (day-of-week (:day-of-week (local-date))))
  ([x]
   (wrap
     (pred-cond-throw x (str "Invalid day-of-week: " x)
       day-of-week? x
       integer? (DayOfWeek/of x)
       keyword? (fields/day-of-weeks x)
       :else (DayOfWeek/of (:day-of-week (local-date x)))))))

(defn clock
  ([]
   (wrap
     (Clock/systemDefaultZone)))
  ([x]
   (wrap
     (Clock/system (zone-id x))))
  ([instant-or-clock zone-or-duration]
   (wrap
     (if (instant? instant-or-clock) 
         (Clock/fixed instant-or-clock (zone-id zone-or-duration))
         (Clock/offset instant-or-clock (duration zone-or-duration))))))

(defn years
  [years]
  (wrap
    (Period/ofYears years)))

(defn months
  [months]
  (wrap
    (Period/ofMonths months)))

(defn weeks
  [weeks]
  (wrap
    (Period/ofWeeks weeks)))

(defn days
  [days]
  (wrap
    (Period/ofDays days)))

(defn hours
  [hours]
  (wrap
    (Duration/ofHours hours)))

(defn minutes
  [minutes]
  (wrap
    (Duration/ofMinutes minutes)))

(defn seconds
  [seconds]
  (wrap
    (Duration/ofSeconds seconds)))

(defn nanos
  [nanos]
  (wrap
    (Duration/ofNanos nanos)))

(defn plus
  ([] 0)
  ([x] x)
  ([x y] (.plus x y))
  ([x y & more]
   (reduce plus (.plus x y) more)))

(defn minus
  ([] 0)
  ([x] x)
  ([x y] (.add x y))
  ([x y & more]
   (reduce minus (.minus x y) more)))

