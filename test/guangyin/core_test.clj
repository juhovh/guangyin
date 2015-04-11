(ns guangyin.core-test
  (:require [clojure.test :refer :all]
            [guangyin.core :refer :all])
  (:import (java.time Clock ZoneOffset)
           (java.time.format DateTimeFormatter)))

(defn date-time-tests
  ([type? type]
   (is (type? (type)))
   (is (type? (type (Clock/systemUTC))))
   (is (type? (type ZoneOffset/UTC))))
  ([type? type strval]
   (date-time-tests type? type)
   (is (type? (type :min)))
   (is (type? (type :max)))
   (is (type? (type strval)))
   (is (type? (type "2011-12-03T10:15:30+01:00[Europe/Paris]" DateTimeFormatter/ISO_DATE_TIME)))))

(defn new-date-time-tests
  ([type? type]
   (is (type? (type :now)))
   (is (type? (type (Clock/systemUTC))))
   (is (type? (type ZoneOffset/UTC))))
  ([type? type strval]
   (new-date-time-tests type? type)
   (is (type? (type :min)))
   (is (type? (type :max)))
   (is (type? (type strval)))
   (is (type? (type "2011-12-03T10:15:30+01:00[Europe/Paris]" DateTimeFormatter/ISO_DATE_TIME)))))

(deftest test-local-date
  (new-date-time-tests local-date? local-date "2011-12-03"))

(deftest test-local-time
  (new-date-time-tests local-time? local-time "10:15:30"))

(deftest test-offset-time
  (date-time-tests offset-time? offset-time "10:15:30+01:00"))

(deftest test-local-date-time
  (date-time-tests local-date-time? local-date-time "2011-12-03T10:15:30"))

(deftest test-offset-date-time
  (date-time-tests offset-date-time? offset-date-time "2011-12-03T10:15:30+01:00"))

(deftest test-zoned-date-time
  (date-time-tests zoned-date-time? zoned-date-time)
  (is (zoned-date-time? (zoned-date-time "2011-12-03T10:15:30+01:00[Europe/Paris]")))
  (is (zoned-date-time? (zoned-date-time "2011-12-03T10:15:30+01:00[Europe/Paris]" DateTimeFormatter/ISO_DATE_TIME))))
  

(deftest test-year
  (date-time-tests year? year))

(deftest test-year-month
  (date-time-tests year-month? year-month))

(deftest test-month
  (date-time-tests month? month))

(deftest test-month-day
  (date-time-tests month-day? month-day))

(deftest test-day-of-week
  (date-time-tests day-of-week? day-of-week))
