(ns guangyin.core
  (:require [guangyin.internal.fields :refer :all]
            [guangyin.internal.utils :refer :all])
  (:import (java.time Clock Duration Instant LocalDate LocalDateTime LocalTime
                      MonthDay OffsetDateTime OffsetTime Period Year YearMonth
                      ZonedDateTime ZoneId ZoneOffset DayOfWeek Month)
           (java.time.format DateTimeFormatter)))

(defn instant?
  [x] (instance? Instant x))

(defn duration?
  [x] (instance? Duration x))

(defn period?
  [x] (instance? Period x))

(defn local-date?
  [x] (instance? LocalDate x))

(defn local-time?
  [x] (instance? LocalTime x))

(defn offset-time?
  [x] (instance? OffsetTime x))

(defn local-date-time?
  [x] (instance? LocalDateTime x))

(defn offset-date-time?
  [x] (instance? OffsetDateTime x))

(defn zoned-date-time?
  [x] (instance? ZonedDateTime x))

(defn year?
  [x] (instance? Year x))

(defn year-month?
  [x] (instance? YearMonth x))

(defn month?
  [x] (instance? Month x))

(defn month-day?
  [x] (instance? MonthDay x))

(defn day-of-week?
  [x] (instance? DayOfWeek x))

(defn zone-id?
  [x] (instance? ZoneId x))

(defn zone-offset?
  [x] (instance? ZoneOffset x))

(defn clock?
  [x] (instance? Clock x))

(defn date-time-formatter?
  [x] (instance? DateTimeFormatter x))

(defn zone-offset
  ([x]
   (pred-cond-throw x (str "Invalid zone-offset: " x)
     zone-offset? x
     string? (ZoneOffset/of x)
     keyword? (zone-offset-keywords x)
     duration? (ZoneOffset/ofTotalSeconds (/ (.toMillis x) 1000))
     :else (ZoneOffset/from x))))

(defn zone-id
  ([]
   (ZoneId/systemDefault))
  ([x]
   (pred-cond-throw x (str "Invalid zone-id: " x)
     zone-id? x
     string? (ZoneId/of x)
     :else (ZoneId/from x)))
  ([prefix offset]
   (ZoneId/ofOffset prefix offset)))

(defn instant
  ([]
   (Instant/now))
  ([x]
   (pred-cond-throw x (str "Invalid instant: " x)
     clock? (Instant/now x)
     string? (Instant/parse x)
     keyword? (instant-keywords x)
     :else (Instant/from x))))

(defn duration
  ([x]
   (if (string? x)
       (Duration/parse x)
       (Duration/from x)))
  ([start-inclusive end-exclusive]
   (Duration/between start-inclusive end-exclusive)))

(defn period
  ([x]
   (pred-cond-throw x (str "Invalid period: " x)
     period? x
     string? (Period/parse x)
     keyword? (period-keywords x)
     :else (Period/from x)))
  ([start end]
   (Period/between start end))
  ([years months days]
   (Period/of years months days)))

(defn local-date
  ([]
   (LocalDate/now))
  ([x]
   (pred-cond-throw x (str "Invalid local-date: " x)
     local-date? x
     clock? (LocalDate/now x)
     zone-id? (LocalDate/now x)
     string? (LocalDate/parse x)
     keyword? (local-date-keywords x)
     :else (LocalDate/from x)))
  ([text formatter]
   (LocalDate/parse text formatter))
  ([year month day-of-month]
   (LocalDate/of year month day-of-month)))

(defn local-time
  ([]
   (LocalTime/now))
  ([x]
   (pred-cond-throw x (str "Invalid local-time: " x)
     local-time? x
     clock? (LocalTime/now x)
     zone-id? (LocalTime/now x)
     string? (LocalTime/parse x)
     keyword? (local-time-keywords x)
     :else (LocalTime/from x)))
  ([text formatter]
   (LocalTime/parse text formatter)))

