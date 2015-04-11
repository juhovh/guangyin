(ns guangyin.format
  "The format namespace for date and time formatting."
  (:require [guangyin.internal.fields :as fields]
            [guangyin.internal.utils :refer :all])
  (:import (java.time.format DateTimeFormatter)))

(defn date-time-formatter?
  "Returns true if the given value is a date time formatter."
  [x] (instance? DateTimeFormatter x))

(defn date-time-formatter
  ([x]
   (pred-cond-throw x (str "Invalid format: " x)
     date-time-formatter? x
     string? (DateTimeFormatter/ofPattern x)
     keyword? (fields/date-time-formatters x)))
  ([pattern-or-type locale-or-style]
   (if (string? pattern-or-type)
       (DateTimeFormatter/ofPattern pattern-or-type locale-or-style)
       (let [f (fields/format-styles locale-or-style)]
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
   (let [datef (fields/format-styles date-style)
         timef (fields/format-styles time-style)]
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