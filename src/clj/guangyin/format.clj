(ns guangyin.format
  "The format namespace for date and time formatting."
  (:require [guangyin.internal.fields :as fields]
            [guangyin.internal.types :refer :all])
  (:import (java.time.format DateTimeFormatter)))

(defn date-time-formatter?
  "Returns true if the given value is a date time formatter."
  [x] (instance? DateTimeFormatter (unwrap x)))

(defprotocol IDateTimeFormatter
  (date-time-formatter [this] [this param]))

(extend-protocol IDateTimeFormatter
  guangyin.internal.types.ObjectWrapper
  (date-time-formatter [this] (date-time-formatter @this))
  (date-time-formatter [this param] (date-time-formatter @this param))
  clojure.lang.Keyword
  (date-time-formatter [this] (wrap (fields/date-time-formatters this)))
  java.time.format.DateTimeFormatter
  (date-time-formatter [this] (wrap this))
  java.lang.String
  (date-time-formatter ([this] (DateTimeFormatter/ofPattern this))
                       ([this param] (DateTimeFormatter/ofPattern this param))))