(defn offset-time
  ([]
   (OffsetTime/now))
  ([x]
   (pred-cond-throw x (str "Invalid offset-time: " x)
     offset-time? x
     clock? (OffsetTime/now x)
     zone-id? (OffsetTime/now x)
     string? (OffsetTime/parse x)
     keyword? (offset-time-keywords x)
     :else (OffsetTime/from x)))
  ([time-or-instant-or-text offset-or-zone-or-formatter]
   (pred-cond time-or-instant-or-text
     local-time? (OffsetTime/of
                   time-or-instant-or-text
                   (zone-offset offset-or-zone-or-formatter))
     instant? (OffsetTime/ofInstant
                time-or-instant-or-text
                (zone-id offset-or-zone-or-formatter))
     :else (OffsetTime/parse
             time-or-instant-or-text
             offset-or-zone-or-formatter)))
  ([hour minute second nano-of-second offset]
   (OffsetTime/of hour minute second nano-of-second offset)))

(defn local-date-time
  ([]
   (LocalDateTime/now))
  ([x]
   (pred-cond-throw x (str "Invalid local-date-time: " x)
     local-date-time? x
     clock? (LocalDateTime/now x)
     zone-id? (LocalDateTime/now x)
     string? (LocalDateTime/parse x)
     keyword? (local-date-time-keywords x)
     :else (LocalDateTime/from x)))
  ([date-or-instant-or-text time-or-zone-or-formatter]
   (pred-cond date-or-instant-or-text
     local-date? (LocalDateTime/of
                   date-or-instant-or-text
                   time-or-zone-or-formatter)
     instant? (LocalDateTime/ofInstant
                date-or-instant-or-text
                (zone-id time-or-zone-or-formatter))
     :else (LocalDateTime/parse
             date-or-instant-or-text
             time-or-zone-or-formatter)))
  ([year month day-of-month hour minute]
   (LocalDateTime/of year month day-of-month hour minute))
  ([year month day-of-month hour minute second]
   (LocalDateTime/of year month day-of-month hour minute second))
  ([year month day-of-month hour minute second nano-of-second]
   (LocalDateTime/of year month day-of-month hour minute second
                     nano-of-second)))

(defn offset-date-time
  ([]
   (OffsetDateTime/now))
  ([x]
   (pred-cond-throw x (str "Invalid offset-date-time: " x)
     offset-date-time? x
     clock? (OffsetDateTime/now x)
     zone-id? (OffsetDateTime/now x)
     string? (OffsetDateTime/parse x)
     keyword? (offset-date-time-keywords x)
     :else (OffsetDateTime/from x)))
  ([date-time-or-instant-or-text offset-or-zone-or-formatter]
   (pred-cond date-time-or-instant-or-text
     local-date-time? (OffsetDateTime/of
                        date-time-or-instant-or-text
                        (zone-offset offset-or-zone-or-formatter))
     instant? (OffsetDateTime/ofInstant
                date-time-or-instant-or-text
                (zone-id offset-or-zone-or-formatter))
     :else (OffsetDateTime/parse
             date-time-or-instant-or-text
             offset-or-zone-or-formatter)))
  ([date time offset]
   (OffsetDateTime/of date time offset))
  ([year month day-of-month hour minute second nano-of-second offset]
   (OffsetDateTime/of year month day-of-month hour minute second nano-of-second
                      offset)))

(defn zoned-date-time
  ([]
   (ZonedDateTime/now))
  ([x]
   (pred-cond-throw x (str "Invalid zoned-date-time: " x)
     zoned-date-time? x
     clock? (ZonedDateTime/now x)
     zone-id? (ZonedDateTime/now x)
     string? (ZonedDateTime/parse x)
     :else (ZonedDateTime/from x)))
  ([date-time-or-instant-or-text zone-or-formatter]
   (pred-cond date-time-or-instant-or-text
     local-date-time? (ZonedDateTime/of
                        date-time-or-instant-or-text
                        (zone-id zone-or-formatter))
     instant? (ZonedDateTime/ofInstant
                date-time-or-instant-or-text
                (zone-id zone-or-formatter))
     :else (ZonedDateTime/parse
             date-time-or-instant-or-text
             zone-or-formatter)))
  ([local-date-time zone preferred-zone]
   (ZonedDateTime/ofLocal local-date-time zone preferred-zone))
  ([year month day-of-month hour minute second nano-of-second zone]
   (ZonedDateTime/of year month day-of-month hour minute second nano-of-second
                     zone)))

