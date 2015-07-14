(ns guangyin.java
  "The java namespace for legacy Java date and time objects."
  (:require [guangyin.core :refer :all]))

(defn util-date
  [& args]
  (java.sql.Timestamp.
    (.toEpochMilli
      @(apply instant args))))

(defn sql-timestamp
  [& args]
  (java.sql.Timestamp.
    (.toEpochMilli
      @(apply instant args))))

(defn sql-date
  [& args]
  (java.sql.Date.
    (.toEpochMilli
      @(instant
         (offset-date-time
           (apply local-date args)
           (local-time 0 0)
           (zone-offset :utc))))))

(defn sql-time
  [& args]
  (java.sql.Time.
    (.toEpochMilli
      @(instant
         (offset-date-time
           (local-date 1970 1 1)
           (apply local-time args)
           (zone-offset :utc))))))

(extend-protocol PInstant
  java.util.Date
  (instant [this] (instant (java.time.Instant/ofEpochMilli (.getTime this)))))

(extend-protocol PLocalDate
  java.sql.Date
  (local-date [this] (local-date (offset-date-time (instant this) (zone-offset :utc)))))

(extend-protocol PLocalTime
  java.sql.Time
  (local-time [this] (local-time (offset-date-time (instant this) (zone-offset :utc)))))