(defn year
  ([]
   (Year/now))
  ([x]
   (pred-cond-throw x (str "Invalid year: " x)
     year? x
     clock? (Year/now x)
     zone-id? (Year/now x)
     integer? (Year/of x)
     string? (Year/parse x)
     keyword? (year-keywords x)
     :else (Year/from x)))
  ([text formatter]
   (Year/parse text formatter)))

(defn year-month
  ([]
   (YearMonth/now))
  ([x]
   (pred-cond x
     year-month? x
     clock? (YearMonth/now x)
     zone-id? (YearMonth/now x)
     string? (YearMonth/parse x)
     :else (YearMonth/from x)))
  ([year-or-text month-or-formatter]
   (if (integer? year-or-text)
       (YearMonth/of year-or-text month-or-formatter)
       (YearMonth/parse year-or-text month-or-formatter))))

(defn get-field
  [accessor field]
  (.get accessor (chrono-field-keywords field)))

(defn month
  ([]
   (month (get-field (local-date) :month-of-year)))
  ([x]
   (pred-cond-throw x (str "Invalid month: " x)
     month? x
     integer? (Month/of x)
     keyword? (month-keywords x)
     :else (Month/of (get-field (local-date x) :month-of-year)))))

(defn month-day
  ([]
   (MonthDay/now))
  ([x]
   (pred-cond x
     month-day? x
     clock? (MonthDay/now x)
     zone-id? (MonthDay/now x)
     string? (MonthDay/parse x)
     :else (MonthDay/from x)))
  ([month-or-text day-or-formatter]
   (if (integer? month-or-text)
       (MonthDay/of month-or-text day-or-formatter)
       (MonthDay/parse month-or-text day-or-formatter))))

(defn day-of-week
  ([]
   (day-of-week (get-field (local-date) :day-of-week)))
  ([x]
   (pred-cond-throw x (str "Invalid day-of-week: " x)
     day-of-week? x
     integer? (DayOfWeek/of x)
     keyword? (day-of-week-keywords x)
     :else (DayOfWeek/of (get-field (local-date x) :day-of-week)))))

(defn clock
  ([]
   (Clock/systemDefaultZone))
  ([x]
   (Clock/system (zone-id x)))
  ([instant-or-clock zone-or-duration]
   (if (instant? instant-or-clock) 
       (Clock/fixed instant-or-clock (zone-id zone-or-duration))
       (Clock/offset instant-or-clock (duration zone-or-duration)))))

(defn date-time-formatter
  ([x]
   (pred-cond-throw x (str "Invalid format: " x)
     date-time-formatter? x
     string? (DateTimeFormatter/ofPattern x)
     keyword? (date-time-formatter-keywords x)))
  ([pattern-or-type locale-or-style]
   (if (string? pattern-or-type)
       (DateTimeFormatter/ofPattern pattern-or-type locale-or-style)
       (let [f (format-style-keywords locale-or-style)]
         (when (nil? f)
           (throw (IllegalArgumentException.
                    (str "Invalid format style: " locale-or-style))))
         (case pattern-or-type
           :date (DateTimeFormatter/ofLocalizedDate f)
           :time (DateTimeFormatter/ofLocalizedTime f)
           :date-time (DateTimeFormatter/ofLocalizedDateTime f)
           (throw (IllegalArgumentException.
                    (str "Invalid format type: " pattern-or-type)))))))
  ([type date-style time-style]
   (let [datef (format-style-keywords date-style)
         timef (format-style-keywords time-style)]
     (when-not (= type :date-time)
       (throw (IllegalArgumentException.
                (str "Invalid format type: " type)))
     (when (nil? datef)
       (throw (IllegalArgumentException.
                (str "Invalid format style: " date-style))))
     (when (nil? timef)
       (throw (IllegalArgumentException.
                (str "Invalid format style: " time-style))))
     (DateTimeFormatter/ofLocalizedDateTime date-style time-style)))))

(defn years
  [years]
  (Period/ofYears years))

(defn months
  [months]
  (Period/ofMonths months))

(defn weeks
  [weeks]
  (Period/ofWeeks weeks))

(defn days
  [days]
  (Period/ofDays days))

(defn hours
  [hours]
  (Duration/ofHours hours))

(defn minutes
  [minutes]
  (Duration/ofMinutes minutes))

(defn seconds
  [seconds]
  (Duration/ofSeconds seconds))

(defn nanos
  [nanos]
  (Duration/ofNanos nanos))

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

